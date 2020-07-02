package no.unit.nva.customer.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.exception.DynamoDBException;
import no.unit.nva.customer.exception.InputException;
import no.unit.nva.customer.exception.NotFoundException;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.utils.Environment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DynamoDBCustomerService implements CustomerService {

    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String BY_ORG_NUMBER_INDEX_NAME = "BY_ORG_NUMBER_INDEX_NAME";
    public static final String ERROR_MAPPING_ITEM_TO_CUSTOMER = "Error mapping Item to Customer";
    public static final String ERROR_MAPPING_CUSTOMER_TO_ITEM = "Error mapping Customer to Item";
    public static final String ERROR_WRITING_ITEM_TO_TABLE = "Error writing Item to Table";
    public static final String CUSTOMER_NOT_FOUND = "Customer not found: ";
    public static final String ERROR_READING_FROM_TABLE = "Error reading from Table";
    public static final String IDENTIFIERS_NOT_EQUAL = "Identifier in request parameters '%s' "
            + "is not equal to identifier in customer object '%s'";

    private final Table table;
    private final Index byOrgNumberIndex;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for DynamoDBCustomerService.
     *
     * @param client    AmazonDynamoDB client
     * @param objectMapper  Jackson objectMapper
     * @param environment   Environment reader
     */
    public DynamoDBCustomerService(AmazonDynamoDB client, ObjectMapper objectMapper, Environment environment) {
        String tableName = environment.readEnv(TABLE_NAME);
        String byOrgNumberIndexName = environment.readEnv(BY_ORG_NUMBER_INDEX_NAME);
        DynamoDB dynamoDB = new DynamoDB(client);

        this.table = dynamoDB.getTable(tableName);
        this.byOrgNumberIndex = table.getIndex(byOrgNumberIndexName);
        this.objectMapper = objectMapper;
    }

    /**
     * Constructor for DynamoDBCustomerService.
     *
     * @param objectMapper  Jackson objectMapper
     * @param table table name
     * @param byOrgNumberIndex  index name
     */
    public DynamoDBCustomerService(ObjectMapper objectMapper, Table table, Index byOrgNumberIndex) {
        this.table = table;
        this.byOrgNumberIndex = byOrgNumberIndex;
        this.objectMapper = objectMapper;
    }

    @Override
    public Customer getCustomerByOrgNumber(UUID identifier) throws ApiGatewayException {
        Item item;
        try {
            item = table.getItem(Customer.IDENTIFIER, identifier.toString());
        } catch (Exception e) {
            throw new DynamoDBException(ERROR_READING_FROM_TABLE, e);
        }
        if (item == null) {
            throw new NotFoundException(CUSTOMER_NOT_FOUND + identifier.toString());
        }
        return itemToCustomer(item);
    }

    @Override
    public Customer getCustomerByOrgNumber(String orgNumber) throws ApiGatewayException {
        Item item = null;
        try {
            ItemCollection<QueryOutcome> query = byOrgNumberIndex.query(Customer.ORG_NUMBER, orgNumber);
            Iterator<Item> iterator = query.iterator();
            if (iterator.hasNext()) {
                item = iterator.next();
            }
        } catch (Exception e) {
            throw new DynamoDBException(ERROR_READING_FROM_TABLE, e);
        }
        if (item == null) {
            throw new NotFoundException(CUSTOMER_NOT_FOUND + orgNumber);
        }
        return itemToCustomer(item);
    }

    @Override
    public List<Customer> getCustomers() throws ApiGatewayException {
        ItemCollection<ScanOutcome> scan;
        try {
            scan = table.scan();
        } catch (Exception e) {
            throw new DynamoDBException(ERROR_READING_FROM_TABLE, e);
        }
        return scanToCustomers(scan);
    }

    protected List<Customer> scanToCustomers(ItemCollection<ScanOutcome> scan) throws DynamoDBException {
        List<Customer> customers = new ArrayList<>();
        for (Item item: scan) {
            customers.add(itemToCustomer(item));
        }
        return customers;
    }

    @Override
    public Customer createCustomer(Customer customer) throws ApiGatewayException {
        UUID identifier = UUID.randomUUID();
        try {
            customer.setIdentifier(identifier);
            table.putItem(customerToItem(customer));
        } catch (Exception e) {
            throw new DynamoDBException(ERROR_WRITING_ITEM_TO_TABLE, e);
        }
        return getCustomerByOrgNumber(identifier);
    }

    @Override
    public Customer updateCustomer(UUID identifier, Customer customer) throws ApiGatewayException {
        validateIdentifier(identifier, customer);
        try {
            customer.setModifiedDate(Instant.now());
            Item item = customerToItem(customer);
            table.putItem(item);
        } catch (Exception e) {
            throw new DynamoDBException(ERROR_WRITING_ITEM_TO_TABLE, e);
        }
        return getCustomerByOrgNumber(identifier);
    }

    private void validateIdentifier(UUID identifier, Customer customer) throws InputException {
        if (!identifier.equals(customer.getIdentifier())) {
            throw new InputException(String.format(IDENTIFIERS_NOT_EQUAL, identifier, customer.getIdentifier()), null);
        }
    }

    protected Item customerToItem(Customer customer) throws InputException {
        Item item;
        try {
            item = Item.fromJSON(objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException e) {
            throw new InputException(ERROR_MAPPING_CUSTOMER_TO_ITEM, e);
        }
        return item;
    }

    protected Customer itemToCustomer(Item item) throws DynamoDBException {
        Customer customerOutcome;
        try {
            customerOutcome = objectMapper.readValue(item.toJSON(), Customer.class);
        } catch (Exception e) {
            throw new DynamoDBException(ERROR_MAPPING_ITEM_TO_CUSTOMER, e);
        }
        return customerOutcome;
    }
}
