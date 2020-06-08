package com.eot;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(DataProviderRunner.class)
public class MyFirstTest {

    @Test
    public void getLocation() {
        given()
            .log().all()
        .when()
            .get("http://zippopotam.us/IN/411006")
        .then()
            .log().all(true)
            .assertThat()
            .body("places[0].'place name'", equalTo("Yerwada"))
        .and()
            .assertThat()
            .body("places", hasSize(2));
    }

    @Test
    @UseDataProvider("zipCodesAndPlaces")
    public void getLocationAndPlaces(String countryCode, String zipCode, String expectedPlaceName
            , String expectedSize) {
        given()
            .pathParam("country", countryCode)
            .pathParam("zip", zipCode)
            .queryParam("foo", "bar")
            .log().all()
        .when()
            .get("http://zippopotam.us/{country}/{zip}")
        .then()
            .log().all(true)
            .assertThat()
            .body("places[0].'place name'", equalTo(expectedPlaceName))
        .and()
            .assertThat()
            .body("places", hasSize(Integer.parseInt(expectedSize)));
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
