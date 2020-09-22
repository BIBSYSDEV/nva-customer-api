package no.unit.nva.customer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerListTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void customerListFromCustomer() {
        CustomerDto customer = new CustomerDto();
        CustomerList customerList = CustomerList.of(objectMapper.createObjectNode(), customer);
        assertEquals(1, customerList.getCustomers().size());
        assertEquals(customer, customerList.getCustomers().get(0));
    }

    @Test
    public void customerListFromNull() {
        List<CustomerDto> list = new ArrayList<>();
        list.add(null);
        CustomerList customerList = CustomerList.of(objectMapper.createObjectNode(), list);
        assertEquals(1, customerList.getCustomers().size());
        assertTrue(customerList.getCustomers().get(0) == null);
    }
}
