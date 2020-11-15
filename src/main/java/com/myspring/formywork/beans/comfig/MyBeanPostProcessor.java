package com.myspring.formywork.beans.comfig;

/**
 * Created by fei 2020/11/15 20:10.
 */
public class MyBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
