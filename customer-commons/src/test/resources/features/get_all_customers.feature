Feature: Get all Customers

  Scenario: The User requests all Customers
    Given that the User is looking for all Customers
    When they set the Accept header to "application/json"
    And they request GET /customer
    Then they receive a response with status code 200
    And they see that the response Content-Type header is "application/json"
    And they see that the response body is a JSON object with a list of Customers
    And they see that each Customer has an UUID
    And they see that each Customer has a name
    And they see that each Customer has a display name
    And they see that each Customer has an archive name
    And they see that each Customer has a CNAME
    And they see that each Customer has an Institution DNS
    And they see that each Customer has an Administration ID
    And they see that each Customer has a Feide Organization ID
    And they see that each Customer has a created date
    And they see that each Customer has a modified date
    And they see that each Customer has a contact
    And they see that each Customer has a logo file

  Scenario: The User requests all Customers but no Customers exist
    Given that the User is looking for all Customers but no Customers exist
    When they set the Accept header to "application/json"
    And they request GET /customer
    Then they receive a response with status code 200
    And they see that the response Content-Type header is "application/json"
    And they see that the response body is a JSON object with an empty Customers list

  Scenario: The persistence service is unavailable
    Given that the User requests all Customers but the persistence service i unavailable
    When they set the Accept header to "application/json"
    And they request GET /customer
    Then they receive a response with status code 502
    And they see that the response Content-Type header is "application/problem+json"
    And they see that the response body is a problem.json object
    And they see the response body has a field "title" with the value "Bad gateway"
    And they see the response body has a field "status" with the value "502"
    And they see the response body has a field "detail" with the value "Persistence service is unavailable"
