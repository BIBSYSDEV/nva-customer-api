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
        CustomerList customerList = CustomerList.of(customer);
        assertEquals(1, customerList.getCustomers().size());
        assertEquals(customer, customerList.getCustomers().get(0));
    }

    @Test
    public void customerListFromNull() {
        List<CustomerDto> list = new ArrayList<>();
        list.add(null);
        CustomerList customerList = CustomerList.of(list);
        assertEquals(1, customerList.getCustomers().size());
        assertTrue(customerList.getCustomers().get(0) == null);
    }
}
