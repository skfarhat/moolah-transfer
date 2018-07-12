package moolah.providers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import moolah.model.Account;
import moolah.services.AccountService;

import java.io.IOException;
import java.util.UUID;

public class AccountJSONDeserializer extends JsonDeserializer<Account> {
    @Override
    public Account deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String id = p.getValueAsString();
        AccountService resource = new AccountService(); // create object = get the singleton resource
        return resource.getAccount(UUID.fromString(id));
    }
}
