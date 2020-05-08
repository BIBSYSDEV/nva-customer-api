package no.unit.nva.customer.get;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.testutils.TestContext;
import nva.commons.handlers.GatewayResponse;
import nva.commons.utils.Environment;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static no.unit.nva.customer.get.GetCustomerHandler.IDENTIFIER;
import static no.unit.nva.customer.get.GetCustomerHandler.IDENTIFIER_IS_NOT_A_VALID_UUID;
import static no.unit.nva.customer.testing.TestHeaders.getErrorResponseHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getRequestHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getResponseHeaders;
import static no.unit.nva.testutils.HandlerUtils.requestObjectToApiGatewayRequestInputSteam;
import static nva.commons.handlers.ApiGatewayHandler.ALLOWED_ORIGIN_ENV;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_REQUEST;

public class GetCustomerHandlerTest {

    public static final String WILDCARD = "*";
    public static final String HEADERS = "headers";
    public static final String PATH_PARAMETERS = "pathParameters";
    public static final String REQUEST_ID = "requestId";

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerService customerServiceMock;
    private Environment environmentMock;
    private GetCustomerHandler handler;
    private ByteArrayOutputStream outputStream;
    private Context context;

    /**
     * Setting up test environment.
     */
    @BeforeEach
    public void setUp() {
        customerServiceMock = mock(CustomerService.class);
        environmentMock = mock(Environment.class);
        when(environmentMock.readEnv(ALLOWED_ORIGIN_ENV)).thenReturn(WILDCARD);
        handler = new GetCustomerHandler(customerServiceMock, environmentMock);
        outputStream = new ByteArrayOutputStream();
        context = new TestContext();
    }

    @Test
    public void requestToHandlerReturnsCustomer() throws Exception {
        UUID identifier = UUID.randomUUID();
        Customer customer = new Customer.Builder()
                .withIdentifier(identifier)
                .build();
        when(customerServiceMock.getCustomer(identifier)).thenReturn(customer);

        Map<String,String> headers = getRequestHeaders();
        Map<String, String> pathParameters = Map.of(IDENTIFIER, identifier.toString());
        InputStream inputStream = requestObjectToApiGatewayRequestInputSteam(
                customer,
                headers,
                pathParameters,
                null);
        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Customer> actual = objectMapper.readValue(
                outputStream.toByteArray(),
                GatewayResponse.class);

        GatewayResponse<Customer> expected = new GatewayResponse<>(
            objectMapper.writeValueAsString(customer),
            getResponseHeaders(),
            HttpStatus.SC_OK
        );

        assertEquals(expected, actual);
    }

    @Test
    public void requestToHandlerWithMalformedIdentifierReturnsBadRequest() throws Exception {
        String malformedIdentifier = "for-testing";

        Map<String,String> headers = getRequestHeaders();
        Map<String, String> pathParameters = Map.of(IDENTIFIER, malformedIdentifier);
        InputStream inputStream = requestObjectToApiGatewayRequestInputSteam(
                null,
                headers,
                pathParameters,
                null);

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> actual = objectMapper.readValue(
                outputStream.toByteArray(),
                GatewayResponse.class);

        GatewayResponse<Problem> expected = new GatewayResponse<>(
                Problem.builder()
                .withStatus(BAD_REQUEST)
                .withTitle(BAD_REQUEST.getReasonPhrase())
                .withDetail(IDENTIFIER_IS_NOT_A_VALID_UUID + malformedIdentifier)
                .with(REQUEST_ID, null)
                .build(),
                getErrorResponseHeaders(),
                SC_BAD_REQUEST
        );

        assertEquals(expected, actual);
    }
}
