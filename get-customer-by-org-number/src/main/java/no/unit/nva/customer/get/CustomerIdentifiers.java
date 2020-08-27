package no.unit.nva.customer.get;

import java.net.URI;

public class CustomerIdentifiers {

    private final URI identifier;
    private final URI cristinId;

    public CustomerIdentifiers(URI identifier, URI cristinId) {
        this.identifier = identifier;
        this.cristinId = cristinId;
    }

    public URI getIdentifier() {
        return identifier;
    }

    public URI getCristinId() {
        return cristinId;
    }
}
