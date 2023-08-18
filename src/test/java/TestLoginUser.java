import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestLoginUser {
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
    public void loginUser(){
        File json = new File("src/test/resources/User/newValideUser.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/auth/login");
        response.then().assertThat().body("accessToken",  notNullValue())
                .and()
                .statusCode(200);
    }
    @Test
    public void loginInvalideUser(){
        File json = new File("src/test/resources/User/repeatValideUser.json");
        Response response =
        given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/login");
        response.then().assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);


    }
    @AfterClass
    public static void deleteUser(){
        given().auth().oauth2(token).delete("/api/auth/user");
    }

}
