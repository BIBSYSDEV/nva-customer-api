package no.unit.nva.customer.get;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.testutils.TestContext;
import nva.commons.handlers.GatewayResponse;
import nva.commons.utils.Environment;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static no.unit.nva.customer.get.GetCustomerHandler.APPLICATION_PROBLEM_JSON;
import static no.unit.nva.customer.get.GetCustomerHandler.IDENTIFIER;
import static no.unit.nva.customer.get.GetCustomerHandler.IDENTIFIER_IS_NOT_A_VALID_UUID;
import static nva.commons.handlers.ApiGatewayHandler.ACCESS_CONTROL_ALLOW_ORIGIN;
import static nva.commons.handlers.ApiGatewayHandler.ALLOWED_ORIGIN_ENV;
import static nva.commons.handlers.ApiGatewayHandler.CONTENT_TYPE;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
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
    private CustomerService customerService;
    private Environment environment;
    private GetCustomerHandler handler;
    private ByteArrayOutputStream outputStream;
    private Context context;

    /**
     * Setting up test environment.
     */
    @BeforeEach
    public void setUp() {
        customerService = mock(CustomerService.class);
        environment = mock(Environment.class);
        when(environment.readEnv(ALLOWED_ORIGIN_ENV)).thenReturn(WILDCARD);
        handler = new GetCustomerHandler(customerService, environment);
        outputStream = new ByteArrayOutputStream();
        context = new TestContext();
    }

    @Test
    public void requestToHandlerReturnsCustomer() throws Exception {
        UUID identifier = UUID.randomUUID();
        Customer customer = new Customer.Builder()
                .withIdentifier(identifier)
                .build();
        when(customerService.getCustomer(identifier)).thenReturn(customer);

        Map<String,Object> headers = getRequestHeaders();
        InputStream inputStream = inputStream(identifier.toString(), headers);

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

        Map<String,Object> headers = getRequestHeaders();
        InputStream inputStream = inputStream(malformedIdentifier, headers);

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

    private Map<String, Object> getRequestHeaders() {
        return Map.of(
                CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType(),
                ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    }

    private Map<String, String> getResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType(),
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

    private Map<String, String> getErrorResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_PROBLEM_JSON,
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

    protected InputStream inputStream(String identifier, Map<String,Object> headers) throws JsonProcessingException {
        Map<String,Object> request = Map.of(
                PATH_PARAMETERS, Map.of(IDENTIFIER, identifier),
                HEADERS, headers
        );
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(request));
    }

}
