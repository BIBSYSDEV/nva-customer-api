package no.unit.nva.customer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@SuppressWarnings("PMD.ShortMethodName")
public class CustomerList {

    private List<CustomerDto> customers;
    @JsonProperty("@context")
    private JsonNode context;

    public CustomerList(List<CustomerDto> customers, JsonNode context) {
        this.customers = customers;
        this.context = context;
    }

    /**
     * Create a CustomerList from a List of CustomerDto objects.
     *
     * @param customers list of CustomerDto
     * @return customerList
     */
    public static CustomerList of(List<CustomerDto> customers) {
        return new CustomerList(customers, new ObjectMapper().createObjectNode());
    }

    public static CustomerList of(CustomerDto... customers) {
        return of(List.of(customers));
    }

    public List<CustomerDto> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerDto> customers) {
        this.customers = customers;
    }

    public JsonNode getContext() {
        return context;
    }

    public void setContext(JsonNode context) {
        this.context = context;
    }
}
