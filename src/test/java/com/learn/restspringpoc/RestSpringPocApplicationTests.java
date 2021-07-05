package com.learn.restspringpoc;

import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest
class RestSpringPocApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void firstRestAPITest() {

        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";

        // Get the RequestSpecification of the request that you want to sent
        // to the server. The server is specified by the BaseURI that we have
        // specified in the above step.
        RequestSpecification httpRequest = RestAssured.given();

        // Make a request to the server by specifying the method Type and the method URL.
        // This will return the Response from the server. Store the response in a variable.
        Response response = httpRequest.request(Method.GET, "/posts");

        // Now let us print the body of the message to see what response
        // we have recieved from the server
        String responseBody = response.getBody().asString();
        log.info("Response Body is =>  " + responseBody);

        //or convert the response to a JSON
        io.restassured.path.json.JsonPath jsonPath = response.jsonPath();


        List<Object> allDetails = jsonPath.getList("$");
        //On how to navigate through this JSON body look here: https://github.com/json-path/JsonPath
        //Remeber there are two JSON path implementations- one by RestAssured and other by jayway. Use only one of them to avoid confusion. I prefer the latter
        log.info(allDetails.get(0).toString());

    }

    @Test
    void anotherBetterWay() {
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";

        io.restassured.path.json.JsonPath jsonPath = RestAssured
                .given()
                .when()
                .request(Method.GET, "/posts")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();
    }

    @Test
    void anotherWay() {
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";

        io.restassured.path.json.JsonPath jsonPath = RestAssured
                .given()
                .header("Content-Type", ContentType.JSON)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();

        log.info(jsonPath.prettify());
    }

    @Test
    void assertingResponse1() {
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";

        RestAssured
                .given()
                .header("Content-Type", ContentType.JSON)
                .when()
                .get("/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .body("userId[0]", equalTo(1));


    }

    @Test
    void assertingResponse2() {
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";

        String responseAsString = RestAssured
                .given()
                .header("Content-Type", ContentType.JSON)
                .when()
                .get("/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .asString();

        String firstUserId = JsonPath.parse(responseAsString).read("$[0].userId");

    }

    @Test
    void assertingPostResponse3() {
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";

        String responseAsString = RestAssured
                .given()
                .body("{\n" +
                        "    \"title\": \"foo\",\n" +
                        "    \"body\": \"body\",\n" +
                        "    \"userId\": \"12\"\n" +
                        "    }")
                .when()
                .post("/posts")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .response()
                .asString();

        String newId = JsonPath.parse(responseAsString).read("$..id");
        log.info(newId);
        JsonPath jsonPath =JsonPath.parse(responseAsString).json();

    }
}
