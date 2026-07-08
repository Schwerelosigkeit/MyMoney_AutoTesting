package api;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // подумать как настроить логирование. и вообще этот класс
    }

}