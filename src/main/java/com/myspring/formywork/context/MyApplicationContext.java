package com.myspring.formywork.context;

import com.myspring.formywork.annotation.MyAutowired;
import com.myspring.formywork.beans.MyBeanWrapper;
import com.myspring.formywork.beans.comfig.MyBeanDefinition;
import com.myspring.formywork.beans.comfig.MyBeanPostProcessor;
import com.myspring.formywork.beans.support.MyBeanDefinitionReader;
import com.myspring.formywork.beans.support.MyDefaultListableBeanFactory;
import com.myspring.formywork.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fei 2020/11/15 19:58.
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
   private String[] configLocations;
   private MyBeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String,Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String,MyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MyBeanWrapper>();

    public MyApplicationContext(String... configLoactions){
        this.configLocations = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void refresh() {
       //定位
         reader= new MyBeanDefinitionReader(this.configLocations);
        //2、加载
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }
     /**
          * 21:20 2020/11/15
          * @param 
          * @return
          */
    private void doAutowrited() {
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<MyBeanDefinition> beanDefinitions) {

        for (MyBeanDefinition  beanDefinition: beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
               continue;
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }

    }


    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    public Object getBean(String beanName) throws Exception {

        MyBeanDefinition myBeanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式 后续
        //MyBeanPostProcessor postProcessor = new MyBeanPostProcessor();

       // postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(beanName,myBeanDefinition);

        //3、把这个对象封装到BeanWrapper中
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);

        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        //postProcessor.postProcessAfterInitialization(instance,beanName);

//        //3、注入
        populateBean(beanName,myBeanDefinition,beanWrapper);


        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();

    }
    private Object instantiateBean(String beanName, MyBeanDefinition myBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = myBeanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
            //假设默认就是单例,细节暂且不考虑，先把主线拉通
            if(this.singletonObjects.containsKey(className)){
                instance = this.singletonObjects.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonObjects.put(className,instance);
                this.singletonObjects.put(myBeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }


    private void populateBean(String beanName, MyBeanDefinition myBeanDefinition, MyBeanWrapper myBeanWrapper) {
        Object instance = myBeanWrapper.getWrappedInstance();


        Class<?> clazz = myBeanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入 所有类都注入
       // if(!(clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class))){
          //  return;
       // }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(MyAutowired.class)){ continue;}

            MyAutowired autowired = field.getAnnotation(MyAutowired.class);

            String autowiredBeanName =  autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                //可能NULL， 后续
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }

                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }


    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
