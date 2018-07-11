package moolah.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import moolah.model.Transfer;

import javax.ws.rs.ext.ContextResolver;

public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper defaultProvider;

    public ObjectMapperProvider() {
        defaultProvider = new ObjectMapper();
        defaultProvider.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return defaultProvider;
    }
}
