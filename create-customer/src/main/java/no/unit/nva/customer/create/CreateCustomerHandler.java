package no.unit.nva.customer.create;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.CustomerDb;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.customer.service.impl.DynamoDBCustomerService;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCustomerHandler extends ApiGatewayHandler<CustomerDb, CustomerDb> {

    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(CreateCustomerHandler.class);

    /**
     * Default Constructor for CreateCustomerHandler.
     */
    @JacocoGenerated
    public CreateCustomerHandler() {
        this(new DynamoDBCustomerService(
                AmazonDynamoDBClientBuilder.defaultClient(),
                ObjectMapperConfig.objectMapper,
                new Environment()
        ), new Environment());
    }

    /**
     * Constructor for CreateCustomerHandler.
     *
     * @param customerService customerService
     * @param environment   environment
     */
    public CreateCustomerHandler(CustomerService customerService, Environment environment) {
        super(CustomerDb.class, environment, logger);
        this.customerService = customerService;
    }

    @Override
    protected CustomerDb processInput(CustomerDb input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return customerService.createCustomer(input);
    }

    @Override
    protected Integer getSuccessStatusCode(CustomerDb input, CustomerDb output) {
        return HttpStatus.SC_CREATED;
    }
}
