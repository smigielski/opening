package pl.linuh.opening

import com.jayway.restassured.RestAssured
import groovy.util.logging.Log
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

            with().body(opening).put("/api/v1/test/openings/e4");

        then:
            openingRepository.count() == 1
            Opening storedOening  = openingRepository.findAll().first()
            opening.creationDate != null
            opening.pgn == "1. e4 e5 2. Nf3 Nf6"
            log.info("opening with name "+opening.name +" uploaded at "+ opening.creationDate);
    }


    // helper methods
}