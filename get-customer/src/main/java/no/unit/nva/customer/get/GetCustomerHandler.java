package no.unit.nva.customer.get;

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

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_OK;

public class GetCustomerHandler extends ApiGatewayHandler<Void,Customer> {

    public static final String IDENTIFIER = "identifier";
    public static final String IDENTIFIER_IS_NOT_A_VALID_UUID = "Identifier is not a valid UUID: ";
    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";
    private final CustomerService customerService;

    /**
     * Default Constructor for GetCustomerHandler.
     */
    @JacocoGenerated
    public GetCustomerHandler() {
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
    public GetCustomerHandler(CustomerService customerService, Environment environment) {
        super(Void.class, environment);
        this.customerService = customerService;
    }

    @Override
    protected Customer processInput(Void input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return customerService.getCustomer(getIdentifier(requestInfo));
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
    protected Integer getSuccessStatusCode(Void input, Customer output) {
        return SC_OK;
    }
}
