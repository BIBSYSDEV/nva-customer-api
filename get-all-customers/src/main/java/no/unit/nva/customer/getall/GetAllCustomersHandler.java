package no.unit.nva.customer.getall;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.CustomerList;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.customer.service.impl.DynamoDBCustomerService;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;

import static org.apache.http.HttpStatus.SC_OK;

public class GetAllCustomersHandler extends ApiGatewayHandler<Void, CustomerList> {

    private final CustomerService customerService;

    /**
     * Default Constructor for GetAllCustomersHandler.
     */
    @JacocoGenerated
    public GetAllCustomersHandler() {
        this(new DynamoDBCustomerService(
                AmazonDynamoDBClientBuilder.defaultClient(),
                ObjectMapperConfig.objectMapper,
                new Environment()
        ), new Environment());
    }

    /**
     * Constructor for CreateAllCustomersHandler.
     *
     * @param customerService customerService
     * @param environment   environment
     */
    public GetAllCustomersHandler(CustomerService customerService, Environment environment) {
        super(Void.class, environment);
        this.customerService = customerService;
    }

    @Override
    protected CustomerList processInput(Void input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return CustomerList.of(customerService.getCustomers());
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, CustomerList output) {
        return SC_OK;
    }
}
