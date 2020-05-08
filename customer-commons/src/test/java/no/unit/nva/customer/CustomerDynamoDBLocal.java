package no.unit.nva.customer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import no.unit.nva.customer.model.Customer;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.List;

import static com.amazonaws.services.dynamodbv2.model.BillingMode.PAY_PER_REQUEST;
import static com.amazonaws.services.dynamodbv2.model.ScalarAttributeType.S;

public class CustomerDynamoDBLocal extends ExternalResource {

    public static final String NVA_CUSTOMERS_TABLE_NAME = "nva_customers";
    public static final String IDENTIFIER = Customer.IDENTIFIER;

    private AmazonDynamoDB ddb;
    private DynamoDB client;

    @Override
    protected void before() throws Throwable {
        super.before();
        ddb = DynamoDBEmbedded.create().amazonDynamoDB();
        createCustomerTable(ddb);
        client = new DynamoDB(ddb);
    }

    public Table getTable() {
        return client.getTable(NVA_CUSTOMERS_TABLE_NAME);
    }

    private CreateTableResult createCustomerTable(AmazonDynamoDB ddb) {
        List<AttributeDefinition> attributeDefinitions = Arrays.asList(
                new AttributeDefinition(IDENTIFIER, S)
        );

        List<KeySchemaElement> keySchema = Arrays.asList(
                new KeySchemaElement(IDENTIFIER, KeyType.HASH)
        );

        CreateTableRequest createTableRequest =
                new CreateTableRequest()
                .withTableName(NVA_CUSTOMERS_TABLE_NAME)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchema)
                .withBillingMode(PAY_PER_REQUEST);

        return ddb.createTable(createTableRequest);
    }

    @Override
    protected void after() {
        super.after();
        if (ddb != null) {
            ddb.shutdown();
        }
    }
}
