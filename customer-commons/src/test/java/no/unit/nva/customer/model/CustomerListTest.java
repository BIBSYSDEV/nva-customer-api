package no.unit.nva.customer.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerListTest {

    @Test
    public void customerListFromCustomer() {
        CustomerDto customer = new CustomerDto();
        CustomerList customers = CustomerList.of(customer);
        assertEquals(1, customers.size());
        assertEquals(customer, customers.get(0));
    }

    @Test
    public void customerListFromNull() {
        List<CustomerDto> list = new ArrayList<>();
        list.add(null);
        CustomerList customers = CustomerList.of(list);
        assertEquals(1, customers.size());
        assertTrue(customers.get(0) == null);
    }
}
