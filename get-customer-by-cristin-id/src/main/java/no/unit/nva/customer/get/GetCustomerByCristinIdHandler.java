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

public class GetCustomerByCristinIdHandler extends ApiGatewayHandler<Void, Customer> {

    public static final String CRISTIN_ID = "cristinId";

    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(GetCustomerByCristinIdHandler.class);

    /**
     * Default Constructor for GetCustomerByCristinIdHandler.
     */
    @JacocoGenerated
    public GetCustomerByCristinIdHandler() {
        this(new DynamoDBCustomerService(
            AmazonDynamoDBClientBuilder.defaultClient(),
            ObjectMapperConfig.objectMapper,
            new Environment()
        ), new Environment());
    }

    /**
     * Constructor for GetCustomerByCristinIdHandler.
     *
     * @param customerService customerService
     * @param environment   environment
     */
    public GetCustomerByCristinIdHandler(CustomerService customerService, Environment environment) {
        super(Void.class, environment, logger);
        this.customerService = customerService;
    }

    @Override
    protected Customer processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        String cristinId = getCristinId(requestInfo);
        Customer customer = customerService.getCustomerByCristinId(cristinId);

        return customer;
    }

    private String getCristinId(RequestInfo requestInfo) throws InputException {
        try {
            return RequestUtils.getPathParameter(requestInfo, CRISTIN_ID);
        } catch (IllegalArgumentException e) {
            throw new InputException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, Customer output) {
        return HttpStatus.SC_OK;
    }
}
