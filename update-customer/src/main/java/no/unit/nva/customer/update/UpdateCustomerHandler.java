package no.unit.nva.customer.update;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.exception.InputException;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.customer.service.impl.DynamoDBCustomerService;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCustomerHandler extends ApiGatewayHandler<Customer,Customer> {

    public static final String IDENTIFIER = "identifier";
    public static final String IDENTIFIER_IS_NOT_A_VALID_UUID = "Identifier is not a valid UUID: ";

    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomerHandler.class);


    @JacocoGenerated
    public UpdateCustomerHandler() {
        this(new Environment());
    }

    @JacocoGenerated
    public UpdateCustomerHandler(Environment environment) {
        this(defaultDynamoDBCustomerService(environment), environment);
    }


    private static DynamoDBCustomerService defaultDynamoDBCustomerService(Environment environment) {
        return new DynamoDBCustomerService(
            AmazonDynamoDBClientBuilder.defaultClient(),
            ObjectMapperConfig.objectMapper,
            environment);
    }

    /**
     * Constructor for UpdateCustomerHandler.
     *
     * @param customerService customerService
     * @param environment   environment
     */
    public UpdateCustomerHandler(CustomerService customerService, Environment environment) {
        super(Customer.class, environment, logger);
        this.customerService = customerService;
    }

    @Override
    protected Customer processInput(Customer input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return customerService.updateCustomer(getIdentifier(requestInfo), input);
    }

    protected UUID getIdentifier(RequestInfo requestInfo) throws ApiGatewayException {
        String identifier = null;
        try {
            identifier = requestInfo.getPathParameters().get(IDENTIFIER);
            return UUID.fromString(identifier);
        } catch (Exception e) {
            throw new InputException(IDENTIFIER_IS_NOT_A_VALID_UUID + identifier, e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Customer input, Customer output) {
        return HttpStatus.SC_OK;
    }
}
