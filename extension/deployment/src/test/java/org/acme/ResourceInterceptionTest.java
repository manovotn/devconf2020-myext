package org.acme;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ResourceInterceptionTest {

    @RegisterExtension
    static QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClasses(TestResource.class)
            .addAsResource("application.properties"));

    @Test
    public void testResourceGetsIntercepted() {
        RestAssured.given()
                .when().get("/test")
                .then()
                .statusCode(200)
                .body(CoreMatchers.containsString("Your invocation got intercepted. ping"));
    }
}
