package no.unit.nva.customer.model.interfaces;

import static nva.commons.utils.attempt.Try.attempt;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.UUID;

public interface JsonLdSupport extends WithIdentifier {

    URI getId();

    void setId(URI id);

    @Override
    @JsonProperty("identifier")
    default UUID getIdentifier() {
        return attempt(() -> getId().getPath())
            .map(str -> str.substring(str.lastIndexOf("/") + 1))
            .map(UUID::fromString)
            .orElse(fail -> null);
    }

    @Override
    default void setIdentifier(UUID identifier) {
        //do nothing
    }

    URI getContext();

    void setContext(URI context);
}
