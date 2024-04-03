package pe.com.amsac.tramite.api.util;

import java.lang.reflect.Method;

public abstract interface Setter {
    public abstract void set(Object paramObject1, Object paramObject2) throws PropertyAccessException;

    public abstract String getMethodName();

    public abstract Method getMethod();
}
