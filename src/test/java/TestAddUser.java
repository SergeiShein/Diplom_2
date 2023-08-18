import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestAddUser {
    private static String token;
    private static String tokenRepeat;
    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void createNewUser(){
        File json = new File("src/test/resources/User/newValideUser.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().body("accessToken",  notNullValue())
                .and()
                .statusCode(200);
        token = response.path("accessToken");
        String [] tokenData = token.split(" ");
        token = tokenData[1];

    }
    @Test
    public void createRepeatUser(){
        File json = new File("src/test/resources/User/repeatValideUser.json");
        Response registerResponse =
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(json)
                    .when()
                    .post("/api/auth/register");
        Response repeatResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/auth/register");
        repeatResponse.then().assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
        tokenRepeat = registerResponse.path("accessToken");
        String [] tokenData = tokenRepeat.split(" ");
        tokenRepeat = tokenData[1];
    }
    @Test
    public void createInvalidUser(){
        File json = new File("src/test/resources/User/invalideUser.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
    @AfterClass
    public static void deleteUser(){
        given().auth().oauth2(token).delete("/api/auth/user");
        given().auth().oauth2(tokenRepeat).delete("/api/auth/user");
    }
}
