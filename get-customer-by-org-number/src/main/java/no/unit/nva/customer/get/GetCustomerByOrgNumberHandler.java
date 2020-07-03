package no.unit.nva.customer.get;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
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
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCustomerByOrgNumberHandler extends ApiGatewayHandler<Void, CustomerIdentifier> {

    public static final String ORG_NUMBER = "orgNumber";
    public static final String ERROR_BUILDING_CUSTOMER_IDENTIFIER = "Error building customer identifier";
    public static final String API_HOST = "API_HOST";
    public static final String API_SCHEME = "API_SCHEME";
    public static final String API_BASE_PATH = "API_BASE_PATH";

    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(GetCustomerByOrgNumberHandler.class);

    private final String apiScheme;
    private final String apiHost;
    private final String apiBasePath;

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
        this.apiScheme = environment.readEnv(API_SCHEME);
        this.apiHost = environment.readEnv(API_HOST);
        this.apiBasePath = environment.readEnv(API_BASE_PATH);
    }

    @Override
    protected CustomerIdentifier processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        String orgNumber = getOrgNumber(requestInfo);
        Customer customer = customerService.getCustomerByOrgNumber(orgNumber);

        URI identifier = toUri(customer.getIdentifier());
        return new CustomerIdentifier(identifier);
    }

    protected URI toUri(UUID customerIdentifier) {
        URI identifier;
        try {
            identifier = new URIBuilder()
                .setScheme(apiScheme)
                .setHost(apiHost)
                .setPathSegments(apiBasePath, customerIdentifier.toString())
                .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(ERROR_BUILDING_CUSTOMER_IDENTIFIER, e);
        }
        return identifier;
    }

    private String getOrgNumber(RequestInfo requestInfo) throws InputException {
        try {
            return RequestUtils.getPathParameter(requestInfo, ORG_NUMBER);
        } catch (IllegalArgumentException e) {
            throw new InputException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, CustomerIdentifier output) {
        return HttpStatus.SC_OK;
    }
}
