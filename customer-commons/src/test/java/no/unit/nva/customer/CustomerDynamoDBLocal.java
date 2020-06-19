package no.unit.nva.customer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import java.util.Collections;
import no.unit.nva.customer.model.Customer;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.List;

import static com.amazonaws.services.dynamodbv2.model.BillingMode.PAY_PER_REQUEST;
import static com.amazonaws.services.dynamodbv2.model.ScalarAttributeType.S;
import static java.util.Collections.singletonList;

public class CustomerDynamoDBLocal extends ExternalResource {

    public static final String NVA_CUSTOMERS_TABLE_NAME = "nva_customers";
    public static final String IDENTIFIER = Customer.IDENTIFIER;
    public  static final String BY_ORG_NUMBER_INDEX_NAME = "byOrgNumber";
    public static final String ORG_NUMBER = Customer.ORG_NUMBER;

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

    public Index getByOrgNumberIndex() {
        return client.getTable(NVA_CUSTOMERS_TABLE_NAME).getIndex(BY_ORG_NUMBER_INDEX_NAME);
    }

    private void createCustomerTable(AmazonDynamoDB ddb) {
        List<AttributeDefinition> attributeDefinitions = Arrays.asList(
                new AttributeDefinition(IDENTIFIER, S),
                new AttributeDefinition(ORG_NUMBER, S)
        );

        List<KeySchemaElement> keySchema = singletonList(
                new KeySchemaElement(IDENTIFIER, KeyType.HASH)
        );

        List<KeySchemaElement> byOrgNumberKeyScheme = singletonList(
            new KeySchemaElement(ORG_NUMBER, KeyType.HASH)
        );

        Projection byOrgNumberProjection = new Projection()
            .withProjectionType(ProjectionType.ALL);

        List<GlobalSecondaryIndex> globalSecondaryIndexes = singletonList(
            new GlobalSecondaryIndex()
                .withIndexName(BY_ORG_NUMBER_INDEX_NAME)
                .withKeySchema(byOrgNumberKeyScheme)
                .withProjection(byOrgNumberProjection)
        );

        CreateTableRequest createTableRequest =
                new CreateTableRequest()
                .withTableName(NVA_CUSTOMERS_TABLE_NAME)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchema)
                .withGlobalSecondaryIndexes(globalSecondaryIndexes)
                .withBillingMode(PAY_PER_REQUEST);

        ddb.createTable(createTableRequest);
    }

    @Override
    protected void after() {
        super.after();
        if (ddb != null) {
            ddb.shutdown();
        }
    }
}
