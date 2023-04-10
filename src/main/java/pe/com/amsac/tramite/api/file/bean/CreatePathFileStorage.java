package pe.com.amsac.tramite.api.file.bean;

public abstract class CreatePathFileStorage<T> {

    private T object;

    public CreatePathFileStorage() {
    }

    public CreatePathFileStorage setObject(T object) {
        this.object = object;
        return this;
    }

    public abstract String build();
}
