package no.unit.nva.customer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.UUID;
import nva.commons.utils.JsonUtils;

public class CustomerMapper {

    private ObjectMapper objectMapper;
    private String namespace;

    public CustomerMapper(String namespace) {
        this.namespace = namespace;
        objectMapper = JsonUtils.objectMapper;
    }

    public CustomerDto toCustomerDto(CustomerDb customer) {
        CustomerDto customerDto = objectMapper.convertValue(customer, CustomerDto.class);
        customerDto.setId(toId(customer.getIdentifier()));
        customerDto.setContext(objectMapper.createObjectNode());
        return customerDto;
    }

    private URI toId(UUID identifier) {
        return URI.create(namespace + "/" + identifier);
    }

    public CustomerDb toCustomerDb(CustomerDto customerDto) {
        CustomerDb customer = objectMapper.convertValue(customerDto, CustomerDb.class);
        return customer;
    }
}
