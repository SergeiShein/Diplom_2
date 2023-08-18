import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestUpdateUser {
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
    public void withoutAutorization() {
        File json = new File("src/test/resources/User/newValideUser.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .patch("/api/auth/user");
        response.then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
    @Test
    public void withAutorization(){
        File json = new File("src/test/resources/User/repeatValideUser.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .auth()
                        .oauth2(token)
                        .patch("/api/auth/user");
        response.then().assertThat().body("success",  Matchers.equalTo(true))
                .and()
                .statusCode(200);
        response.then().assertThat().body("user.email",  Matchers.equalTo("sergeyshein1992@yandex.ru"));
        response.then().assertThat().body("user.name",  Matchers.equalTo("Sergeyshein3132"));
    }
    @AfterClass
    public static void deleteUser() {
        given().auth().oauth2(token).delete("/api/auth/user");
    }
}
