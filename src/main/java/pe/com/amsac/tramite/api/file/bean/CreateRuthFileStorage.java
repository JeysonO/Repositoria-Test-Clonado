package pe.com.amsac.tramite.api.file.bean;

public abstract class CreateRuthFileStorage<T> {

    private T object;

    public CreateRuthFileStorage() {
    }

    public CreateRuthFileStorage setObject(T object) {
        this.object = object;
        return this;
    }

    public abstract String build();
}
