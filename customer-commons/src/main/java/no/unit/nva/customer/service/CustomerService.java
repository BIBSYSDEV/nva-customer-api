package no.unit.nva.customer.service;

import no.unit.nva.customer.model.Customer;
import nva.commons.exceptions.ApiGatewayException;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    Customer getCustomerByOrgNumber(UUID identifier) throws ApiGatewayException;

    Customer getCustomerByOrgNumber(String orgNumber) throws ApiGatewayException;

    List<Customer> getCustomers() throws ApiGatewayException;

    Customer createCustomer(Customer customer) throws ApiGatewayException;

    Customer updateCustomer(UUID identifier, Customer customer) throws ApiGatewayException;

}
