package com.qa.petstore.context;

import com.qa.petstore.models.Pet;
import io.restassured.response.Response;
import lombok.Data;

@Data
public class ScenarioContext {

    private Pet requestPet;
    private String rawRequestBody;
    private Response lastResponse;
    private long nonExistentPetId;
}
