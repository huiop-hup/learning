package com.myspring.formywork.beans;

/**
 * Created by fei 2020/11/15 20:04.
 */
public class MyBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public MyBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
