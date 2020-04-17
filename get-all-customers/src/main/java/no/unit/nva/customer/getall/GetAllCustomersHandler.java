package no.unit.nva.customer.getall;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.customer.service.impl.DynamoDBCustomerService;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;

public class GetAllCustomersHandler extends ApiGatewayHandler<String,List<Customer>> {

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
        super(String.class, environment);
        this.customerService = customerService;
    }

    @Override
    protected List<Customer> processInput(String input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return customerService.getCustomers();
    }

    @Override
    protected Integer getSuccessStatusCode(String input, List<Customer> output) {
        return SC_OK;
    }
}
