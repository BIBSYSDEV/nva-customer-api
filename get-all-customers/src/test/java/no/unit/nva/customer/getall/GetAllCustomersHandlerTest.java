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

import static no.unit.nva.customer.testing.TestHeaders.getRequestHeaders;
import static no.unit.nva.customer.testing.TestHeaders.getResponseHeaders;
import static nva.commons.handlers.ApiGatewayHandler.ALLOWED_ORIGIN_ENV;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAllCustomersHandlerTest {

    public static final String WILDCARD = "*";
    public static final String HEADERS = "headers";

    private ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private CustomerService customerServiceMock;
    private Environment environmentMock;
    private GetAllCustomersHandler handler;
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
        handler = new GetAllCustomersHandler(customerServiceMock, environmentMock);
        outputStream = new ByteArrayOutputStream();
        context = new TestContext();
    }

    @Test
    public void requestToHandlerReturnsCustomerList() throws Exception {
        UUID identifier = UUID.randomUUID();
        Customer customer = new Customer.Builder()
                .withIdentifier(identifier)
                .build();
        CustomerList customers = CustomerList.of(customer);
        when(customerServiceMock.getCustomers()).thenReturn(customers);

        Map<String,Object> headers = getRequestHeaders();
        InputStream inputStream = createRequest(headers);

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

    protected InputStream createRequest(Map<String,Object> headers) throws JsonProcessingException {
        Map<String,Object> request = Map.of(
                HEADERS, headers
        );
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(request));
    }

}
