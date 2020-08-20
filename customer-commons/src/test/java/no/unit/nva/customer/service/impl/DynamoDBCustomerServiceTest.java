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
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.BY_CRISTIN_ID_INDEX_NAME;
import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.BY_ORG_NUMBER_INDEX_NAME;
import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.ERROR_MAPPING_CUSTOMER_TO_ITEM;
import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.ERROR_MAPPING_ITEM_TO_CUSTOMER;
import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.ERROR_READING_FROM_TABLE;
import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.ERROR_WRITING_ITEM_TO_TABLE;
import static no.unit.nva.customer.service.impl.DynamoDBCustomerService.TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
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
                db.getTable(),
                db.getByOrgNumberIndex(),
                db.getByCristinIdIndex()
        );
    }

    @Test
    public void testConstructorThrowsNoExceptions() {
        when(environment.readEnv(TABLE_NAME)).thenReturn(NVA_CUSTOMERS_TEST);
        when(environment.readEnv(BY_ORG_NUMBER_INDEX_NAME)).thenReturn(NVA_CUSTOMERS_TEST);
        when(environment.readEnv(BY_CRISTIN_ID_INDEX_NAME)).thenReturn(NVA_CUSTOMERS_TEST);
        CustomerService serviceWithTableNameFromEnv = new DynamoDBCustomerService(client, objectMapper, environment);
        assertNotNull(serviceWithTableNameFromEnv);
    }

    @Test
    public void createNewCustomerReturnsTheCustomer() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);

        assertNotNull(createdCustomer.getIdentifier());
        assertEquals(customer, createdCustomer);
    }

    @Test
    public void updateExistingCustomerWithNewName() throws Exception {
        String newName = "New name";
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        assertNotEquals(newName, createdCustomer.getName());

        createdCustomer.setName(newName);
        Customer updatedCustomer = service.updateCustomer(createdCustomer.getIdentifier(), createdCustomer);
        assertEquals(newName, updatedCustomer.getName());
    }

    @Test
    public void updateExistingCustomerChangesModifiedDate() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);

        Customer updatedCustomer = service.updateCustomer(createdCustomer.getIdentifier(), createdCustomer);
        assertNotEquals(customer.getModifiedDate(), updatedCustomer.getModifiedDate());
    }

    @Test
    public void updateExistingCustomerPreservesCreatedDate() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);

        Customer updatedCustomer = service.updateCustomer(createdCustomer.getIdentifier(), createdCustomer);
        assertEquals(customer.getCreatedDate(), updatedCustomer.getCreatedDate());
    }

    @Test
    public void updateExistingCustomerWithDifferentIdentifiersThrowsException() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        UUID differentIdentifier = UUID.randomUUID();

        InputException exception = assertThrows(InputException.class,
            () -> service.updateCustomer(differentIdentifier, createdCustomer));
        String expectedMessage = String.format(DynamoDBCustomerService.IDENTIFIERS_NOT_EQUAL,
                differentIdentifier, customer.getIdentifier());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getExistingCustomerReturnsTheCustomer() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        Customer getCustomer = service.getCustomer(createdCustomer.getIdentifier());
        assertEquals(createdCustomer, getCustomer);
    }

    @Test
    public void getCustomerByOrgNumberReturnsTheCustomer() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        Customer getCustomer = service.getCustomerByOrgNumber(createdCustomer.getFeideOrganizationId());
        assertEquals(createdCustomer, getCustomer);
    }

    @Test
    public void getCustomerByCristinIdReturnsTheCustomer() throws Exception {
        Customer customer = getNewCustomer();
        Customer createdCustomer = service.createCustomer(customer);
        Customer getCustomer = service.getCustomerByCristinId(createdCustomer.getCristinId());
        assertEquals(createdCustomer, getCustomer);
    }

    @Test
    public void getAllCustomersReturnsListOfCustomers() throws Exception {
        // create three customers
        service.createCustomer(getNewCustomer());
        service.createCustomer(getNewCustomer());
        service.createCustomer(getNewCustomer());

        List<Customer> customers = service.getCustomers();
        assertEquals(3, customers.size());
    }

    @Test
    public void getCustomerNotFoundThrowsException() {
        UUID nonExistingCustomer = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> service.getCustomer(nonExistingCustomer));
    }

    @Test
    public void getCustomerTableErrorThrowsException() {
        Table failingTable = mock(Table.class);
        when(failingTable.getItem(anyString(),any())).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable,
                db.getByOrgNumberIndex(),
                db.getByCristinIdIndex()
        );
        DynamoDBException exception = assertThrows(DynamoDBException.class,
            () -> failingService.getCustomer(UUID.randomUUID()));
        assertEquals(ERROR_READING_FROM_TABLE, exception.getMessage());
    }

    @Test
    public void getCustomersTableErrorThrowsException() {
        Table failingTable = mock(Table.class);
        when(failingTable.scan()).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable,
                db.getByOrgNumberIndex(),
                db.getByCristinIdIndex()
        );
        DynamoDBException exception = assertThrows(DynamoDBException.class,
            () -> failingService.getCustomers());   
        assertEquals(ERROR_READING_FROM_TABLE, exception.getMessage());
    }

    @Test
    public void createCustomerTableErrorThrowsException() {
        Table failingTable = mock(Table.class);
        when(failingTable.putItem(any(Item.class))).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable,
                db.getByOrgNumberIndex(),
                db.getByCristinIdIndex()
        );
        DynamoDBException exception = assertThrows(DynamoDBException.class,
            () -> failingService.createCustomer(getNewCustomer()));
        assertEquals(ERROR_WRITING_ITEM_TO_TABLE, exception.getMessage());

    }

    @Test
    public void updateCustomerTableErrorThrowsException() {
        Table failingTable = mock(Table.class);
        when(failingTable.putItem(any(Item.class))).thenThrow(RuntimeException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                objectMapper,
                failingTable,
                db.getByOrgNumberIndex(),
                db.getByCristinIdIndex()
        );
        Customer customer = getNewCustomer();
        customer.setIdentifier(UUID.randomUUID());
        DynamoDBException exception = assertThrows(DynamoDBException.class,
            () -> failingService.updateCustomer(customer.getIdentifier(), customer));
        assertEquals(ERROR_WRITING_ITEM_TO_TABLE, exception.getMessage());
    }

    @Test
    public void customerToItemThrowsExceptionWhenInvalidJson() throws JsonProcessingException {
        ObjectMapper failingObjectMapper = mock(ObjectMapper.class);
        when(failingObjectMapper.writeValueAsString(any(Customer.class))).thenThrow(JsonProcessingException.class);
        DynamoDBCustomerService failingService = new DynamoDBCustomerService(
                failingObjectMapper,
                db.getTable(),
                db.getByOrgNumberIndex(),
                db.getByCristinIdIndex()
        );
        InputException exception = assertThrows(InputException.class,
            () -> failingService.customerToItem(getNewCustomer()));
        assertEquals(ERROR_MAPPING_CUSTOMER_TO_ITEM, exception.getMessage());
    }

    @Test
    public void itemToCustomerThrowsExceptionWhenInvalidJson() {
        Item item = mock(Item.class);
        when(item.toJSON()).thenThrow(new IllegalStateException());
        DynamoDBException exception = assertThrows(DynamoDBException.class,
            () -> service.itemToCustomer(item));
        assertEquals(ERROR_MAPPING_ITEM_TO_CUSTOMER, exception.getMessage());

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
                .withCristinId("http://cristin.id")
                .build();
    }

}
