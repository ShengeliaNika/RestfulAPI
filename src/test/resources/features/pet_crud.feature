Feature: Pet resource management

  As a client of the Pet Store API
  I want to create, retrieve, update and delete pets
  So that I can manage the pet inventory through the REST API

  Background:
    Given the Pet Store API base URL is configured

  @smoke @create @positive
  Scenario: Create a new pet successfully
    Given a new pet payload with name "Buddy" and status "available"
    When the client submits a request to create the pet
    Then the response status code should be 200
    And the response content type should be "application/json"
    And the response body should contain the submitted pet details

  @create @negative
  Scenario: Fail to create a pet with a malformed request body
    Given a malformed pet request body
    When the client submits a request to create the pet
    Then the response status code should be 400

  @read @positive
  Scenario: Retrieve an existing pet by its id
    Given a new pet payload with name "Rex" and status "available"
    And the client submits a request to create the pet
    When the client requests the pet by its id
    Then the response status code should be 200
    And the response content type should be "application/json"
    And the response body should contain the submitted pet details

  @read @negative
  Scenario: Fail to retrieve a pet that does not exist
    Given a pet id that does not exist
    When the client requests the pet by its id
    Then the response status code should be 404

  @read @positive
  Scenario: Retrieve all pets filtered by status
    When the client requests pets filtered by status "available"
    Then the response status code should be 200
    And the response content type should be "application/json"
    And every pet in the response should have status "available"

  @update @positive
  Scenario: Update an existing pet's name and status
    Given a new pet payload with name "Max" and status "available"
    And the client submits a request to create the pet
    When the client updates the pet with name "Maximus" and status "sold"
    Then the response status code should be 200
    And the response body should contain the name "Maximus"
    And the response body should contain the status "sold"

  @update @negative
  Scenario: Fail to update a pet with a malformed request body
    Given a malformed pet request body
    When the client submits a request to update the pet
    Then the response status code should be 400

  @delete @positive
  Scenario: Delete an existing pet successfully
    Given a new pet payload with name "Luna" and status "available"
    And the client submits a request to create the pet
    When the client deletes the pet
    Then the response status code should be 200
    And requesting the deleted pet should return status code 404

  @delete @negative
  Scenario: Fail to delete a pet that does not exist
    Given a pet id that does not exist
    When the client deletes the pet
    Then the response status code should be 404
