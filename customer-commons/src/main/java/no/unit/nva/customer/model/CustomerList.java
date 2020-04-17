package no.unit.nva.customer.model;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.ShortMethodName")
public class CustomerList extends ArrayList<Customer> {

    /**
     * Create a CustomerList from a List of Customer objects.
     *
     * @param customers list of Customer
     * @return customerList
     */
    public static CustomerList of(List<Customer> customers) {
        CustomerList list = new CustomerList();
        list.addAll(customers);
        return list;
    }

    public static CustomerList of(Customer customers) {
        return of(List.of(customers));
    }


}
