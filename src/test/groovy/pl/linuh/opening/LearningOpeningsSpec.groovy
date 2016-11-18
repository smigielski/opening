package pl.linuh.opening

import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles
import pl.linuh.opening.application.OpeningApplication
import pl.linuh.opening.repositories.GameRepository
import pl.linuh.opening.repositories.OpeningRepository
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.*
import static com.jayway.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*

@Log
@SpringApplicationConfiguration(OpeningApplication.class)
@WebIntegrationTest
@ActiveProfiles("test")
class LearningOpeningsSpec extends Specification {


    @Autowired
    OpeningRepository openingRepository

    @Autowired
    GameRepository gameRepository

    def "check start new game with white"(){
        given:
            username = "test"
            openingName = "e4"
            with().post("/api/v1/"+username+"/openings/"+openingName);
            assert openingRepository.count() == 1
            assert gameRepository.count() == 0
        when:
            location = with().post("/api/v1/"+username+"/openings/"+openingName);

        then:
            location == "/api/v1/"+username+"/openings/"+openingName+"/{game}/1"
            gameRepository.count() == 1
    }

    //test start with black

    def "check initial position with white"() {
        given:
            username = "test"
            openingName = "e4"
            with().post("/api/v1/"+username+"/openings/"+openingName);
            assert openingRepository.count() == 1
            assert gameRepository.count() == 0
        when:
            location = with().post("/api/v1/"+username+"/openings/"+openingName);
            game = with().get(location);

        then:
            game.pgn == ""

    }

    def "check first move is correct"() {
        given:
            username = "test"
            openingName = "e4"
            with().post("/api/v1/"+username+"/openings/"+openingName);
            assert openingRepository.count() == 1
            assert gameRepository.count() == 0

        when:
            location = with().post("/api/v1/"+username+"/openings/"+openingName);
            postResult = with().post(location);
            location = ""

        then:
            localtion == "/api/v1/"+username+"/openings/"+openingName+"/{game}/3"

    }


    // helper methods
}