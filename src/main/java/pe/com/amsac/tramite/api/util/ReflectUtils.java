package pe.com.amsac.tramite.api.util;

import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;

public class ReflectUtils {

    private static final PropertyAccessor PROPERTY_ACCESSOR = new PropertyAccessorImpl();

    protected ReflectUtils()
    {
        throw new UnsupportedOperationException();
    }

    public static boolean isPublic(Class<?> clazz, Member member)
    {
        return (Modifier.isPublic(member.getModifiers())) && (Modifier.isPublic(clazz.getModifiers()));
    }

    public static boolean isAbstractClass(Class<?> clazz)
    {
        int modifier = clazz.getModifiers();
        return (Modifier.isAbstract(modifier)) || (Modifier.isInterface(modifier));
    }

    public static Map<String, String> getBeanProperties(Class<?> clazz)
    {
        Map<String, String> beanProperties = new HashMap(4);
        List<Method> methods = getDeclaredMethods(clazz);
        for (Iterator<Method> iterator = methods.iterator(); iterator.hasNext();)
        {
            Method method = (Method)iterator.next();
            if (method.getParameterTypes().length == 0)
            {
                String methodName = method.getName();
                if ((methodName.startsWith("get")) || (methodName.startsWith("is")))
                {
                    String propertyName = "";
                    if (methodName.startsWith("get")) {
                        propertyName = Introspector.decapitalize(methodName.substring(3));
                    } else if (methodName.startsWith("is")) {
                        propertyName = Introspector.decapitalize(methodName.substring(2));
                    }
                    String propertyType = method.getReturnType().getName();
                    beanProperties.put(propertyName, propertyType);
                }
            }
        }
        return beanProperties;
    }

    public static Object getProperty(Object bean, String propertyName)
            throws PropertyNotFoundException, PropertyAccessException
    {
        Class<?> clazz = bean.getClass();
        Getter getter = PROPERTY_ACCESSOR.getGetter(clazz, propertyName);
        return getter.get(bean);
    }

    public static Object setProperty(Object bean, String propertyName, Object propertyValue)
            throws PropertyNotFoundException, PropertyAccessException
    {
        Class<?> clazz = bean.getClass();
        Setter setter = PROPERTY_ACCESSOR.getSetter(clazz, propertyName);
        setter.set(bean, propertyValue);
        return bean;
    }

    public static Class<?> getPropertyType(Object bean, String propertyName)
            throws PropertyNotFoundException, PropertyAccessException
    {
        Class<?> clazz = bean.getClass();
        Getter getter = PROPERTY_ACCESSOR.getGetter(clazz, propertyName);
        return getter.getReturnType();
    }

    public static Object newInstance(Class<?> clazz)
            throws Exception
    {
        Object object;
        try
        {
            object = clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw e;
        }
        catch (IllegalAccessException e)
        {
            throw e;
        }
        return object;
    }

    public static Object newInstance(String className)
            throws Exception
    {
        Object object;
        try
        {
            Class<?> clazz = Class.forName(className);
            object = clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw e;
        }
        catch (IllegalAccessException e)
        {
            throw e;
        }
        return object;
    }

    public static List<?> newListInstance(Class<?> clazz)
            throws Exception
    {
        List<?> list;
        try
        {
            list = Collections.emptyList();
        }
        catch (Exception e)
        {
            throw e;
        }
        return list;
    }

    public static Object newArrayInstance(Class<?> clazz, int length)
            throws Exception
    {
        Object object;
        try
        {
            object = Array.newInstance(clazz, length);
        }
        catch (NullPointerException e)
        {
            throw e;
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        catch (NegativeArraySizeException e)
        {
            throw e;
        }
        return object;
    }

    public static Object setElement(Object array, Object value, int index)
            throws Exception
    {
        try
        {
            Array.set(array, index, value);
        }
        catch (NullPointerException e)
        {
            throw e;
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw e;
        }
        return array;
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz)
    {
        List<Method> methods = new ArrayList();

        Class<?> superclass = clazz.getSuperclass();
        if ((superclass != null) && (!"java.lang.Object".equals(superclass.getName())))
        {
            List<Method> superclassMethods = getDeclaredMethods(superclass);
            if (superclassMethods.size() > 0) {
                methods.addAll(superclassMethods);
            }
        }
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        return methods;
    }

    public static List<Field> getDeclaredFields(Class<?> clazz)
    {
        List<Field> fields = new ArrayList();

        Class<?> superclass = clazz.getSuperclass();
        if ((superclass != null) && (!"java.lang.Object".equals(superclass.getName())))
        {
            List<Field> superclassFields = getDeclaredFields(superclass);
            if (superclassFields.size() > 0) {
                fields.addAll(superclassFields);
            }
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }
}
