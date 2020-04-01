Feature: Update an existing Customer

  Scenario:
    Given the user wants to update an existing Customer and has valid <Customer UUID> and valid <credentials>
    When they set the Content-Type header to "application/json"
    And they set the Accept header to "application/json"
    And they set the Authentication header to "Bearer <credentials>"
    And they set the request body to a JSON object describing the Customer
    And they request PUT /customer/<Customer UUUID>
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the response body contains a JSON object describing the Customer

  Scenario:
    Given the user wants to create a new Customer and has valid <Customer UUID> but has invalid <credentials>
    When they set the Content-Type header to "application/json"
    And they set the Accept header to "application/json"
    And they set the Authentication header to "Bearer <credentials>"
    And they set the request body to a JSON object describing the Customer
    And they request PUT /customer/<Customer UUUID>
    Then they receive a response with status code 401
    And they see that the response Content-Type header is "application/problem+json"
    And they see that the response body is a problem.json object
    And they see the response body has a field "title" with the value "Unauthorized"
    And they see the response body has a field "status" with the value "401"