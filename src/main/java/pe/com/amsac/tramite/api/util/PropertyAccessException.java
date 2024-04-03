package pe.com.amsac.tramite.api.util;

public class PropertyAccessException extends Exception {

    private static final long serialVersionUID = 8907410269594903062L;
    private String className;
    private String propertyName;

    public PropertyAccessException(String className, String propertyName, boolean wasSetter, String message) {
        super(message + (wasSetter ? " setter" : " getter") + " of property " + propertyName + " in class "
                + className);

        this.className = className;
        this.propertyName = propertyName;
    }

    public final String getClassName() {
        return this.className;
    }

    public final String getPropertyName() {
        return this.propertyName;
    }
}
