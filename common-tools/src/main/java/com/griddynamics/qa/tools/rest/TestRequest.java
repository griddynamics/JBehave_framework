package com.griddynamics.qa.tools.rest;

import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;


public class TestRequest {

    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNATHORIZED_REQUEST = 401;
    public static final int HTTP_FORBIDDEN_REQUEST = 403;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;
    public static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";


    private String url;
    private Boolean isLogging = false;
    private Response response;

    public TestRequest() {
        super();
    }

    public TestRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsLogging() {
        return isLogging;
    }

    public void setIsLogging(Boolean isLogging) {
        this.isLogging = isLogging;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public void get() {
        response = isLogging ? given().log().body().expect().log().body().get(url) : with().get(url);
    }

    public void post(String body) {
        response = isLogging ? given().log().body().body(body).expect().log().body().when().post(url) : with().body(body).post(url);
    }

    public void post(Map<String, ?> props) {
        response = isLogging ? given().log().parameters().formParameters(props).expect().log().body().when().post(url)
                : with().formParameters(props).post(url);
    }

    public void post(Object object) {
        response = isLogging ? given().log().body().body(object, ObjectMapperType.GSON).expect().log().body().when().post(url) : with().body(object, ObjectMapperType.GSON).post(url);
    }

    public void soap(String body) {
        response = isLogging ? given().log().body().body(body).contentType(CONTENT_TYPE_XML).expect().log().body().when().post(url) : with().body(body).contentType(CONTENT_TYPE_XML).post(url);
    }

    public void delete() {
        response = isLogging ? given().expect().log().body().when().delete(url) : with().delete(url);
    }

    public void put(String body) {
        response = isLogging ? given().log().body().body(body).expect().log().body().put(url) : with().body(body).put(url);
    }

    public void put(Object object) {
        response = isLogging ? given().log().body().body(object, ObjectMapperType.GSON).expect().log().body().put(url) : with().body(object, ObjectMapperType.GSON).put(url);
    }

    public boolean isStatusCode200() {
        return response.getStatusCode() == HTTP_OK;
    }

    public boolean isStatusCode400() {
        return response.getStatusCode() == HTTP_BAD_REQUEST;
    }

    public boolean isStatusCode401() {
        return response.getStatusCode() == HTTP_UNATHORIZED_REQUEST;
    }

    public boolean isStatusCode403() {
        return response.getStatusCode() == HTTP_FORBIDDEN_REQUEST;
    }

    public boolean isStatusCode503() {
        return response.getStatusCode() == HTTP_SERVICE_UNAVAILABLE;
    }

    public <T> T getResponseObject(Class<T> cls) {
        return getResponse().as(cls, ObjectMapperType.GSON);
    }

    public String getResponseAsString() {
        return com.jayway.restassured.RestAssured.get(url).asString();
    }

    public String getBodyAsString() {
        return response.getBody().asString();
    }

    public void postMultipart(String... bodies) {
        RequestSpecification req = given();
        for (int i = 0; i < bodies.length; i++) {
            req = req.multiPart("file", bodies[i]);
        }
        response = req.when().post(url);
    }

}
