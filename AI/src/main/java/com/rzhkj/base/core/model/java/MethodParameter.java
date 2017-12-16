package com.rzhkj.base.core.model.java;

import com.rzhkj.base.core.IOHelper;
import com.rzhkj.base.core.StringHelper;
import com.rzhkj.base.core.paranamer.*;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class MethodParameter {
    /**
     * paramIndex,从1开始
     */
    int paramIndex = -1;
    /**
     * paramName名称
     */
    String paramName;
    /**
     * parameter的类型
     */
    JavaClass paramClass;
    /**
     * 与parameter相关联的method
     */
    JavaMethod method;

    public MethodParameter(int paramIndex, JavaMethod method, JavaClass paramClazz) {
        super();
        this.method = method;
        this.paramIndex = paramIndex;
        this.paramClass = paramClazz;
    }


    /**
     * 获得方法名
     *
     * @return String
     */
    public String getName() {
        if (paramIndex < 0) {
            return null;
        }
        String[] parameterNames = lookupParameterNamesByParanamer();
        if (parameterNames == null || parameterNames.length == 0) {
            if (StringUtils.isNotBlank(paramName)) {
                return paramName;
            }
            if (paramClass.getClazz().isPrimitive() || paramClass.getClazz().getName().startsWith("java.")) {
                return "param" + paramIndex;
            } else {
                return StringHelper.uncapitalize(paramClass.getClassName());
            }
        } else {
            return parameterNames[paramIndex - 1];
        }
    }

    public static Paranamer paranamer = setParanamer(Thread.currentThread().getContextClassLoader());

    public static Paranamer setParanamer(ClassLoader classLoader) {
        paranamer = new CachingParanamer(new AdaptiveParanamer(new DefaultParanamer(), new BytecodeReadingParanamer(), new JavaSourceParanamer(classLoader)));
        return paranamer;
    }

    private String[] lookupParameterNamesByParanamer() {
        return paranamer.lookupParameterNames(method.method, false);
    }


    public String getAsType() {
        return paramClass.getAsType();
    }

    public String getClassName() {
        return paramClass.getClassName();
    }

    public String getJavaType() {
        return paramClass.getJavaType();
    }

    public String getPackageName() {
        return paramClass.getPackageName();
    }

    public String getPackagePath() {
        return paramClass.getPackagePath();
    }

    public String getParentPackageName() {
        return paramClass.getParentPackageName();
    }

    public String getParentPackagePath() {
        return paramClass.getParentPackagePath();
    }

    public boolean isArray() {
        return paramClass.isArray();
    }

    public String getCanonicalName() {
        return paramClass.getCanonicalName();
    }

    public List<JavaField> getFields() {
        return paramClass.getFields();
    }

    public JavaMethod[] getMethods() {
        return paramClass.getMethods();
    }

    public boolean isAnnotation() {
        return paramClass.isAnnotation();
    }

    public boolean isAnonymousClass() {
        return paramClass.isAnonymousClass();
    }

    public boolean isEnum() {
        return paramClass.isEnum();
    }

    public boolean isInterface() {
        return paramClass.isInterface();
    }

    public boolean isLocalClass() {
        return paramClass.isLocalClass();
    }

    public boolean isMemberClass() {
        return paramClass.isMemberClass();
    }

    public boolean isPrimitive() {
        return paramClass.isPrimitive();
    }

    public boolean isSynthetic() {
        return paramClass.isSynthetic();
    }

    public JavaProperty[] getProperties() throws Exception {
        return paramClass.getProperties();
    }

    public String getSuperclassName() {
        return paramClass.getSuperclassName();
    }

    public static String[] parseJavaFileForParamNames(Method method, File javaFile) throws IOException {
        String content = IOHelper.readFile(javaFile);
        return new JavaSourceFileMethodParametersParser().parseJavaFileForParamNames(method, content);
    }

    /**
     * java 方法參數轉換
     */
    public static class JavaSourceFileMethodParametersParser {

        public String[] parseJavaFileForParamNames(Method method, String content) {
            Pattern methodPattern = Pattern.compile("(?s)" + method.getName() + "\\s*\\(" + getParamsPattern(method) + "\\)\\s*\\{");
            Matcher m = methodPattern.matcher(content);
            List paramNames = new ArrayList();
            while (m.find()) {
                for (int i = 1; i <= method.getParameterTypes().length; i++) {
                    paramNames.add(m.group(i));
                }
                return (String[]) paramNames.toArray(new String[0]);
            }
            return null;
        }

        private String getParamsPattern(Method method) {
            List paramPatterns = new ArrayList();
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                Class type = method.getParameterTypes()[i];
                String paramPattern = ".*" + type.getSimpleName() + ".*\\s+(\\w+).*";
                paramPatterns.add(paramPattern);
            }
            return StringHelper.join(paramPatterns, ",");
        }

    }

    @Override
    public String toString() {
        return "MethodParameter:" + getName() + "=" + getJavaType();
    }

}
