package no.unit.nva.customer.create;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.customer.service.impl.DynamoDBCustomerService;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.hanlders.ApiGatewayHandler;
import nva.commons.hanlders.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import nva.commons.utils.JsonUtils;
import org.apache.http.HttpStatus;

public class CreateCustomerHandler extends ApiGatewayHandler<Customer,Customer> {

    private final CustomerService customerService;

    /**
     * Default Constructor for CreateCustomerHandler.
     */
    @JacocoGenerated
    public CreateCustomerHandler() {
        this(new DynamoDBCustomerService(
                AmazonDynamoDBClientBuilder.defaultClient(),
                JsonUtils.jsonParser,
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
        super(Customer.class, environment);
        this.customerService = customerService;

    }

    @Override
    protected Customer processInput(Customer input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return customerService.createCustomer(input);
    }

    @Override
    protected Integer getSuccessStatusCode(Customer input, Customer output) {
        return HttpStatus.SC_CREATED;
    }
}
