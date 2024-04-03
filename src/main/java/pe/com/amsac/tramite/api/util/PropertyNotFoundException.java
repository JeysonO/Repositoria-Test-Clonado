package pe.com.amsac.tramite.api.util;

public class PropertyNotFoundException extends Exception {

    private static final long serialVersionUID = -1479960989712770286L;
    private String className;
    private String propertyName;

    public PropertyNotFoundException(String className, String propertyName, boolean wasSetter) {
        super("Could not find a " + (wasSetter ? "setter" : "getter") + " for property " + propertyName + " in class "
                + className);

        this.propertyName = propertyName;
        this.className = className;
    }

    public final String getClassName() {
        return this.className;
    }

    public final String getPropertyName() {
        return this.propertyName;
    }
}
