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
import nva.commons.utils.RequestUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCustomerByOrgNumberHandler extends ApiGatewayHandler<Void, Customer> {

    public static final String ORG_NUMBER = "orgNumber";

    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(GetCustomerByOrgNumberHandler.class);

    /**
     * Default Constructor for GetCustomerHandler.
     */
    @JacocoGenerated
    public GetCustomerByOrgNumberHandler() {
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
    public GetCustomerByOrgNumberHandler(CustomerService customerService, Environment environment) {
        super(Void.class, environment, logger);
        this.customerService = customerService;
    }

    @Override
    protected Customer processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        String orgNumber;
        try {
            orgNumber = RequestUtils.getPathParameter(requestInfo, ORG_NUMBER);
        } catch (IllegalArgumentException e) {
            throw new InputException(e.getMessage(), e);
        }
        return customerService.getCustomerByOrgNumber(orgNumber);
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, Customer output) {
        return HttpStatus.SC_OK;
    }
}
