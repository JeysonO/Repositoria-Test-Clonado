package pe.com.amsac.tramite.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.*;

public class BeanUtils {
    private static Logger logger = LogManager.getLogger(BeanUtils.class);

    protected BeanUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isJavaType(String type) {
        if (TypeConstant.TYPES_JAVA.get(type) != null) {
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(Object object) {
        if (object != null) {
            if ((TypeConstant.TYPES_JAVA_LIST.get(object.getClass().getCanonicalName()) != null)
                    && (((List) object).size() == 0)) {
                return true;
            }
            if (("java.lang.Long".equals(object.getClass().getCanonicalName())) && (((Long) object).intValue() <= 0)) {
                return true;
            }
            if (("java.lang.Integer".equals(object.getClass().getCanonicalName()))
                    && (((Integer) object).intValue() <= 0)) {
                return true;
            }
            if (("java.lang.Short".equals(object.getClass().getCanonicalName()))
                    && (((Short) object).intValue() <= 0)) {
                return true;
            }
            if (("java.lang.String".equals(object.getClass().getCanonicalName())) && ("".equals(object.toString()))) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static void copyProperties(Object dest, Object orig) {
        Map<String, String> origBeanProperties = ReflectUtils.getBeanProperties(orig.getClass());

        Map<String, String> destBeanProperties = ReflectUtils.getBeanProperties(dest.getClass());

        Set<String> origPropertyNames = new HashSet(origBeanProperties.keySet());

        Iterator<String> iterator = origPropertyNames.iterator();
        while (iterator.hasNext()) {
            String origPropertyName = (String) iterator.next();
            if (destBeanProperties.get(origPropertyName) != null) {
                try {
                    Object origPropertyValue = ReflectUtils.getProperty(orig, origPropertyName);
                    if (origPropertyValue != null) {
                        String origPropertyType = (String) origBeanProperties.get(origPropertyName);

                        String destPropertyType = (String) destBeanProperties.get(origPropertyName);
                        if (origPropertyType.equals(destPropertyType)) {
                            copyProperty(origPropertyName, origPropertyType, origPropertyValue, dest);
                        } else {
                            copyProperty(origPropertyName, origPropertyType, destPropertyType, origPropertyValue, dest);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error processing origPropertyName=" + origPropertyName, e);
                }
            }
        }
    }

    public static void copyProperties(List dest, Class<?> destClass, List<?> orig) {
        if ((orig != null) && (orig.size() > 0)) {
            try {
                for (int i = 0; i < orig.size(); i++) {
                    Object origObject = orig.get(i);
                    Object destObject = ReflectUtils.newInstance(destClass);
                    copyProperties(destObject, origObject);

                    dest.add(destObject);
                }
            } catch (Exception e) {
                logger.error(e);
                return;
            }
        }
    }

    public static String objectToXml(Object object) {
        Map<String, String> beanProperties = ReflectUtils.getBeanProperties(object.getClass());

        Set<String> propertyNames = new HashSet(beanProperties.keySet());

        String objectName = Introspector.decapitalize(object.getClass().getSimpleName());

        String objectToXml = "<" + objectName + ">";

        Iterator<String> iterator = propertyNames.iterator();
        while (iterator.hasNext()) {
            String propertyName = (String) iterator.next();
            try {
                Object propertyValue = ReflectUtils.getProperty(object, propertyName);

                String propertyType = (String) beanProperties.get(propertyName);
                if (TypeConstant.TYPES_JAVA_LIST.get(propertyType) != null) {
                    objectToXml = objectToXml + listToXml(propertyName, propertyValue);
                } else if (TypeConstant.TYPES_JAVA.get(propertyType) != null) {
                    objectToXml = objectToXml + predefinedToXml(propertyName, propertyValue);
                } else {
                    objectToXml = objectToXml + customToXml(propertyName, propertyValue);
                }
            } catch (Exception e) {
                logger.error("Error processing origPropertyName=" + propertyName, e);
            }
        }
        objectToXml = objectToXml + "\n" + "</" + objectName + ">";

        return objectToXml;
    }

    private static void copyProperty(String propertyName, String propertyType, Object propertyValue, Object dest)
            throws Exception {
        Object newPropertyValue = null;
        if (TypeConstant.TYPES_JAVA_LIST.get(propertyType) != null) {
            List origList = (List) propertyValue;
            List destList = new ArrayList();
            for (Iterator iter = origList.iterator(); iter.hasNext();) {
                Object origElement = iter.next();

                Object destElement = ReflectUtils.newInstance(origElement.getClass());

                copyProperties(destElement, origElement);
                destList.add(destElement);
            }
            newPropertyValue = destList;
        } else if (TypeConstant.TYPES_JAVA.get(propertyType) != null) {
            newPropertyValue = propertyValue;
        } else {
            Object customPropertyValue = ReflectUtils.newInstance(propertyValue.getClass());

            copyProperties(customPropertyValue, propertyValue);
            newPropertyValue = customPropertyValue;
        }
        ReflectUtils.setProperty(dest, propertyName, newPropertyValue);
    }

    private static void copyProperty(String propertyName, String origPropertyType, String destPropertyType,
                                     Object origPropertyValue, Object dest) throws Exception {
        if ((TypeConstant.TYPES_JAVA.get(origPropertyType) == null)
                && (TypeConstant.TYPES_JAVA.get(destPropertyType) == null)) {
            Object destPropertyValue = ReflectUtils.newInstance(destPropertyType);

            copyProperties(destPropertyValue, origPropertyValue);
            ReflectUtils.setProperty(dest, propertyName, destPropertyValue);
        }
    }

    private static String listToXml(String objectName, Object objectValue) {
        String objectToXml = "\n\t<" + objectName + ">";
        Iterator iter;
        if (objectValue != null) {
            List elements = (List) objectValue;
            for (iter = elements.iterator(); iter.hasNext();) {
                Object element = iter.next();

                String tmpObjectToXml = "\n" + objectToXml(element);

                tmpObjectToXml = tmpObjectToXml.replaceAll("\n", "\n\t\t");

                objectToXml = objectToXml + tmpObjectToXml;
            }
        }
        objectToXml = objectToXml + "\n" + "\t" + "</" + objectName + ">";

        return objectToXml;
    }

    private static String predefinedToXml(String objectName, Object objectValue) {
        String objectToXml = "\n\t<" + objectName + ">";
        if (objectValue != null) {
            objectToXml = objectToXml + objectValue.toString();
        }
        objectToXml = objectToXml + "</" + objectName + ">";

        return objectToXml;
    }

    private static String customToXml(String objectName, Object objectValue) {
        String objectToXml = "\n\t<" + objectName + ">";
        if (objectValue != null) {
            String tmpObjectToXml = objectToXml(objectValue);
            tmpObjectToXml = tmpObjectToXml.substring(tmpObjectToXml.indexOf(">") + 1,
                    tmpObjectToXml.lastIndexOf("</"));

            tmpObjectToXml = tmpObjectToXml.replaceAll("\n", "\n\t");

            tmpObjectToXml = tmpObjectToXml.substring(0, tmpObjectToXml.lastIndexOf("\t"));

            objectToXml = objectToXml + tmpObjectToXml;
        }
        objectToXml = objectToXml + "\t" + "</" + objectName + ">";

        return objectToXml;
    }

    public static <T> T merge(T local, T remote) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = local.getClass();
        Object merged = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object localValue = field.get(local);
            Object remoteValue = field.get(remote);
            if (!field.getName().equals("serialVersionUID")) {
                field.set(merged, (remoteValue != null) ? remoteValue : localValue);
            }
        }
        return (T) merged;
    }
}
