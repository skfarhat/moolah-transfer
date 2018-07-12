package moolah.providers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import moolah.model.Account;
import moolah.resources.AccountResource;

import java.io.IOException;
import java.util.UUID;

public class AccountJSONDeSerializer extends JsonDeserializer<Account> {
    @Override
    public Account deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String id = p.getValueAsString();
        AccountResource resource = new AccountResource(); // create object = get the singleton resource
        return resource.getAccount(UUID.fromString(id));
    }
}
