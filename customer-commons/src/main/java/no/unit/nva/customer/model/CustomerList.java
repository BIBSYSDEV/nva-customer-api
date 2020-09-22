package no.unit.nva.customer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import nva.commons.utils.JacocoGenerated;

@SuppressWarnings("PMD.ShortMethodName")
public class CustomerList {

    private List<CustomerDto> customers;
    @JsonProperty("@context")
    private JsonNode context;

    @JacocoGenerated
    public CustomerList() {

    }

    public CustomerList(List<CustomerDto> customers, JsonNode context) {
        this.customers = customers;
        this.context = context;
    }

    /**
     * Create a CustomerList from a List of CustomerDto objects.
     *
     * @param context   context object
     * @param customers list of CustomerDto
     * @return customerList
     */
    public static CustomerList of(JsonNode context, List<CustomerDto> customers) {
        return new CustomerList(customers, context);
    }

    public static CustomerList of(JsonNode context, CustomerDto... customers) {
        return of(context, List.of(customers));
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
