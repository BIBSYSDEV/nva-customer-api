package no.unit.nva.customer.get;

import java.util.UUID;

public class CustomerIdentifier {

    private final UUID identifier;

    public CustomerIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public UUID getIdentifier() {
        return identifier;
    }
}
