package pe.com.amsac.tramite.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyAccessorImpl implements PropertyAccessor {
    private static Logger logger = LogManager.getLogger(PropertyAccessorImpl.class);

    private static final class SetterImpl implements Setter {
        private Class clazz;
        private final Method method;
        private final String propertyName;

        private SetterImpl(Class clazz, Method method, String propertyName) {
            this.clazz = clazz;
            this.method = method;
            this.propertyName = propertyName;
        }

        public void set(Object target, Object value) throws PropertyAccessException {
            try {
                this.method.invoke(target, new Object[] { value });
            } catch (NullPointerException npe) {
                if ((value == null) && (this.method.getParameterTypes()[0].isPrimitive())) {
                    throw new PropertyAccessException(this.clazz.getName(), this.propertyName, true,
                            "Null value was assigned to a property of primitive type");
                }
                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, true,
                        "NullPointerException occurred while calling");
            } catch (InvocationTargetException ite) {
                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, true,
                        "Exception occurred inside");
            } catch (IllegalAccessException iae) {
                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, true,
                        "IllegalAccessException occurred while calling");
            } catch (IllegalArgumentException iae) {
                if ((value == null) && (this.method.getParameterTypes()[0].isPrimitive())) {
                    throw new PropertyAccessException(this.clazz.getName(), this.propertyName, true,
                            "Null value was assigned to a property of primitive type");
                }
                PropertyAccessorImpl.logger.error("IllegalArgumentException in class: " + this.clazz.getName()
                        + ", setter method of property: " + this.propertyName);

                PropertyAccessorImpl.logger.error("expected type: " + this.method.getParameterTypes()[0].getName()
                        + ", actual value: " + (value == null ? null : value.getClass().getName()));

                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, true,
                        "IllegalArgumentException occurred while calling");
            }
        }

        public Method getMethod() {
            return this.method;
        }

        public String getMethodName() {
            return this.method.getName();
        }
    }

    public static final class GetterImpl implements Getter {
        private Class clazz;
        private final Method method;
        private final String propertyName;

        private GetterImpl(Class clazz, Method method, String propertyName) {
            this.clazz = clazz;
            this.method = method;
            this.propertyName = propertyName;
        }

        public Object get(Object target) throws PropertyAccessException {
            try {
                return this.method.invoke(target, null);
            } catch (InvocationTargetException ite) {
                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, false,
                        "Exception occurred inside");
            } catch (IllegalAccessException iae) {
                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, false,
                        "IllegalAccessException occurred while calling");
            } catch (IllegalArgumentException iae) {
                PropertyAccessorImpl.logger.error("IllegalArgumentException in class: " + this.clazz.getName()
                        + ", getter method of property: " + this.propertyName);

                throw new PropertyAccessException(this.clazz.getName(), this.propertyName, false,
                        "IllegalArgumentException occurred calling");
            }
        }

        public Class getReturnType() {
            return this.method.getReturnType();
        }

        public Method getMethod() {
            return this.method;
        }

        public String getMethodName() {
            return this.method.getName();
        }
    }

    public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
        Setter result = getSetterOrNull(theClass, propertyName);
        if (result == null) {
            throw new PropertyNotFoundException(theClass.getName(), propertyName, true);
        }
        return result;
    }

    public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
        Getter result = getGetterOrNull(theClass, propertyName);
        if (result == null) {
            throw new PropertyNotFoundException(theClass.getName(), propertyName, false);
        }
        return result;
    }

    private static Setter getSetterOrNull(Class theClass, String propertyName) {
        if ((theClass == Object.class) || (theClass == null)) {
            return null;
        }
        Method method = setterMethod(theClass, propertyName);
        if (method != null) {
            if (!ReflectUtils.isPublic(theClass, method)) {
                method.setAccessible(true);
            }
            return new SetterImpl(theClass, method, propertyName);
        }
        Setter setter = getSetterOrNull(theClass.getSuperclass(), propertyName);
        if (setter == null) {
            Class[] interfaces = theClass.getInterfaces();
            for (int i = 0; (setter == null) && (i < interfaces.length); i++) {
                setter = getSetterOrNull(interfaces[i], propertyName);
            }
        }
        return setter;
    }

    private static Method setterMethod(Class theClass, String propertyName) {
        Getter getter = getGetterOrNull(theClass, propertyName);
        Class returnType = getter == null ? null : getter.getReturnType();
        Method[] methods = theClass.getDeclaredMethods();
        Method potentialSetter = null;
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            if ((methods[i].getParameterTypes().length == 1) && (methodName.startsWith("set"))) {
                String testStdMethod = Introspector.decapitalize(methodName.substring(3));

                String testOldMethod = methodName.substring(3);
                if ((testStdMethod.equals(propertyName)) || (testOldMethod.equals(propertyName))) {
                    potentialSetter = methods[i];
                    if ((returnType == null) || (methods[i].getParameterTypes()[0].equals(returnType))) {
                        return potentialSetter;
                    }
                }
            }
        }
        return potentialSetter;
    }

    private static Getter getGetterOrNull(Class theClass, String propertyName) {
        if ((theClass == Object.class) || (theClass == null)) {
            return null;
        }
        Method method = getterMethod(theClass, propertyName);
        if (method != null) {
            if (!ReflectUtils.isPublic(theClass, method)) {
                method.setAccessible(true);
            }
            return new GetterImpl(theClass, method, propertyName);
        }
        Getter getter = getGetterOrNull(theClass.getSuperclass(), propertyName);
        if (getter == null) {
            Class[] interfaces = theClass.getInterfaces();
            for (int i = 0; (getter == null) && (i < interfaces.length); i++) {
                getter = getGetterOrNull(interfaces[i], propertyName);
            }
        }
        return getter;
    }

    private static Method getterMethod(Class theClass, String propertyName) {
        Method[] methods = theClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getParameterTypes().length == 0) {
                String methodName = methods[i].getName();
                if ((methodName.startsWith("get")) || (methodName.startsWith("is"))) {
                    if (methodName.startsWith("get")) {
                        String testStdMethod = Introspector.decapitalize(methodName.substring(3));

                        String testOldMethod = methodName.substring(3);
                        if ((testStdMethod.equals(propertyName)) || (testOldMethod.equals(propertyName))) {
                            return methods[i];
                        }
                    } else if (methodName.startsWith("is")) {
                        String testStdMethod = Introspector.decapitalize(methodName.substring(2));

                        String testOldMethod = methodName.substring(2);
                        if ((testStdMethod.equals(propertyName)) || (testOldMethod.equals(propertyName))) {
                            return methods[i];
                        }
                    }
                }
            }
        }
        return null;
    }
}
