package com.dispassionproject.gutterball.controller

import com.dispassionproject.gutterball.BaseIntSpec
import com.dispassionproject.gutterball.api.GameStatus
import spock.lang.Unroll

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GameControllerSpec extends BaseIntSpec {

    def "should setup and start a new game with players"() {
        given:
        def playerNames = aRandom.playerNameSet(2)

        when:
        def game = createGame()

        then:
        game.id
        game.status == GameStatus.PENDING

        when:
        def gameId = game.id
        def playerOne = createPlayer(gameId, playerNames[0])
        def playerTwo = createPlayer(gameId, playerNames[1])

        then:
        playerOne.id
        playerOne.name == playerNames[0]
        playerTwo.id
        playerTwo.name == playerNames[1]

        when:
        def gameWithPlayers = getGame(gameId)

        then:
        gameWithPlayers.id == gameId
        gameWithPlayers.status == GameStatus.READY
        gameWithPlayers.players.size() == 2
        gameWithPlayers.players[0].id == playerOne.id
        gameWithPlayers.players[0].score == 0
        gameWithPlayers.players[1].id == playerTwo.id
        gameWithPlayers.players[1].score == 0

        when:
        def startedGame = startGame(gameId)

        then:
        startedGame.id == gameWithPlayers.id
        startedGame.status == GameStatus.STARTED
        startedGame.nextPlayer == 1
        startedGame.currentFrame == 1
        gameWithPlayers.players.size() == 2
    }

    def "should return 404 when retrieving non-existent game"() {
        given:
        def aNonExistentGameId = aRandom.gameId()

        when:
        getGame(aNonExistentGameId, status().isNotFound())

        then:
        noExceptionThrown()
    }

    def "should return 400 when adding a duplicate player"() {
        given:
        def game = setupPlayableGame()
        def existingPlayerName = game.getPlayers().get(0).name

        when:
        createPlayer(game.id, existingPlayerName, status().isBadRequest())

        then:
        noExceptionThrown()
    }

    def "should return 400 when adding a player to a full game"() {
        given:
        def game = setupPlayableGame(4)

        when:
        createPlayer(game.id, aRandom.playerName(), status().isBadRequest())

        then:
        noExceptionThrown()
    }

    def "should return 400 when starting a game with zero players"() {
        given:
        def game = createGame()

        when:
        startGame(game.id, status().isBadRequest())

        then:
        noExceptionThrown()
    }

    def "should return 403 when adding a player to a started game"() {
        given:
        def game = setupAndStartGame()

        expect:
        game.status == GameStatus.STARTED

        when:
        createPlayer(game.id, aRandom.playerName(), status().isForbidden())

        then:
        noExceptionThrown()
    }

    def "should return 404 when bowling a non-existent game"() {
        given:
        def aNonExistentGameId = aRandom.gameId()
        def aPlayerId = aRandom.playerId()
        def roll = aRandom.pins()

        when:
        bowl(aNonExistentGameId, aPlayerId, roll, status().isNotFound())

        then:
        noExceptionThrown()
    }

    def "should return 403 when bowling out of turn"() {
        given:
        def game = setupAndStartGame(2)
        def playerTwo = game.getPlayer(2)

        expect:
        game.nextPlayer == 1

        when:
        bowl(game.id, playerTwo.id, aRandom.pins(), status().isForbidden())

        then:
        noExceptionThrown()
    }

    def "should allow players to bowl frames in turn and advance to next frame after last player bowls"() {
        given:
        def game = setupAndStartGame(2)
        def playerOne = game.getPlayer(1)
        def playerTwo = game.getPlayer(2)

        expect:
        game.nextPlayer == 1
        game.currentFrame == 1

        when:
        bowl(game.id, playerOne.id, 6)
        game = bowl(game.id, playerOne.id, 2)

        then:
        game.nextPlayer == 2
        game.currentFrame == 1

        when:
        game = bowl(game.id, playerTwo.id, 10)

        then:
        game.nextPlayer == 1
        game.currentFrame == 2
    }

    @Unroll
    def "should bowl #description and expect score"() {
        given:
        def game = setupAndStartGame()
        def playerOne = game.getPlayer(1)

        when:
        rolls.each {
            bowl(game.id, playerOne.id, it as int)
        }
        def endGame = getGame(game.id)

        then:
        endGame.status == expectedStatus
        endGame.getPlayer(1).score == expectedScore
        endGame.currentFrame == expectedCurrentFrame

        where:
        description                          | rolls                           || expectedStatus       | expectedScore | expectedCurrentFrame
        "a half frame"                       | [aRandom.pins(9)]               || GameStatus.STARTED   | 0             | 1
        "a gutterball frame"                 | frames(1, 0)                    || GameStatus.STARTED   | 0             | 2
        "a 3/ then a _1 frame"               | [3, 7, 0, 1]                    || GameStatus.STARTED   | 11            | 3
        "a 3/ then a 1_ frame"               | [3, 7, 1, 0]                    || GameStatus.STARTED   | 12            | 3
        "a X, then a 3/ and a 1_ frame"      | [10, 3, 7, 1, 0]                || GameStatus.STARTED   | 32            | 4
        "3 consecutive Xs"                   | strikes(3)                      || GameStatus.STARTED   | 30            | 4
        "3 consecutive Xs, then a _1 frame"  | strikes(3) + [0, 1]             || GameStatus.STARTED   | 62            | 5
        "3 consecutive Xs, then a 1_ frame"  | strikes(3) + [1, 0]             || GameStatus.STARTED   | 63            | 5
        "a gutterball game"                  | frames(10, 0)                   || GameStatus.COMPLETED | 0             | 10
        "all 1s game"                        | frames(10, 1)                   || GameStatus.COMPLETED | 20            | 10
        "all spares, then a _"               | spares(10) + [0]                || GameStatus.COMPLETED | 127           | 10
        "all spares, then a 1"               | spares(10) + [1]                || GameStatus.COMPLETED | 128           | 10
        "all spares, then a X"               | spares(10) + [10]               || GameStatus.COMPLETED | 137           | 10
        "a perfect game"                     | strikes(12)                     || GameStatus.COMPLETED | 300           | 10
        "8 Xs, then a 3/, then _1 frame"     | strikes(8) + [3, 7, 0, 1]       || GameStatus.COMPLETED | 234           | 10
        "8 Xs, then a 3/, then 1_ frame"     | strikes(8) + [3, 7, 1, 0]       || GameStatus.COMPLETED | 235           | 10
        "8 Xs, then a 3/ and a 3/1 frame"    | strikes(8) + [3, 7, 3, 7, 1]    || GameStatus.COMPLETED | 247           | 10
        "8 Xs, then a 3/ and a X11 frame"    | strikes(8) + [3, 7, 10, 1, 1]   || GameStatus.COMPLETED | 255           | 10
        "8 Xs, then a 3/ and a X3/ frame"    | strikes(8) + [3, 7, 10, 3, 7]   || GameStatus.COMPLETED | 263           | 10
        "8 Xs, then a 3/ and a XX1 frame"    | strikes(8) + [3, 7, 10, 10, 1]  || GameStatus.COMPLETED | 264           | 10
        "8 Xs, then a 3/ and a XXX frame"    | strikes(8) + [3, 7, 10, 10, 10] || GameStatus.COMPLETED | 273           | 10
        "9 consecutive Xs, then a _1 frame"  | strikes(9) + [0, 1]             || GameStatus.COMPLETED | 242           | 10
        "9 consecutive Xs, then a 1_ frame"  | strikes(9) + [1, 0]             || GameStatus.COMPLETED | 243           | 10
        "9 consecutive Xs, then a 3/_ frame" | strikes(9) + [3, 7, 0]          || GameStatus.COMPLETED | 263           | 10
        "9 consecutive Xs, then a 3/1 frame" | strikes(9) + [3, 7, 1]          || GameStatus.COMPLETED | 264           | 10
        "9 consecutive Xs, then a 3/X frame" | strikes(9) + [3, 7, 10]         || GameStatus.COMPLETED | 273           | 10
        "10 consecutive Xs, then a _X frame" | strikes(10) + [0, 10]           || GameStatus.COMPLETED | 280           | 10
        "10 consecutive Xs, then a 3/ frame" | strikes(10) + [3, 7]            || GameStatus.COMPLETED | 283           | 10
        "10 consecutive Xs, then a _1 frame" | strikes(10) + [0, 1]            || GameStatus.COMPLETED | 271           | 10
        "10 consecutive Xs, then a 1_ frame" | strikes(10) + [1, 0]            || GameStatus.COMPLETED | 272           | 10
    }

    def "should handle final frame correctly"() {
        given:
        def game = setupAndStartGame()
        def playerOne = game.getPlayer(1)
        def rolls = strikes(10) + [1]

        and:
        rolls.each {
            bowl(game.id, playerOne.id, it as int)
        }

        when:
        bowl(game.id, playerOne.id, 10, status().isForbidden())

        then:
        noExceptionThrown()
    }

    def "should 400 when bowling in a game that hasn't started"() {
        given:
        def game = setupPlayableGame()
        def playerOne = game.getPlayer(1)

        when:
        bowl(game.id, playerOne.id, aRandom.pins(), status().isBadRequest())

        then:
        noExceptionThrown()
    }

    def "should 400 when bowling in a game that is completed"() {
        given:
        def game = setupAndStartGame()
        def playerOne = game.getPlayer(1)

        and:
        completeGame().each {
            bowl(game.id, playerOne.id, it as int)
        }
        def endGame = getGame(game.id)

        expect:
        endGame.status == GameStatus.COMPLETED

        when:
        bowl(game.id, playerOne.id, aRandom.pins(), status().isBadRequest())

        then:
        noExceptionThrown()
    }

    @Unroll
    def "should 400 when bowling #illegalPinCount pins"() {
        given:
        def game = setupAndStartGame()
        def playerOne = game.getPlayer(1)

        when:
        bowl(game.id, playerOne.id, illegalPinCount, status().isBadRequest())

        then:
        noExceptionThrown()

        where:
        illegalPinCount << [-1, 11]
    }

    def "should 403 when bowling more than 10 pins in a standard frame"() {
        given:
        def game = setupAndStartGame()
        def playerOne = game.getPlayer(1)

        and:
        bowl(game.id, playerOne.id, 6)

        when:
        bowl(game.id, playerOne.id, 5, status().isForbidden())

        then:
        noExceptionThrown()
    }

    def setupPlayableGame(int playerCount = 1) {
        def game = createGame()
        def playerNames = aRandom.playerNameSet(playerCount)
        playerNames.each {
            createPlayer(game.id, it)
        }
        def playableGame = getGame(game.id)
        assert playableGame.status == GameStatus.READY
        playableGame
    }

    def setupAndStartGame(int playerCount = 1) {
        startGame(setupPlayableGame(playerCount).id)
    }

    def completeGame() {
        def rolls = []
        9.times {
            def frame = aRandom.frame()
            rolls += frame
        }
        rolls += [8, 1]
    }

    static def frames(int frames, int pins) {
        def rolls = []
        (2 * frames).times {
            rolls << pins
        }
        rolls
    }

    static def spares(int frames) {
        def spares = []
        frames.times {
            spares += [3, 7]
        }
        spares
    }

    static def strikes(int frames) {
        def strikes = []
        frames.times {
            strikes << 10
        }
        strikes
    }

}
