package pe.com.amsac.tramite.api.util;

import java.lang.reflect.Method;

public abstract interface Getter<T> {
    public abstract Object get(Object paramObject) throws PropertyAccessException;

    public abstract Class<T> getReturnType();

    public abstract String getMethodName();

    public abstract Method getMethod();
}
