import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
 * Performance test for the Student entity.
 */
class StudentGatlingTest extends Simulation {

    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    // Log all HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
    // Log failed HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

    val baseURL = Option(System.getProperty("baseURL")) getOrElse """http://localhost:8080"""

    val httpConf = http
        .baseURL(baseURL)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

    val headers_http = Map(
        "Accept" -> """application/json"""
    )

    val headers_http_authentication = Map(
        "Content-Type" -> """application/json""",
        "Accept" -> """application/json"""
    )

    val headers_http_authenticated = Map(
        "Accept" -> """application/json""",
        "Authorization" -> "${access_token}"
    )

    val scn = scenario("Test the Student entity")
        .exec(http("First unauthenticated request")
        .get("/api/account")
        .headers(headers_http)
        .check(status.is(401))).exitHereIfFailed
        .pause(10)
        .exec(http("Authentication")
        .post("/api/authenticate")
        .headers(headers_http_authentication)
        .body(StringBody("""{"username":"admin", "password":"admin"}""")).asJSON
        .check(header.get("Authorization").saveAs("access_token"))).exitHereIfFailed
        .pause(1)
        .exec(http("Authenticated request")
        .get("/api/account")
        .headers(headers_http_authenticated)
        .check(status.is(200)))
        .pause(10)
        .repeat(2) {
            exec(http("Get all students")
            .get("/api/students")
            .headers(headers_http_authenticated)
            .check(status.is(200)))
            .pause(10 seconds, 20 seconds)
            .exec(http("Create new student")
            .post("/api/students")
            .headers(headers_http_authenticated)
            .body(StringBody("""{"id":null, "firstName":"SAMPLE_TEXT", "lastName":"SAMPLE_TEXT", "fatherName":"SAMPLE_TEXT", "motherName":"SAMPLE_TEXT", "studentId":"SAMPLE_TEXT", "email":"SAMPLE_TEXT", "phoneNumber":"SAMPLE_TEXT", "photo":null, "photoType":"SAMPLE_TEXT", "photoName":"SAMPLE_TEXT", "signature":null, "signatureType":"SAMPLE_TEXT", "signatureName":"SAMPLE_TEXT", "dob":"2020-01-01T00:00:00.000Z", "nid":"SAMPLE_TEXT", "birthCertNo":"SAMPLE_TEXT", "gender":null, "createDate":"2020-01-01T00:00:00.000Z", "updateDate":"2020-01-01T00:00:00.000Z", "createBy":"0", "updateBy":"0"}""")).asJSON
            .check(status.is(201))
            .check(headerRegex("Location", "(.*)").saveAs("new_student_url"))).exitHereIfFailed
            .pause(10)
            .repeat(5) {
                exec(http("Get created student")
                .get("${new_student_url}")
                .headers(headers_http_authenticated))
                .pause(10)
            }
            .exec(http("Delete created student")
            .delete("${new_student_url}")
            .headers(headers_http_authenticated))
            .pause(10)
        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(rampUsers(Integer.getInteger("users", 100)) over (Integer.getInteger("ramp", 1) minutes))
    ).protocols(httpConf)
}
