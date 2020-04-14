package no.unit.nva.customer.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
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
    public static final String ERROR_MAPPING_ITEM_TO_CUSTOMER = "Error mapping Item to Customer: ";
    public static final String ERROR_MAPPING_CUSTOMER_TO_ITEM = "Error mapping Customer to Item: ";
    public static final String ERROR_WRITING_ITEM_TO_TABLE = "Error writing Item to Table: ";
    public static final String CUSTOMER_NOT_FOUND = "Customer not found: ";
    public static final String ERROR_READING_FROM_TABLE = "Error reading from Table: ";

    private final Table table;
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
        DynamoDB dynamoDB = new DynamoDB(client);

        this.table = dynamoDB.getTable(tableName);
        this.objectMapper = objectMapper;
    }

    public DynamoDBCustomerService(ObjectMapper objectMapper, Table table) {
        this.table = table;
        this.objectMapper = objectMapper;
    }

    @Override
    public Customer getCustomer(UUID identifier) throws ApiGatewayException {
        Item item;
        try {
            item = table.getItem(Customer.IDENTIFIER, identifier.toString());
        } catch (Exception e) {
            logError(e);
            throw new DynamoDBException(ERROR_READING_FROM_TABLE + e.getMessage());
        }
        if (item == null) {
            throw new NotFoundException(CUSTOMER_NOT_FOUND + identifier.toString());
        }
        return itemToCustomer(item);
    }

    @Override
    public List<Customer> getCustomers() throws ApiGatewayException {
        ItemCollection<ScanOutcome> scan;
        try {
            scan = table.scan();
        } catch (Exception e) {
            logError(e);
            throw new DynamoDBException(ERROR_READING_FROM_TABLE + e.getMessage());
        }
        return scanToCustomers(scan);
    }

    protected List<Customer> scanToCustomers(ItemCollection<ScanOutcome> scan) throws DynamoDBException {
        List<Customer> customers = new ArrayList<>();
        Iterator<Item> iterator = scan.iterator();
        while (iterator.hasNext()) {
            customers.add(itemToCustomer(iterator.next()));
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
            logError(e);
            throw new DynamoDBException(ERROR_WRITING_ITEM_TO_TABLE + e.getMessage());
        }
        return getCustomer(identifier);
    }

    @Override
    public Customer updateCustomer(UUID identifier, Customer customer) throws ApiGatewayException {
        try {
            customer.setModifiedDate(Instant.now());
            Item item = customerToItem(customer);
            table.putItem(item);
        } catch (Exception e) {
            logError(e);
            throw new DynamoDBException(ERROR_WRITING_ITEM_TO_TABLE + e.getMessage());
        }
        return getCustomer(identifier);
    }

    protected Item customerToItem(Customer customer) throws InputException {
        Item item;
        try {
            item = Item.fromJSON(objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException e) {
            logError(e);
            throw new InputException(ERROR_MAPPING_CUSTOMER_TO_ITEM + e.getMessage());
        }
        return item;
    }

    protected Customer itemToCustomer(Item item) throws DynamoDBException {
        Customer customerOutcome;
        try {
            customerOutcome = objectMapper.readValue(item.toJSON(), Customer.class);
        } catch (Exception e) {
            logError(e);
            throw new DynamoDBException(ERROR_MAPPING_ITEM_TO_CUSTOMER + e.getMessage());
        }
        return customerOutcome;
    }
}
