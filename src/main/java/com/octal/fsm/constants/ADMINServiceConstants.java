package com.octal.fsm.constants;

public class ADMINServiceConstants {
    public static final String ADMIN_MICROSERVICE = "ADMIN-SERVICE";
    public static final String ADMIN_BASE_URL = "http://localhost:9083/";
    public static final String GET_EMAIL_TEM_BY_ID = "/get/by/{id}";
    public static final String GET_EMAIL_BY_SLUG = "/email-template/get/by/slug";

    private ADMINServiceConstants() {
        throw new AssertionError("Utility class should not be instantiated.");
    }
}
