package api;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = "https://mymoney-webapp.onrender.com";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

}