package no.unit.nva.customer.model;

import static no.unit.nva.hamcrest.DoesNotHaveNullOrEmptyFields.doesNotHaveNullOrEmptyFields;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import no.unit.nva.customer.ObjectMapperConfig;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    private final ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;

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
                .withFeideOrganizationId("123456789")
                .withCristinId("http://cristin.id")
                .build();

        Customer mappedCustomer = objectMapper.readValue(objectMapper.writeValueAsString(customer), Customer.class);

        assertEquals(customer, mappedCustomer);
        assertThat(customer, doesNotHaveNullOrEmptyFields());
    }

}
