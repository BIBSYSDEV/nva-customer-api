package no.unit.nva.customer.get;

import static no.unit.nva.customer.testing.TestHeaders.getErrorResponseHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getRequestHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getResponseHeaders;
import static nva.commons.handlers.ApiGatewayHandler.ALLOWED_ORIGIN_ENV;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.handlers.GatewayResponse;
import nva.commons.utils.Environment;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.zalando.problem.Problem;

public class GetCustomerByOrgNumberHandlerTest {

    public static final String WILDCARD = "*";
    public static final String REQUEST_ID = "requestId";
    public static final String SAMPLE_ORG_NUMBER = "123";
    public static final String EXPECTED_ERROR_MESSAGE = "Missing from pathParameters: orgNumber";

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerService customerServiceMock;
    private Environment environmentMock;
    private GetCustomerByOrgNumberHandler handler;
    private ByteArrayOutputStream outputStream;
    private Context context;

    /**
     * Setting up test environment.
     */
    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        customerServiceMock = mock(CustomerService.class);
        environmentMock = mock(Environment.class);
        when(environmentMock.readEnv(ALLOWED_ORIGIN_ENV)).thenReturn(WILDCARD);
        handler = new GetCustomerByOrgNumberHandler(customerServiceMock, environmentMock);
        outputStream = new ByteArrayOutputStream();
        context = Mockito.mock(Context.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestToHandlerReturnsCustomer() throws Exception {
        UUID identifier = UUID.randomUUID();
        Customer customer = new Customer.Builder()
            .withIdentifier(identifier)
            .withFeideOrganizationId(SAMPLE_ORG_NUMBER)
            .build();
        when(customerServiceMock.getCustomerByOrgNumber(SAMPLE_ORG_NUMBER)).thenReturn(customer);

        Map<String, String> pathParameters = Map.of(GetCustomerByOrgNumberHandler.ORG_NUMBER, SAMPLE_ORG_NUMBER);
        InputStream inputStream = new HandlerRequestBuilder<Void>(objectMapper)
            .withHeaders(getRequestHeaders())
            .withPathParameters(pathParameters)
            .build();
        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<CustomerIdentifier> actual = objectMapper.readValue(
            outputStream.toByteArray(),
            GatewayResponse.class);

        GatewayResponse<CustomerIdentifier> expected = new GatewayResponse<>(
            objectMapper.writeValueAsString(new CustomerIdentifier(identifier)),
            getResponseHeaders(),
            HttpStatus.SC_OK
        );

        assertEquals(expected, actual);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestToHandlerWithEmptyOrgNumberReturnsBadRequest() throws Exception {

        InputStream inputStream = new HandlerRequestBuilder<Void>(objectMapper)
            .withHeaders(getRequestHeaders())
            .build();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> actual = objectMapper.readValue(
            outputStream.toByteArray(),
            GatewayResponse.class);

        GatewayResponse<Problem> expected = new GatewayResponse<>(
            Problem.builder()
                .withStatus(BAD_REQUEST)
                .withTitle(BAD_REQUEST.getReasonPhrase())
                .withDetail(EXPECTED_ERROR_MESSAGE)
                .with(REQUEST_ID, null)
                .build(),
            getErrorResponseHeaders(),
            SC_BAD_REQUEST
        );

        assertEquals(expected, actual);
    }
}