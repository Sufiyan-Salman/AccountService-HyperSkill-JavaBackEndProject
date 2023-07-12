package account.CustomSerializers;

import account.Entities.Role;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jdk.dynalink.linker.LinkerServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SETofRoleSerializer extends JsonSerializer<Set<Role>> {

    @Override
    public void serialize(Set<Role> roles, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

//        System.out.println("serializer me agay");
        jsonGenerator.writeStartArray();
        for (Role role : roles) {
            jsonGenerator.writeString("ROLE_" + role.getRole());
        }
        jsonGenerator.writeEndArray();

    }


}