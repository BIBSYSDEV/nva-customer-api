package no.unit.nva.customer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import nva.commons.utils.JsonUtils;

public class CustomerMapper {

    private final ObjectMapper objectMapper;
    private final String namespace;

    public CustomerMapper(String namespace) {
        this.namespace = namespace;
        objectMapper = JsonUtils.objectMapper;
    }

    /**
     * Map from Customer from Db to Dto version.
     *
     * @param customerDb    customerDb
     * @return  customerDto
     */
    public CustomerDto toCustomerDto(CustomerDb customerDb) {
        CustomerDto customerDto = objectMapper.convertValue(customerDb, CustomerDto.class);
        URI id = toId(customerDb.getIdentifier());
        customerDto.setId(id);
        customerDto.setContext(objectMapper.createObjectNode());
        return customerDto;
    }

    /**
     * Map from Customer from Db to Dto version without context object.
     *
     * @param customerDb    customerDb
     * @return  customerDto
     */
    public CustomerDto toCustomerDtoWithoutContext(CustomerDb customerDb) {
        CustomerDto customerDto = toCustomerDto(customerDb);
        customerDto.setContext(null);
        return customerDto;
    }

    /**
     * Map from list of Customers from Db to Dto version.
     *
     * @param customersDbs  list of CustomerDb
     * @return  customerList
     */
    public CustomerList toCustomerList(List<CustomerDb> customersDbs) {
        List<CustomerDto> customerDtos = customersDbs.stream()
            .map(this::toCustomerDtoWithoutContext)
            .collect(Collectors.toList()
            );
        return CustomerList.of(customerDtos);
    }

    /**
     * Map from Customer from Dto to Db version.
     *
     * @param customerDto   customerDto
     * @return  customerDb
     */
    public CustomerDb toCustomerDb(CustomerDto customerDto) {
        CustomerDb customer = objectMapper.convertValue(customerDto, CustomerDb.class);
        return customer;
    }

    private URI toId(UUID identifier) {
        return URI.create(namespace + "/" + identifier);
    }


}
