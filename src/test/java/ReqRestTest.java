import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ReqRestTest extends BaseTest {

    @Test
    public void loginTest(){

        BaseTest.setUp();
        given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void getSingleUserTest(){

        BaseTest.setUp();
        given()
                .get("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id",equalTo(2));
    }

    @Test
    public void deleteUserTest(){

        BaseTest.setUp();
        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void patchUserTest(){

        BaseTest.setUp();
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getString("name");

        assertThat(nameUpdated, equalTo("morpheus"));
    }

    @Test
    public void putUserTest(){

        BaseTest.setUp();
        String jobUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getString("job");

        assertThat(jobUpdated, equalTo("zion resident"));
    }

    @Test
    public void getAllUserTest(){

        BaseTest.setUp();
        Response response = given()
                .get("users?page=2");

        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + body);
        System.out.println("Content Type: " + contentType);

    }

    @Test
    public void getAllUsersTest2(){

        BaseTest.setUp();
        String response = given().when().get("users?page=2").then().extract().body().asString();

        int page = from(response).get("page");
        int totalPages = from(response).get("total_pages");
        int idFirstUser = from(response).get("data[0].id");

        System.out.println("Paginas: " + page);
        System.out.println("Total de paginas: " + totalPages);
        System.out.println("Id primer usuario: " + idFirstUser);

        List<Map> usersWithIdGreaterThan10 = from(response).get("data.findAll{ user -> user.id > 10}");
        String email = usersWithIdGreaterThan10.get(0).get("email").toString();
        System.out.println(email);

        List<Map> user = from(response).get("data.findAll{ user -> user.id > 10 && user.last_name == 'Howell'}");
        int id =  Integer.valueOf(user.get(0).get("id").toString());
        System.out.println(id);

    }

    @Test
    public void createUserTest(){

        BaseTest.setUp();
        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"Fernando\",\n" +
                        "    \"job\": \"QA\"\n" +
                        "}")
                .post("users")
                .then().extract().body().asString();

        User user = from(response).getObject("", User.class);
        System.out.println(user.getId());
        System.out.println(user.getJob());

    }

    @Test
    public void registerUserTest(){

        BaseTest.setUp();
        RegisterRequest user = new RegisterRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");

        RegisterResponse userResponse = given()
                .when()
                .body(user)
                .post("register")
                .then()
                .spec(defaultResponseSpecification())
                .contentType(equalTo("application/json; charset=utf-8"))
                .extract()
                .body()
                .as(RegisterResponse.class);

        MatcherAssert.assertThat(userResponse.getId(), equalTo(4));
        MatcherAssert.assertThat(userResponse.getToken(),equalTo("QpwL5tke4Pnpja7X4"));
    }
}

