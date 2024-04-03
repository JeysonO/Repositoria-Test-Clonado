package pe.com.amsac.tramite.api.util;

import java.util.HashMap;
import java.util.Map;

public class TypeConstant {

    public static final String TYPE_JAVA_NULL = "Null";
    public static final String TYPE_JAVA_BYTE_NAME = "Byte";
    public static final String TYPE_JAVA_SHORT_NAME = "Short";
    public static final String TYPE_JAVA_INTEGER_NAME = "Integer";
    public static final String TYPE_JAVA_LONG_NAME = "Long";
    public static final String TYPE_JAVA_FLOAT_NAME = "Float";
    public static final String TYPE_JAVA_DOUBLE_NAME = "Double";
    public static final String TYPE_JAVA_BIGDECIMAL_NAME = "BigDecimal";
    public static final String TYPE_JAVA_STRING_NAME = "String";
    public static final String TYPE_JAVA_DATE_NAME = "Date";
    public static final String TYPE_JAVA_BOOLEAN_NAME = "Boolean";
    public static final String TYPE_JAVA_ARRAY_LIST_NAME = "ArrayList";
    public static final String TYPE_JAVA_OBJECT_NAME = "Object";
    public static final String TYPE_JAVA_BYTE_CANONICAL_NAME = "java.lang.Byte";
    public static final String TYPE_JAVA_SHORT_CANONICAL_NAME = "java.lang.Short";
    public static final String TYPE_JAVA_INTEGER_CANONICAL_NAME = "java.lang.Integer";
    public static final String TYPE_JAVA_LONG_CANONICAL_NAME = "java.lang.Long";
    public static final String TYPE_JAVA_FLOAT_CANONICAL_NAME = "java.lang.Float";
    public static final String TYPE_JAVA_DOUBLE_CANONICAL_NAME = "java.lang.Double";
    public static final String TYPE_JAVA_BIGDECIMAL_CANONICAL_NAME = "java.math.BigDecimal";
    public static final String TYPE_JAVA_STRING_CANONICAL_NAME = "java.lang.String";
    public static final String TYPE_JAVA_DATE_CANONICAL_NAME = "java.util.Date";
    public static final String TYPE_JAVA_BOOLEAN_CANONICAL_NAME = "java.lang.Boolean";
    public static final String TYPE_JAVA_ARRAY_LIST_CANONICAL_NAME = "java.util.ArrayList";
    public static final String TYPE_JAVA_OBJECT_CANONICAL_NAME = "java.lang.Object";
    public static final String INTERFACE_JAVA_LIST_NAME = "List";
    public static final String INTERFACE_JAVA_LINKEDLIST_NAME = "LinkedList";
    public static final String INTERFACE_JAVA_SET_NAME = "java.util.Set";
    public static final String INTERFACE_JAVA_COLLECTION_NAME = "java.util.Collection";
    public static final String INTERFACE_JAVA_SERIALIZABLE_NAME = "Serializable";
    public static final String INTERFACE_JAVA_LIST_CANONICAL_NAME = "java.util.List";
    public static final String INTERFACE_JAVA_LINKEDLIST_CANONICAL_NAME = "java.util.LinkedList";
    public static final String INTERFACE_JAVA_SET_CANONICAL_NAME = "java.util.Set";
    public static final String INTERFACE_JAVA_COLLECTION_CANONICAL_NAME = "java.util.Collection";
    public static final String INTERFACE_JAVA_SERIALIZABLE_CANONICAL_NAME = "java.io.Serializable";

    protected TypeConstant() {
        throw new UnsupportedOperationException();
    }

    public static final Map<String, String> TYPES_JAVA = new HashMap() {
        private static final long serialVersionUID = 7421593965027152607L;
    };
    public static final Map<String, String> TYPES_JAVA_LIST = new HashMap() {
        private static final long serialVersionUID = -709255255556255568L;
    };
}
