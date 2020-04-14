package no.unit.nva.customer.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.CustomerDynamoDBLocal;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.exception.DynamoDBException;
import no.unit.nva.customer.exception.InputException;
import no.unit.nva.customer.exception.NotFoundException;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import nva.commons.utils.Environment;
import nva.commons.utils.JsonUtils;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableRuleMigrationSupport
public class DynamoDBCustomerServiceTest {

    public static final String NVA_CUSTOMERS_TEST = "nva_customers_test";
    @Rule
    public CustomerDynamoDBLocal db =  new CustomerDynamoDBLocal();

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper;
    private DynamoDBCustomerService service;
    private Environment environment;
    private AmazonDynamoDB client;

    /**
     * Set up environment.
     */
    @BeforeEach
    public void setUp() {
        client = DynamoDBEmbedded.create().amazonDynamoDB();
        environment = mock(Environment.class);
        service = new DynamoDBCustomerService(
                objectMapper,
                db.getTable()
        );
    }

    @Test
    public void testConstructor() {
        when(environment.readEnv(DynamoDBCustomerService.TABLE_NAME)).thenReturn(NVA_CUSTOMERS_TEST);
        CustomerService serviceWithTableNameFromEnv = new DynamoDBCustomerService(client, objectMapper, environment);;
        assertNotNull(serviceWithTableNameFromEnv);
    }

    @Test
    public void createNewCustomer() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);

        Assertions.assertNotNull(createdCustomer.getIdentifier());
        Assertions.assertEquals(customer, createdCustomer);
    }

    @Test
    public void updateExistingCustomer() throws Exception {
        String newName = "New name";
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        Assertions.assertNotEquals(newName, createdCustomer.getName());

        createdCustomer.setName(newName);
        Customer updatedCustomer = service.updateCustomer(createdCustomer.getIdentifier(), createdCustomer);
        Assertions.assertEquals(newName, updatedCustomer.getName());
        Assertions.assertNotEquals(customer.getModifiedDate(), updatedCustomer.getModifiedDate());
        Assertions.assertEquals(customer.getCreatedDate(), updatedCustomer.getCreatedDate());

    }

    @Test
    public void getExistingCustomer() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        Customer getCustomer = service.getCustomer(createdCustomer.getIdentifier());
        Assertions.assertEquals(createdCustomer, getCustomer);
    }

    @Test
    public void getAllCustomers() throws Exception {
        // create three customers
        service.createCustomer(getNewCustomer());
        service.createCustomer(getNewCustomer());
        service.createCustomer(getNewCustomer());

        List<Customer> customers = service.getCustomers();
        Assertions.assertEquals(3, customers.size());
    }

    @Test
    public void getCustomerNotFound() {
        UUID nonExistingCustomer = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> service.getCustomer(nonExistingCustomer));
    }

    @Test
    public void getCustomerTableError() {
        Table failingTable = mock(Table.class);
        when(failingTable.getItem(anyString(),any())).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable
        );
        assertThrows(DynamoDBException.class,
            () -> failingService.getCustomer(UUID.randomUUID()));

    }

    @Test
    public void getCustomersTableError() {
        Table failingTable = mock(Table.class);
        when(failingTable.scan()).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable
        );
        assertThrows(DynamoDBException.class,
            () -> failingService.getCustomers());

    }

    @Test
    public void createCustomerTableError() {
        Table failingTable = mock(Table.class);
        when(failingTable.putItem(any(Item.class))).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable
        );
        assertThrows(DynamoDBException.class,
            () -> failingService.createCustomer(getNewCustomer()));

    }

    @Test
    public void updateCustomerTableError() {
        Table failingTable = mock(Table.class);
        when(failingTable.putItem(any(Item.class))).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable
        );
        Customer customer = getNewCustomer();
        customer.setIdentifier(UUID.randomUUID());
        assertThrows(DynamoDBException.class,
            () -> failingService.updateCustomer(customer.getIdentifier(), customer));

    }

    @Test
    public void customerToItemThrowsException() throws JsonProcessingException {
        ObjectMapper failingObjectMapper = mock(ObjectMapper.class);
        when(failingObjectMapper.writeValueAsString(any(Customer.class))).thenThrow(JsonProcessingException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                failingObjectMapper,
                db.getTable()
        );
        assertThrows(InputException.class,
            () -> failingService.customerToItem(getNewCustomer()),
            DynamoDBCustomerService.ERROR_MAPPING_CUSTOMER_TO_ITEM + "N/A");
    }

    @Test
    public void itemToCustomerThrowsException() {
        Item item = mock(Item.class);
        when(item.toJSON()).thenThrow(new IllegalStateException());
        assertThrows(DynamoDBException.class,
            () -> service.itemToCustomer(item),
            DynamoDBCustomerService.ERROR_MAPPING_ITEM_TO_CUSTOMER + "null");
    }

    private Customer getNewCustomer() {
        Instant oneMinuteInThePast = Instant.now().minusSeconds(60L);
        return new Customer.Builder()
                .withName("Name")
                .withShortName("SN")
                .withCreatedDate(oneMinuteInThePast)
                .withModifiedDate(oneMinuteInThePast)
                .withDisplayName("Display Name")
                .withArchiveName("Archive Name")
                .withCname("CNAME")
                .withInstitutionDns("institution.dns")
                .withAdministrationId("adminstr@ion.id")
                .withFeideOrganizationId("123456789")
                .build();
    }

}
