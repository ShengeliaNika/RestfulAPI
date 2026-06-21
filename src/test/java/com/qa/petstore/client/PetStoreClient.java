package com.qa.petstore.client;

import com.qa.petstore.config.ConfigurationManager;
import com.qa.petstore.models.Pet;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class PetStoreClient {

    private static final String PET_RESOURCE = "/pet";
    private static final String PET_BY_ID_RESOURCE = "/pet/{petId}";
    private static final String PET_BY_STATUS_RESOURCE = "/pet/findByStatus";

    private RequestSpecification requestSpecification() {
        return given()
                .baseUri(ConfigurationManager.getInstance().getBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    public Response createPet(Pet pet) {
        return requestSpecification()
                .body(pet)
                .when()
                .post(PET_RESOURCE);
    }

    public Response createPetWithRawBody(String rawBody) {
        return requestSpecification()
                .body(rawBody)
                .when()
                .post(PET_RESOURCE);
    }

    public Response getPetById(long petId) {
        return requestSpecification()
                .when()
                .get(PET_BY_ID_RESOURCE, petId);
    }

    public Response updatePet(Pet pet) {
        return requestSpecification()
                .body(pet)
                .when()
                .put(PET_RESOURCE);
    }

    public Response updatePetWithRawBody(String rawBody) {
        return requestSpecification()
                .body(rawBody)
                .when()
                .put(PET_RESOURCE);
    }

    public Response deletePetById(long petId) {
        return requestSpecification()
                .when()
                .delete(PET_BY_ID_RESOURCE, petId);
    }

    public Response findPetsByStatus(String status) {
        return requestSpecification()
                .queryParam("status", status)
                .when()
                .get(PET_BY_STATUS_RESOURCE);
    }
}
