package pe.com.amsac.tramite.api.util;

public abstract interface PropertyAccessor {
    public abstract Getter getGetter(Class paramClass, String paramString) throws PropertyNotFoundException;

    public abstract Setter getSetter(Class paramClass, String paramString) throws PropertyNotFoundException;
}
