package com.eot;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(DataProviderRunner.class)
public class MySecondTest {

    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;

    @BeforeClass
    public static void createRequestSpecification() {

        requestSpec = new RequestSpecBuilder()
                              .setBaseUri("http://api.zippopotam.us")
                              .log(LogDetail.ALL)
                              .build();

        responseSpec = new ResponseSpecBuilder()
                               .expectStatusCode(200)
                               .expectContentType(ContentType.JSON)
                               .log(LogDetail.ALL)
                               .build();
    }

    @Test
    public void getLocation() {
        given()
            .spec(requestSpec)
        .when()
            .get("http://zippopotam.us/IN/411006")
        .then()
            .spec(responseSpec)
            .assertThat()
            .body("places[0].'place name'", equalTo("Yerwadaa"))
        .and()
            .assertThat()
            .body("places", hasSize(2));
    }

    @Test
    public void getResponse() {
        ResponseBody resp = given()
            .spec(requestSpec)
        .when()
            .get("http://zippopotam.us/IN/411006")
        .then()
            .spec(responseSpec)
            .extract()
            .response().getBody();
        System.out.println("resp: " + resp.print());
    }

    @Test
    @UseDataProvider("zipCodesAndPlaces")
    public void getLocationAndPlaces(String countryCode, String zipCode, String expectedPlaceName
            , String expectedSize) {

        String actualPlace =
        given()
            .spec(requestSpec)
            .pathParam("country", countryCode)
            .pathParam("zip", zipCode)
            .queryParam("foo", "bar")
        .when()
            .get("http://zippopotam.us/{country}/{zip}")
        .then()
            .spec(responseSpec)
            .extract()
            .path("places[0].'place name'");

        Assert.assertEquals("place name incorrect", expectedPlaceName, actualPlace);
    }

    @DataProvider
    public static Object[][] zipCodesAndPlaces() {
        return new Object[][]{
                {"us", "90210", "Beverly Hills", "1"},
                {"us", "12345", "Schenectady", "1"},
                {"ca", "B2R", "Waverley", "1"},
                {"IN", "411006", "Yerwada", "2"}
        };
    }
}
