package no.unit.nva.customer.getall;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.model.CustomerList;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.testutils.TestContext;
import nva.commons.handlers.GatewayResponse;
import nva.commons.utils.Environment;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static nva.commons.handlers.ApiGatewayHandler.ACCESS_CONTROL_ALLOW_ORIGIN;
import static nva.commons.handlers.ApiGatewayHandler.ALLOWED_ORIGIN_ENV;
import static nva.commons.handlers.ApiGatewayHandler.APPLICATION_PROBLEM_JSON;
import static nva.commons.handlers.ApiGatewayHandler.CONTENT_TYPE;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAllCustomersHandlerTest {

    public static final String WILDCARD = "*";
    public static final String HEADERS = "headers";

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerService customerService;
    private Environment environment;
    private GetAllCustomersHandler handler;
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
        handler = new GetAllCustomersHandler(customerService, environment);
        outputStream = new ByteArrayOutputStream();
        context = new TestContext();
    }

    @Test
    public void requestToHandlerReturnsCustomer() throws Exception {
        UUID identifier = UUID.randomUUID();
        Customer customer = new Customer.Builder()
                .withIdentifier(identifier)
                .build();
        CustomerList customers = CustomerList.of(customer);
        when(customerService.getCustomers()).thenReturn(customers);

        Map<String,Object> headers = getRequestHeaders();
        InputStream inputStream = inputStream(headers);

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<CustomerList> actual = objectMapper.readValue(
                outputStream.toByteArray(),
                GatewayResponse.class);

        GatewayResponse<CustomerList> expected = new GatewayResponse<>(
            objectMapper.writeValueAsString(customers),
            getResponseHeaders(),
            HttpStatus.SC_OK
        );

        assertEquals(expected, actual);
    }

    private Map<String, Object> getRequestHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_JSON.getMimeType(),
                ACCEPT, APPLICATION_JSON.getMimeType());
    }

    private Map<String, String> getResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_JSON.getMimeType(),
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

    private Map<String, String> getErrorResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_PROBLEM_JSON,
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

    protected InputStream inputStream(Map<String,Object> headers) throws JsonProcessingException {
        Map<String,Object> request = Map.of(
                HEADERS, headers
        );
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(request));
    }

}
