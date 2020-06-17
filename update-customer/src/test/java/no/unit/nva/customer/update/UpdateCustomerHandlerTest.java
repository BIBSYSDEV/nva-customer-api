package no.unit.nva.customer.update;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.testutils.HandlerRequestBuilder;
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

import static no.unit.nva.customer.testing.TestHeaders.getErrorResponseHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getRequestHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getResponseHeaders;
import static no.unit.nva.customer.update.UpdateCustomerHandler.IDENTIFIER;
import static nva.commons.handlers.ApiGatewayHandler.ALLOWED_ORIGIN_ENV;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_REQUEST;

public class UpdateCustomerHandlerTest {

    public static final String WILDCARD = "*";
    public static final String REQUEST_ID = "requestId";

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerService customerServiceMock;
    private Environment environmentMock;
    private UpdateCustomerHandler handler;
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
        handler = new UpdateCustomerHandler(customerServiceMock, environmentMock);
        outputStream = new ByteArrayOutputStream();
        context = new TestContext();
    }

    @Test
    public void requestToHandlerReturnsCustomerUpdated() throws Exception {
        UUID identifier = UUID.randomUUID();
        Customer customer = new Customer.Builder()
                .withIdentifier(identifier)
                .withName("New Customer")
                .build();
        when(customerServiceMock.updateCustomer(identifier, customer)).thenReturn(customer);

        Map<String,String> pathParameters = Map.of(IDENTIFIER, identifier.toString());
        InputStream inputStream = new HandlerRequestBuilder<Customer>(objectMapper)
            .withBody(customer)
            .withHeaders(getRequestHeaders())
            .withPathParameters(pathParameters)
            .build();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Customer> actual = objectMapper.readValue(
                outputStream.toByteArray(),
                GatewayResponse.class);

        GatewayResponse<Customer> expected = new GatewayResponse<>(
            customer,
            getResponseHeaders(),
            HttpStatus.SC_OK
        );

        assertEquals(expected, actual);
    }

    @Test
    public void requestToHandlerWithMalformedIdentifierReturnsBadRequest() throws Exception {
        String malformedIdentifier = "for-testing";
        Customer customer = new Customer.Builder()
                .withIdentifier(UUID.randomUUID())
                .withName("New Customer")
                .build();

        Map<String,String> pathParameters = Map.of(IDENTIFIER, malformedIdentifier);
        InputStream inputStream = new HandlerRequestBuilder<Customer>(objectMapper)
            .withBody(customer)
            .withHeaders(getRequestHeaders())
            .withPathParameters(pathParameters)
            .build();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> actual = objectMapper.readValue(
                outputStream.toByteArray(),
                GatewayResponse.class);

        GatewayResponse<Problem> expected = new GatewayResponse<>(
                Problem.builder()
                        .withStatus(BAD_REQUEST)
                        .withTitle(BAD_REQUEST.getReasonPhrase())
                        .withDetail(UpdateCustomerHandler.IDENTIFIER_IS_NOT_A_VALID_UUID + malformedIdentifier)
                        .with(REQUEST_ID, null)
                        .build(),
                getErrorResponseHeaders(),
                SC_BAD_REQUEST
        );

        assertEquals(expected, actual);
    }
}
