package moolah.providers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import moolah.model.Account;

import java.io.IOException;

public class AccountJSONSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.getId().toString());
    }
}
