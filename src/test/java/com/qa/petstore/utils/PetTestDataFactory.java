package com.qa.petstore.utils;

import com.qa.petstore.models.Category;
import com.qa.petstore.models.Pet;
import com.qa.petstore.models.Tag;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PetTestDataFactory {

    private static final long MIN_ID = 1_000_000_000L;
    private static final long MAX_ID = 1_999_999_999L;
    private static final String MALFORMED_PET_JSON = "{ \"name\": \"broken\", \"status\": ";

    private PetTestDataFactory() {
    }

    public static String malformedPetJson() {
        return MALFORMED_PET_JSON;
    }

    public static Pet buildPet(String name, String status) {
        long id = generateId();
        return Pet.builder()
                .id(id)
                .name(name)
                .status(status)
                .category(Category.builder().id(id).name("automation-category").build())
                .photoUrls(List.of("https://example.com/photo.jpg"))
                .tags(List.of(Tag.builder().id(id).name("automation-tag").build()))
                .build();
    }

    public static long generateId() {
        return ThreadLocalRandom.current().nextLong(MIN_ID, MAX_ID);
    }
}
