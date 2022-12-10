package com.vv.spring.learn.spring;

public class BeanDefine {

    private Class aClass;

    private String scope;

    public BeanDefine() {
    }

    public BeanDefine(Class aClass, String scope) {
        this.aClass = aClass;
        this.scope = scope;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
