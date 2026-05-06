package com.aicode.core.annotation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author d
 * @date 2020/10/19 4:21 下午
 */
public enum CacheEnumTools {
    Instance;

    /**
     * 缓存枚举
     *
     * @param basePackages 扫描包地址
     * @return 可以用于缓存的集合 key 为code,value 为枚举转化后的map
     */
    public List<Map<String, Map<String, String>>> getCacheEnumList(String... basePackages) {
        List<Map<String, Map<String, String>>> list = new ArrayList<>();
        for (String basePackage : basePackages) {
            Set<Class<?>> clazzSet = getClassWithAnnotation(basePackage, CacheEnum.class);
            for (Class<?> clazz : clazzSet) {
                CacheEnum cacheEnum = clazz.getAnnotation(CacheEnum.class);
                Map<String, String> map = convertEnumToMap(clazz);
                Map<String, Map<String, String>> param = new HashMap<>();
                param.put(cacheEnum.value(), map);
                list.add(param);
            }
        }
        return list;
    }

    /**
     * 获取枚举汇总
     *
     * @param basePackages
     * @return
     */
    public List<Map<String, String>> getCacheEnumDescs(String... basePackages) {
        List<Map<String, String>> list = new ArrayList<>();
        for (String basePackage : basePackages) {
            Set<Class<?>> clazzSet = getClassWithAnnotation(basePackage, CacheEnum.class);
            for (Class<?> clazz : clazzSet) {
                CacheEnum cacheEnum = clazz.getAnnotation(CacheEnum.class);
                Map<String, String> map = new HashMap<>();
                map.put("枚举编码[code = " + cacheEnum.value() + "]", clazz.getSimpleName());
                list.add(map);
            }

        }
        return list;
    }

    /**
     * 转化枚举为map存在
     *
     * @param clazz 枚举类
     * @return map
     */
    private Map<String, String> convertEnumToMap(Class<?> clazz) {
        try {
            Map<String, String> map = new HashMap<>();
            if (clazz.isEnum()) {
                //获得类的所有属性
                Field[] declaredFields = clazz.getDeclaredFields();

                //忽略注解处理
                List<Field> fields = new ArrayList<>();
                for (int i = 0; i < declaredFields.length; i++) {
                    Field field = declaredFields[i];
                    Ignore ignore = field.getAnnotation(Ignore.class);
                    if (null == ignore) {
                        fields.add(field);
                    }
                }

                for (Object obj : clazz.getEnumConstants()) {
                    String type = obj.toString();
                    String classType = obj.getClass().getTypeName();
                    classType = classType.substring(classType.lastIndexOf(".") + 1);
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fType = field.getType().getTypeName();

                        if (!fType.contains(classType)) {
                            Object val = field.get(obj);
                            map.put(type, val == null ? type : val.toString());
                        }
                    }
                }
            }
            return map;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取具有指定枚举类型的类
     *
     * @param basePackage     扫描的起始包，自动递归
     * @param annotationClass 枚举类
     * @return 符合条件的枚举类集合
     */
    private Set<Class<?>> getClassWithAnnotation(String basePackage, Class<? extends CacheEnum> annotationClass) {
        // 包下面的类
        Set<Class<?>> clazzSet = getClasses(basePackage);
        if (clazzSet == null) {
            return null;
        }

        Set<Class<?>> resultSet = new HashSet<>();
        for (Class<?> clazz : clazzSet) {
            Annotation[] annos = clazz.getAnnotations();
            for (Annotation annotation : annos) {
                if (annotationClass == annotation.annotationType()) {
                    resultSet.add(clazz);
                }
            }
        }
        return resultSet;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    private Set<Class<?>> getClasses(String pack) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    private void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
