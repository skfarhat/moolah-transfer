package moolah.providers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import moolah.model.Account;

import java.io.IOException;

/**
 * Class defining how to serialize an Account object to a JSON value.
 *
 * The id of the account is used as JSON string val.
 */
public class AccountJSONSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.getId().toString());
    }
}
