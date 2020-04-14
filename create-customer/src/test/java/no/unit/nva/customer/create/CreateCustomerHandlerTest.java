package no.unit.nva.customer.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.customer.ObjectMapperConfig;
import no.unit.nva.customer.model.Customer;
import no.unit.nva.customer.service.CustomerService;
import nva.commons.utils.Environment;
import nva.commons.utils.JsonUtils;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateCustomerHandlerTest {

    private final ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;

    @Test
    public void createCustomerHandler() throws Exception {
        CustomerService customerService = mock(CustomerService.class);
        Environment environment = mock(Environment.class);
        when(environment.readEnv("ALLOWED_ORIGIN")).thenReturn("*");

        CreateCustomerHandler handler = new CreateCustomerHandler(customerService, environment);

        Customer customer = new Customer.Builder().withName("New Customer").build();
        Map<String,Object> headers = Map.of(
                CONTENT_TYPE, "application/json",
                ACCEPT, "application/json");
        InputStream inputStream = inputStream(customer, headers);
        OutputStream outputStream = new ByteArrayOutputStream();
        Context context = mock(Context.class);

        handler.handleRequest(inputStream, outputStream, context);
    }

    protected InputStream inputStream(Object body, Map<String,Object> headers) throws JsonProcessingException {
        Map<String,Object> request = Map.of(
                "body", objectMapper.writeValueAsString(body),
                "headers", headers
        );
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(request));
    }

}
