package com.myspring.formywork.beans.support;

import com.myspring.formywork.beans.comfig.MyBeanDefinition;
import com.myspring.formywork.context.support.MyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fei 2020/11/15 20:04.
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {
    public final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>(256);

}
