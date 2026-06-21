package com.qa.petstore.hooks;

import com.qa.petstore.client.PetStoreClient;
import com.qa.petstore.context.ScenarioContext;
import com.qa.petstore.models.Pet;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;

public class Hooks {

    private final ScenarioContext scenarioContext;
    private final PetStoreClient petStoreClient;

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.petStoreClient = new PetStoreClient();
    }

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    @After
    public void tearDown() {
        Pet requestPet = scenarioContext.getRequestPet();
        if (requestPet != null && requestPet.getId() != null) {
            petStoreClient.deletePetById(requestPet.getId());
        }
        RestAssured.reset();
    }
}
