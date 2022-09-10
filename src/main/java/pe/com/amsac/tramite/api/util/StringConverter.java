package pe.com.amsac.tramite.api.util;

import org.dozer.DozerConverter;

public class StringConverter extends DozerConverter<String, String> {

    public StringConverter() {
        super(String.class, String.class);
    }

    @Override
    public String convertTo(String source, String destination) {
        return getObject(source, destination);
    }

    @Override
    public String convertFrom(String source, String destination) {
        return getObject(source, destination);
    }

    private String getObject(String source, String destination) {
        if (source != null) {
            return source;
        } else {
            return destination;
        }
    }

}
