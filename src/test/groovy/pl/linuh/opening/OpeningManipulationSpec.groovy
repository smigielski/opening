package pl.linuh.opening

import com.jayway.restassured.RestAssured
import groovy.util.logging.Log
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.linuh.opening.application.OpeningApplication
import pl.linuh.opening.model.Opening
import pl.linuh.opening.repositories.OpeningRepository
import spock.lang.Specification
import static com.jayway.restassured.RestAssured.*
import static com.jayway.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*

@Log
@SpringApplicationConfiguration(OpeningApplication.class)
@WebIntegrationTest
@ActiveProfiles("test")
class OpeningManipulationSpec extends Specification {


    @Autowired
    OpeningRepository openingRepository

    def "upload opening theory"() {
        given:
            assert openingRepository.count() == 0
            def opening = [name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]
        when:

            with().contentType("application/json").body(opening).put("/api/v1/test/openings/e4");

        then:
            openingRepository.count() == 1
            Opening storedOpening  = openingRepository.findAll().first()
            storedOpening.creationDate != null
            storedOpening.creationDate == storedOpening.lastModifiedDate
            storedOpening.pgn == "1. e4 e5 2. Nf3 Nf6"
            storedOpening.user.username == "test"
            log.info("opening with name "+storedOpening.name +" uploaded at "+ storedOpening.creationDate);
    }

    def "get opening should be possible"() {
        given:
            assert openingRepository.count() == 0

            with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4");
        when:
            def opening = with().get("/api/v1/test/openings/e4").as(Opening.class)
        then:
            opening.creationDate != null
            opening.pgn == "1. e4 e5 2. Nf3 Nf6"
            opening.id != null
            opening.uuid != null
    }

    def "update opening should not create new one"() {
        given:
        assert openingRepository.count() == 0
            with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4");
        when:
            def opening = with().get("/api/v1/test/openings/e4").as(Opening.class)
            opening.pgn = "1. e4 e5 2. Nf3 Nf6 3. Bb5"
            with().contentType("application/json").body(opening).put("/api/v1/test/openings/e4");
        then:
        openingRepository.count() == 1
        Opening storedOpening  = openingRepository.findAll().first()
        storedOpening.pgn == "1. e4 e5 2. Nf3 Nf6 3. Bb5"
        storedOpening.creationDate != storedOpening.lastModifiedDate
    }

    def "delete opening is working"() {
        given:
            assert openingRepository.count() == 0

            with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4");
        when:
            assert openingRepository.count() == 1
            with().delete("/api/v1/test/openings/e4");
        then:
            openingRepository.count() == 0
    }

    @Before
    public clean(){
        openingRepository.deleteAll();
    }
    // helper methods
}