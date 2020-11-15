package com.myspring.formywork.core;

/**
 * Created by fei 2020/11/15 19:43.
 */
public interface MyBeanFactory {
     /** 获取bean
          * 19:55 2020/11/15
          * @param
          * @return
          */
    public Object getBean(String beanname) throws Exception;

}
