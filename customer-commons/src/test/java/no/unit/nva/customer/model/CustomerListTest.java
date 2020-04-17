package no.unit.nva.customer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerListTest {

    @Test
    public void customerListFromCustomer() {
        CustomerList customers = CustomerList.of(new Customer());
        assertEquals(1, customers.size());
    }
}
