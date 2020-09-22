package no.unit.nva.customer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import nva.commons.utils.JsonUtils;

public class CustomerMapper {

    private ObjectMapper objectMapper;
    private String namespace;

    public CustomerMapper(String namespace) {
        this.namespace = namespace;
        objectMapper = JsonUtils.objectMapper;
    }

    public CustomerDto toCustomerDto(CustomerDb customerDb) {
        CustomerDto customerDto = objectMapper.convertValue(customerDb, CustomerDto.class);
        customerDto.setId(toId(customerDb.getIdentifier()));
        customerDto.setContext(objectMapper.createObjectNode());
        return customerDto;
    }

    public CustomerDto toCustomerDtoWithoutContext(CustomerDb customerDb) {
        CustomerDto customerDto = toCustomerDto(customerDb);
        customerDto.setContext(null);
        return customerDto;
    }

    public CustomerList toCustomerList(List<CustomerDb> customersDbs) {
        List<CustomerDto> customerDtos = customersDbs.stream()
            .map(this::toCustomerDtoWithoutContext)
            .collect(Collectors.toList()
            );
        return CustomerList.of(customerDtos);
    }

    private URI toId(UUID identifier) {
        return URI.create(namespace + "/" + identifier);
    }

    public CustomerDb toCustomerDb(CustomerDto customerDto) {
        CustomerDb customer = objectMapper.convertValue(customerDto, CustomerDb.class);
        return customer;
    }
}
