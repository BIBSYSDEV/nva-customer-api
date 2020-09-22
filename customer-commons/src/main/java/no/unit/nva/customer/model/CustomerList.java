package no.unit.nva.customer.model;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.ShortMethodName")
public class CustomerList extends ArrayList<CustomerDto> {

    /**
     * Create a CustomerList from a List of CustomerDto objects.
     *
     * @param customers list of CustomerDto
     * @return customerList
     */
    public static CustomerList of(List<CustomerDto> customers) {
        CustomerList list = new CustomerList();
        list.addAll(customers);
        return list;
    }

    public static CustomerList of(CustomerDto... customers) {
        return of(List.of(customers));
    }


}
