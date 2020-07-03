package no.unit.nva.customer.get;

import java.net.URI;

public class CustomerIdentifier {

    private final URI identifier;

    public CustomerIdentifier(URI identifier) {
        this.identifier = identifier;
    }

    public URI getIdentifier() {
        return identifier;
    }
}
