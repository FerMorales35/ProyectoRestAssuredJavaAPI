import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;


//Comentario
public abstract class BaseTest {

    private static final Logger logger = LogManager.getLogger(ReqRestTest.class);

    @BeforeClass
    public static void setUp(){
        logger.info("Iniciando la configuracion");
        RestAssured.requestSpecification = defaultRequestSpecification();
        logger.info("Configuration exitosa.");
    }

    private static RequestSpecification defaultRequestSpecification(){

        List<Filter> filters = new ArrayList<>();
        filters.add(new RequestLoggingFilter());
        filters.add(new ResponseLoggingFilter());
        filters.add(new AllureRestAssured());

        return new RequestSpecBuilder().setBaseUri(ConfVariables.getHost())
                .setBasePath(ConfVariables.getPath())
                .addFilters(filters)
                .setContentType(ContentType.JSON).build();

    }

    private RequestSpecification prodRequestSpecification(){

        List<Filter> filters = new ArrayList<>();
        filters.add(new RequestLoggingFilter());
        filters.add(new ResponseLoggingFilter());

        return new RequestSpecBuilder().setBaseUri(ConfVariables.getHost())
                .setBasePath(ConfVariables.getPath())
                .addFilters(filters)
                .setContentType(ContentType.JSON).build();

    }

    public ResponseSpecification defaultResponseSpecification(){

        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
