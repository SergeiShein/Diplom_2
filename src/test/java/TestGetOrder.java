import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestGetOrder {
    private static String token;
    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        File json = new File("src/test/resources/User/newValideUser.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/auth/register");
        token = response.path("accessToken");
        String [] tokenData = token.split(" ");
        token = tokenData[1];
        File orderJson = new File("src/test/resources/Order/valideIngredient.json");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(orderJson)
                .when()
                .auth()
                .oauth2(token)
                .post("/api/orders");
        File orderFirstJson = new File("src/test/resources/Order/firstValideIngredient.json");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(orderFirstJson)
                .when()
                .auth()
                .oauth2(token)
                .post("/api/orders");
        File orderSecondJson = new File("src/test/resources/Order/secondValideIngredient.json");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(orderSecondJson)
                .when()
                .auth()
                .oauth2(token)
                .post("/api/orders");
    }
    @Test
    public void withAutorizationGetOrders() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .auth()
                        .oauth2(token)
                        .get("/api/orders");
        List<HashMap> orders = response.jsonPath().getList("orders");
        Assert.assertEquals(3, orders.size());
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    public void withoutAutorizationGetOrders() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/orders");
        response.then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
    @AfterClass
    public static void deleteUser() {
        given().auth().oauth2(token).delete("/api/auth/user");
    }
}
