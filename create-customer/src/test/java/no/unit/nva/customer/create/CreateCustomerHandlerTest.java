package no.unit.nva.customer.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import nva.commons.handlers.GatewayResponse;
import nva.commons.utils.Environment;
import nva.commons.utils.TestLogger;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static nva.commons.handlers.ApiGatewayHandler.ACCESS_CONTROL_ALLOW_ORIGIN;
import static nva.commons.handlers.ApiGatewayHandler.CONTENT_TYPE;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateCustomerHandlerTest {

    public static final String APPLICATION_JSON = "application/json";
    public static final String WILDCARD = "*";

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerService customerService;
    private Environment environment;
    private CreateCustomerHandler handler;
    private ByteArrayOutputStream outputStream;
    private Context context;

    /**
     * Setting up test environment.
     */
    @BeforeEach
    public void setUp() {
        customerService = mock(CustomerService.class);
        environment = mock(Environment.class);
        when(environment.readEnv("ALLOWED_ORIGIN")).thenReturn("*");
        handler = new CreateCustomerHandler(customerService, environment);
        outputStream = new ByteArrayOutputStream();
        context = mock(Context.class);
        when(context.getLogger()).thenReturn(new TestLogger());
    }

    @Test
    public void createCustomerHandler() throws Exception {
        Customer customer = new Customer.Builder()
                .withName("New Customer")
                .build();
        when(customerService.createCustomer(customer)).thenReturn(customer);

        Map<String,Object> headers = getRequestHeaders();
        InputStream inputStream = inputStream(customer, headers);

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Customer> actual = objectMapper.readValue(
                outputStream.toByteArray(),
                GatewayResponse.class);

        assertEquals(HttpStatus.SC_CREATED, actual.getStatusCode());
        assertEquals(getResponseHeaders(), actual.getHeaders());
        assertEquals(customer, actual.getBodyObject(Customer.class));
    }

    private Map<String, Object> getRequestHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_JSON,
                ACCEPT, APPLICATION_JSON);
    }

    private Map<String, String> getResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_JSON,
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

    protected InputStream inputStream(Object body, Map<String,Object> headers) throws JsonProcessingException {
        Map<String,Object> request = Map.of(
                "body", objectMapper.writeValueAsString(body),
                "headers", headers
        );
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(request));
    }

}
