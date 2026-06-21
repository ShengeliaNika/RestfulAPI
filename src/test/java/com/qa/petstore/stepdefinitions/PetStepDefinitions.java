package com.qa.petstore.stepdefinitions;

import com.qa.petstore.client.PetStoreClient;
import com.qa.petstore.config.ConfigurationManager;
import com.qa.petstore.context.ScenarioContext;
import com.qa.petstore.models.Pet;
import com.qa.petstore.utils.PetTestDataFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetStepDefinitions {

    private final ScenarioContext scenarioContext;
    private final PetStoreClient petStoreClient;

    public PetStepDefinitions(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.petStoreClient = new PetStoreClient();
    }

    @Given("the Pet Store API base URL is configured")
    public void theApiBaseUrlIsConfigured() {
        assertNotNull(ConfigurationManager.getInstance().getBaseUrl());
    }

    @Given("a new pet payload with name {string} and status {string}")
    public void aNewPetPayloadWithNameAndStatus(String name, String status) {
        scenarioContext.setRequestPet(PetTestDataFactory.buildPet(name, status));
    }

    @Given("a malformed pet request body")
    public void aMalformedPetRequestBody() {
        scenarioContext.setRawRequestBody(PetTestDataFactory.malformedPetJson());
    }

    @Given("a pet id that does not exist")
    public void aPetIdThatDoesNotExist() {
        scenarioContext.setNonExistentPetId(PetTestDataFactory.generateId());
    }

    @When("the client submits a request to create the pet")
    public void theClientSubmitsARequestToCreateThePet() {
        Response response = scenarioContext.getRawRequestBody() != null
                ? petStoreClient.createPetWithRawBody(scenarioContext.getRawRequestBody())
                : petStoreClient.createPet(scenarioContext.getRequestPet());
        scenarioContext.setLastResponse(response);
    }

    @When("the client requests the pet by its id")
    public void theClientRequestsThePetById() {
        scenarioContext.setLastResponse(petStoreClient.getPetById(resolvePetId()));
    }

    @When("the client requests pets filtered by status {string}")
    public void theClientRequestsPetsFilteredByStatus(String status) {
        scenarioContext.setLastResponse(petStoreClient.findPetsByStatus(status));
    }

    @When("the client updates the pet with name {string} and status {string}")
    public void theClientUpdatesThePetWithNameAndStatus(String name, String status) {
        Pet updatedPet = scenarioContext.getRequestPet().toBuilder()
                .name(name)
                .status(status)
                .build();
        scenarioContext.setRequestPet(updatedPet);
        scenarioContext.setLastResponse(petStoreClient.updatePet(updatedPet));
    }

    @When("the client submits a request to update the pet")
    public void theClientSubmitsARequestToUpdateThePet() {
        Response response = scenarioContext.getRawRequestBody() != null
                ? petStoreClient.updatePetWithRawBody(scenarioContext.getRawRequestBody())
                : petStoreClient.updatePet(scenarioContext.getRequestPet());
        scenarioContext.setLastResponse(response);
    }

    @When("the client deletes the pet")
    public void theClientDeletesThePet() {
        scenarioContext.setLastResponse(petStoreClient.deletePetById(resolvePetId()));
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertEquals(expectedStatusCode, scenarioContext.getLastResponse().getStatusCode());
    }

    @Then("the response content type should be {string}")
    public void theResponseContentTypeShouldBe(String expectedContentType) {
        assertTrue(scenarioContext.getLastResponse().getContentType().contains(expectedContentType));
    }

    @Then("the response body should contain the submitted pet details")
    public void theResponseBodyShouldContainTheSubmittedPetDetails() {
        Pet expectedPet = scenarioContext.getRequestPet();
        Pet actualPet = scenarioContext.getLastResponse().as(Pet.class);
        assertEquals(expectedPet.getId(), actualPet.getId());
        assertEquals(expectedPet.getName(), actualPet.getName());
        assertEquals(expectedPet.getStatus(), actualPet.getStatus());
    }

    @Then("every pet in the response should have status {string}")
    public void everyPetInTheResponseShouldHaveStatus(String expectedStatus) {
        Pet[] pets = scenarioContext.getLastResponse().as(Pet[].class);
        assertTrue(pets.length > 0);
        for (Pet pet : pets) {
            assertEquals(expectedStatus, pet.getStatus());
        }
    }

    @Then("the response body should contain the name {string}")
    public void theResponseBodyShouldContainTheName(String expectedName) {
        Pet actualPet = scenarioContext.getLastResponse().as(Pet.class);
        assertEquals(expectedName, actualPet.getName());
    }

    @Then("the response body should contain the status {string}")
    public void theResponseBodyShouldContainTheStatus(String expectedStatus) {
        Pet actualPet = scenarioContext.getLastResponse().as(Pet.class);
        assertEquals(expectedStatus, actualPet.getStatus());
    }

    @Then("requesting the deleted pet should return status code {int}")
    public void requestingTheDeletedPetShouldReturnStatusCode(int expectedStatusCode) {
        Response response = petStoreClient.getPetById(scenarioContext.getRequestPet().getId());
        assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @Then("the response body should match the pet schema")
    public void theResponseBodyShouldMatchThePetSchema() {
        scenarioContext.getLastResponse().then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/pet-schema.json"));
    }

    @Then("the response body should match the pet list schema")
    public void theResponseBodyShouldMatchThePetListSchema() {
        scenarioContext.getLastResponse().then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/pet-list-schema.json"));
    }

    private long resolvePetId() {
        return scenarioContext.getRequestPet() != null
                ? scenarioContext.getRequestPet().getId()
                : scenarioContext.getNonExistentPetId();
    }
}
