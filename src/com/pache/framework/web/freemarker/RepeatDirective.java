package com.pache.framework.web.freemarker;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * 自定义宏：
 * FreeMarker 自定义标签实现重复输出内容体。
 * 参数: count: 重复的次数，必须的且非负整数。 hr: 设置是否输出HTML标签 "hr" 元素. Boolean. 可选的默认为false
 * 循环变量: 只有一个，可选的. 从1开始。
 */
public class RepeatDirective implements TemplateDirectiveModel {

    private static final String PARAM_NAME_COUNT = "count";
    private static final String PARAM_NAME_HR = "hr";

    public RepeatDirective(){}

    /**
     * 自定义的宏，是以map的形式将接收参数
     * @param environment
     * @param paramsMap
     * @param loopVars
     * @param body
     * @throws TemplateException
     * @throws IOException
     */
    public void execute(Environment env, Map paramsMap, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        int countParam = 0;
        boolean countParamSet = false;
        boolean hrParam = false;
        //根据用户定义，给上面的局部变量重新赋值
        Iterator paramIter = paramsMap.entrySet().iterator();
        while(paramIter.hasNext()) {
            Map.Entry ent = (Map.Entry) paramIter.next();
            String paramName = (String)ent.getKey();   //获取key
            TemplateModel paramValue = (TemplateModel) ent.getValue(); //获取key对应的值
            if("count".equals(paramName)){
                if(!(paramValue instanceof TemplateNumberModel)){
                    throw  new TemplateModelException("The \"hr\" count parameter must be a number");
                }
                countParam = ((TemplateNumberModel)paramValue).getAsNumber().intValue();
                countParamSet = true;
                if(countParam < 0){
                    throw new TemplateModelException("The \"hr\" parameter can't be negative[负数].");
                }
            }else{
                if(!"hr".equals(paramName)){
                    throw new TemplateModelException("Unsupported parameter : " + paramName);
                }

                if(!(paramValue instanceof TemplateBooleanModel)){
                    throw new TemplateModelException("The \"hr\" parameter must be a boolean.");
                }

                hrParam = ((TemplateBooleanModel)paramValue).getAsBoolean();
            }
        }
        //根据用户提供的参数，输出html元素
        /**
         * countParamSet：当该参数为false ,说明没有存在count参数,则无法确认输出几个hr
         */
        if (!countParamSet) { //
            throw new TemplateModelException("The required \"count\" parameter missing.");
        } else if (loopVars.length > 1) {
            throw new TemplateModelException("At most one loop variable is allowed.");
        } else {
            Writer out = env.getOut();
            if (body != null) {
                for(int i = 0; i < countParam; ++i) {
                    //是否要输出hr
                    if (hrParam && i != 0) {
                        out.write("<hr>");
                    }
                    if (loopVars.length > 0) {
                        loopVars[0] = new SimpleNumber(i + 1);
                    }
                    //输出标签体里的内容
                    body.render(env.getOut());
                }
            }
        }
    }
}
