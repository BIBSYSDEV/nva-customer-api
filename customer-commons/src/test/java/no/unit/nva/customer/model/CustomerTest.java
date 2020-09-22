package no.unit.nva.customer.model;

import static no.unit.nva.hamcrest.DoesNotHaveNullOrEmptyFields.doesNotHaveNullOrEmptyFields;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import no.unit.nva.customer.ObjectMapperConfig;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    private final ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerMapper customerMapper = new CustomerMapper("http://example.org/customer");

    @Test
    public void customerMappedToJsonAndBack() throws JsonProcessingException {
        CustomerDb customer = createCustomerDb();

        CustomerDb mappedCustomer = objectMapper.readValue(objectMapper.writeValueAsString(customer), CustomerDb.class);

        assertEquals(customer, mappedCustomer);
        assertThat(customer, doesNotHaveNullOrEmptyFields());
    }

    @Test
    public void customerMapperCanMapBetweenCustomerDtoAndCustomerDb() {
        CustomerDb customerDb = createCustomerDb();
        CustomerDto customerDto = customerMapper.fromCustomerDb(customerDb);
        assertNotNull(customerDto);
        assertNotNull(customerDto.getId());

        CustomerDb mappedCustomerDB = customerMapper.fromCustomerDto(customerDto);
        assertNotNull(mappedCustomerDB);
    }

    private CustomerDb createCustomerDb() {
        Instant now = Instant.now();
        return new CustomerDb.Builder()
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
    }
}
