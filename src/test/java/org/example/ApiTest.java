package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest
{
    static Map<String, String> headers= new HashMap<>();
    static String token;
    static String bookingID;
    @BeforeAll
    public static void PrepareTest()
    {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    @Order(1)
    @Test
    public void CheckServerUp()
    {
        given()
                .when()
                .get()
                .then()
                .statusCode(200);
    }

    @Order(2)
    @Test
    public void GetToken()
    {
        String body = "{\n" +
                "    \"username\": \"admin\",\n" +
                "    \"password\": \"password123\"\n" +
                "}";
        Response response =given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/auth")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        token = response.jsonPath().getString("token");
        headers.put("Cookie","token=" + token);
    }

    @Order(3)
    @Test
    public void CreateBooking()
    {
        String body = "{\n" +
                "    \"firstname\": \"Dmitriy\",\n" +
                "    \"lastname\": \"Smirnov\",\n" +
                "    \"totalprice\": 1000,\n" +
                "    \"depositpaid\": true,\n" +
                "    \"bookingdates\": {\n" +
                "        \"checkin\": \"2020-03-10\",\n" +
                "        \"checkout\": \"2020-03-20\"\n" +
                "    },\n" +
                "    \"additionalneeds\": \"Breakfast\"\n" +
                "}";
        Response response =given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/booking")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        bookingID = response.jsonPath().getString("bookingid");
        headers.put("bookingid",bookingID);
    }

    @Order(4)
    @Test
    public void GetBooking()
    {
        given()
                .when()
                .get("/booking/" + bookingID)
                .then()
                .statusCode(200);
    }

    @Order(5)
    @Test
    public void UpdateBooking()
    {
        String body = "{\n" +
                "    \"totalprice\": 2000\n" +
                "}";
        Response response = given()
                .headers(headers)
                .and()
                .body(body)
                .when()
                .patch("/booking/"+bookingID)
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Order(5)
    @Test
    public void DeleteBooking()
    {
        Response response = given()
                .headers(headers)
                .when()
                .delete("/booking/"+bookingID)
                .then()
                .extract().response();

        Assertions.assertEquals(201, response.statusCode());
    }
}
