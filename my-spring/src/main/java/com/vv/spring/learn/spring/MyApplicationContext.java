package com.vv.spring.learn.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {

    private Class myConfigClass;

    private ConcurrentHashMap<String, BeanDefine> beanDefineMap = new ConcurrentHashMap<>(1024);
    private ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap<>(1024);

    public MyApplicationContext(Class myConfigClass) {
        this.myConfigClass = myConfigClass;
        if (myConfigClass.isAnnotationPresent(ComponentScan.class)) {
            //扫描包下所有class文件
            ComponentScan componentScan = (ComponentScan) myConfigClass.getAnnotation(ComponentScan.class);
            String packagePath = componentScan.value();
            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(packagePath.replace(".", "/"));
            File file = new File(resource.getPath());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String path = f.getPath();
                    if (path.endsWith(".class")) {
                        try {
                            String replace = path.replace(File.separator, "/");
                            String urlPath;
                            //判断操作系统
                            String osName = System.getProperty("os.name");
                            if (osName.startsWith("Windows")) {
                                // windows
                                urlPath = classLoader.getResource("").getPath().substring(1);
                            } else {
                                // unix or linux or mac os
                                urlPath = classLoader.getResource("").getPath();
                            }

                            //加载每个class文件
                            String substring = replace.substring(urlPath.length(), replace.indexOf(".class"));
                            String classPath = substring.replace("/", ".");
                            Class<?> aClass = classLoader.loadClass(classPath);

                            //处理带有component注解的class
                            if (aClass.isAnnotationPresent(Component.class)) {
                                Component component = aClass.getAnnotation(Component.class);

                                //将class信息按 beanName -> {class;scope} 缓存
                                String componentName = component.value();
                                if (componentName == null || "".equals(componentName)) {
                                    String name = aClass.getName();
                                    String beanUp = name.substring(name.lastIndexOf(".") + 1);
                                    componentName = Introspector.decapitalize(beanUp);
                                }
                                String scopeType = "singe";
                                if (aClass.isAnnotationPresent(Scope.class)) {
                                    Scope scope = aClass.getAnnotation(Scope.class);
                                    String value = scope.value();
                                    if (value != null && !"".equals(value) && !scopeType.equals(value)) {
                                        scopeType = "mut";
                                    }
                                }
                                beanDefineMap.put(componentName, new BeanDefine(aClass, scopeType));
                            }

                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }
            }
        }

        //初始化bean
        beanDefineMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            getBean(key);
        });

    }

    public Object getBean(String beanName) {
        BeanDefine beanDefine = beanDefineMap.get(beanName);
        if (beanDefine == null) {
            throw new RuntimeException();
        }

        //单例bean从缓存拿，没有就创建并放缓存。多例现创建一个。
        String scope = beanDefine.getScope();
        if ("singe".equals(scope)) {
            Object o = beanMap.get(beanName);
            if (o == null) {
                o = createBean(beanName, beanDefine);
            }
            return o;
        } else {
            return createBean(beanName, beanDefine);
        }

    }

    public Object createBean(String beanName, BeanDefine beanDefine) {
        Class aClass = beanDefine.getaClass();
        try {
            //空构造器构造新对象
            Object o = aClass.getConstructor().newInstance();

            //对象的属性依赖注入
            Field[] fields = aClass.getDeclaredFields();
            for (Field f : fields) {
                //带有注入的注解进行依赖注入
                if (f.isAnnotationPresent(Autowired.class)) {
                    f.setAccessible(true);
                    Object bean = getBean(f.getName());
                    f.set(o, bean);
                }
            }

            //调用回调方法
            if (o instanceof BeanNameAware) {
                ((BeanNameAware) o).setBeanName(beanName);
            }
            //others something like BeanNameAware

            beanMap.put(beanName, o);
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
