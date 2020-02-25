package com.jsdroid.ipc.call;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static Class getTyp(String type) throws ClassNotFoundException {
        if (type.length() > 7) {
            return Class.forName(type);
        }
        if ("int".equals(type)) {
            return int.class;
        } else if ("String".equals(type)) {
            return String.class;
        } else if ("long".equals(type)) {
            return long.class;
        } else if ("double".equals(type)) {
            return double.class;
        } else if ("float".equals(type)) {
            return float.class;
        } else if ("byte".equals(type)) {
            return byte.class;
        } else if ("short".equals(type)) {
            return short.class;
        } else if ("char".equals(type)) {
            return char.class;
        } else if ("boolean".equals(type)) {
            return boolean.class;
        }
        try {
            return ReflectUtil.class.getClassLoader().loadClass(type);
        } catch (ClassNotFoundException e) {
            return Class.forName(type);
        }
    }

    public static Class[] getTypes(String[] paramTypes) throws ClassNotFoundException {
        Class params[] = new Class[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = getTyp(paramTypes[i]);
        }
        return params;
    }


    public static Method getMethod(Class clazz, String methodName, Class... types) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredMethod(methodName, types);
        } catch (NoSuchMethodException e) {
        }

        return getMethod(clazz.getSuperclass(), methodName, types);
    }

    public static Object invoke(Class clazz, String methodName, Class[] types, Object current, Object[] params) throws InvocationTargetException, IllegalAccessException {
        Method method = getMethod(clazz, methodName, types);
        if (method != null) {
            return method.invoke(current, params);
        }
        return null;
    }


    public static Object getField(Object obj, String fieldName) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(fieldName);
            if (declaredField != null) {
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
