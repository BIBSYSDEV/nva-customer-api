package no.unit.nva.customer.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import nva.commons.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerTest {

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper;

    @Test
    public void customerMappedToJsonAndBack() throws JsonProcessingException {
        Instant now = Instant.now();
        Customer customer = new Customer.Builder()
                .withIdentifier(UUID.randomUUID())
                .withName("Name")
                .withShortName("SN")
                .withCreatedDate(now)
                .withModifiedDate(now)
                .withDisplayName("Display Name")
                .withArchiveName("Archive Name")
                .withCname("CNAME")
                .withInstitutionDns("institution.dns")
                .withAdministrationId("adminstr@ion.id")
                .withFeideOrganizationId("123456789")
                .build();

        Customer mappedCustomer = objectMapper.readValue(objectMapper.writeValueAsString(customer), Customer.class);

        assertEquals(customer, mappedCustomer);
    }

}
