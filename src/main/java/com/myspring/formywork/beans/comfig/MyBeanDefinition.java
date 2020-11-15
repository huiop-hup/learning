package com.myspring.formywork.beans.comfig;
import lombok.Data;
/**
 * Created by fei 2020/11/15 20:10.
 */
@Data
public class MyBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
    public String getBeanClassName() {
        return beanClassName;
    }
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
    public boolean isLazyInit() {
        return lazyInit;
    }
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    public String getFactoryBeanName() {
        return factoryBeanName;
    }
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
