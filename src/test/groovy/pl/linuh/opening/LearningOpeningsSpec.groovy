package pl.linuh.opening

import groovy.util.logging.Log
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles
import pl.linuh.opening.application.OpeningApplication
import pl.linuh.opening.model.Opening
import pl.linuh.opening.model.OpeningGame
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

            assert openingRepository.count() == 0
            assert gameRepository.count() == 0

        when:
            with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4")
            def response = with().contentType("application/json").body([pieces: "white"]).post("/api/v1/test/openings/e4/games")
            def location = response.getHeader("Location")

        then:
            gameRepository.count() == 1
            OpeningGame storedGame  = gameRepository.findAll().first()
            location == "/api/v1/test/openings/e4/games/"+storedGame.id+"/0"
            storedGame.user.username == "test"
            storedGame.opening.name == "e4"
    }


    def "check start new game with black"(){
        given:

            assert openingRepository.count() == 0
            assert gameRepository.count() == 0

        when:
            with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4")
            def response = with().contentType("application/json").body([pieces: "black"]).post("/api/v1/test/openings/e4/games")
            def location = response.getHeader("Location")

        then:

        gameRepository.count() == 1
        OpeningGame storedGame  = gameRepository.findAll().first()
        location == "/api/v1/test/openings/e4/games/"+storedGame.id+"/1"
        storedGame.user.username == "test"
        storedGame.opening.name == "e4"
    }
    //test start with black

    def "check initial position with white"() {
        given:
        assert openingRepository.count() == 0
        assert gameRepository.count() == 0
        with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4")
        def response = with().contentType("application/json").body([pieces: "white"]).post("/api/v1/test/openings/e4/games")
        def location = response.getHeader("Location")

        when:
            def game = with().get(location).as(OpeningGame.class);

        then:
            game.pgn == "\n"
            game.id != null
            game.uuid != null
    }

    def "check initial position with black"() {
        given:
        assert openingRepository.count() == 0
        assert gameRepository.count() == 0
        with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4")
        def response = with().contentType("application/json").body([pieces: "black"]).post("/api/v1/test/openings/e4/games")
        def location = response.getHeader("Location")

        when:
        def game = with().get(location).as(OpeningGame.class);

        then:
        game.pgn == "\n1. e4 \n"
        game.id != null
        game.uuid != null
    }

    def "check first move is correct"() {
        given:
            assert openingRepository.count() == 0
            assert gameRepository.count() == 0
            with().contentType("application/json").body([name: 'e4', pgn: "1. e4 e5 2. Nf3 Nf6"]).put("/api/v1/test/openings/e4")
            def response = with().contentType("application/json").body([pieces: "white"]).post("/api/v1/test/openings/e4/games")
            def location = response.getHeader("Location")

        when:

            def postResult = with().contentType("application/json").body("e4").post(location);
            def resultLocation = postResult.getHeader("Location")

        then:
        gameRepository.count() == 1
        OpeningGame storedGame  = gameRepository.findAll().first()
            resultLocation == "/api/v1/test/openings/e4/games/"+storedGame.id+"/2";

    }

    @Before
    public clean(){
        gameRepository.deleteAll();
        openingRepository.deleteAll();
    }
    // helper methods
}