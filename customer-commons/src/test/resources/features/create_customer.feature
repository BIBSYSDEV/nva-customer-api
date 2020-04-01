Feature: Create a new Customer

  Scenario:
    Given the user wants to create a new Customer and has valid <credentials>
    When they set the Content-Type header to "application/json"
    And they set the Accept header to "application/json"
    And they set the Authentication header to "Bearer <credentials>"
    And they set the request body to a JSON object describing the Customer
    And they request POST /customer
    Then they receive a response with status code 201
    And they see that the response Content-type is "application/json"
    And they see the response body contains a JSON object describing the Customer
    And they see the Customer has a UUID

  Scenario:
    Given the user wants to create a new Customer but has invalid <credentials>
    When they set the Content-Type header to "application/json"
    And they set the Accept header to "application/json"
    And they set the Authentication header to "Bearer <credentials>"
    And they set the request body to a JSON object describing the Customer
    And they request POST /customer
    Then they receive a response with status code 401
    And they see that the response Content-Type header is "application/problem+json"
    And they see that the response body is a problem.json object
    And they see the response body has a field "title" with the value "Unauthorized"
    And they see the response body has a field "status" with the value "401"

  Scenario:
    Given the user wants to create a new malformed Customer and has valid <credentials>
    When they set the Content-Type header to "application/json"
    And they set the Accept header to "application/json"
    And they set the Authentication header to "Bearer <credentials>"
    And they set the request body to a JSON object describing the malformed Customer
    And they request POST /customer
    Then they receive a response with status code 400
    And they see that the response body is a problem.json object
    And they see the response body has a field "title" with the value "Bad request"
    And they see the response body has a field "status" with the value "400"

  Scenario: The persistence service is unavailable
    Given the user wants to create a new Customer and has valid <credentials> but the persistence service is unavailable
    When they set the Content-Type header to "application/json"
    And they set the Accept header to "application/json"
    And they set the Authentication header to "Bearer <credentials>"
    And they set the request body to a JSON object describing the Customer
    And they request POST /customer/
    Then they receive a response with status code 502
    And they see that the response Content-Type header is "application/problem+json"
    And they see that the response body is a problem.json object
    And they see the response body has a field "title" with the value "Bad gateway"
    And they see the response body has a field "status" with the value "502"
    And they see the response body has a field "detail" with the value "Persistence service is unavailable"