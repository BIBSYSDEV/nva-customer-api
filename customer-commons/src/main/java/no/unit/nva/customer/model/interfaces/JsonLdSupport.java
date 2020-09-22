package no.unit.nva.customer.model.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;

public interface JsonLdSupport {

    URI getId();

    void setId(URI id);

    JsonNode getContext();

    void setContext(JsonNode context);
}
