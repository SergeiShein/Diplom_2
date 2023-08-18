import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestOrder {
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
    }
    @Test
    public void withoutAutorizationWithIngredients() {
        File json = new File("src/test/resources/Order/valideIngredient.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/orders");
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        response.then().assertThat().body("name", equalTo("Альфа-сахаридный метеоритный флюоресцентный традиционный-галактический бургер"));
    }
    @Test
    public void withoutAutorizationWithoutIngredients() {
        File json = new File("src/test/resources/Order/withoutIngredient.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/orders");
        response.then().assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }
    @Test
    public void withoutAutorizationWithInvalidIngredients() {
        File json = new File("src/test/resources/Order/invalideIngredient.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/orders");
        response.then().statusCode(500);
    }
    @Test
    public void withAutorizationWithIngredients() {
        File json = new File("src/test/resources/Order/valideIngredient.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .auth()
                        .oauth2(token)
                        .post("/api/orders");
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        response.then().assertThat().body("name", equalTo("Альфа-сахаридный метеоритный флюоресцентный традиционный-галактический бургер"));
    }
    @Test
    public void withAutorizationWithoutIngredients() {
        File json = new File("src/test/resources/Order/withoutIngredient.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .auth()
                        .oauth2(token)
                        .post("/api/orders");
        response.then().assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }
    @Test
    public void withAutorizationWithInvalidIngredients() {
        File json = new File("src/test/resources/Order/invalideIngredient.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .auth()
                        .oauth2(token)
                        .post("/api/orders");
        response.then().statusCode(500);
    }
    @AfterClass
    public static void deleteUser() {
        given().auth().oauth2(token).delete("/api/auth/user");
    }
}
