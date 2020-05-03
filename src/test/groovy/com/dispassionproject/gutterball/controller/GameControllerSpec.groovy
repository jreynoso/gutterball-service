package com.dispassionproject.gutterball.controller

import com.dispassionproject.gutterball.BaseIntSpec
import com.dispassionproject.gutterball.api.GameStatus

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GameControllerSpec extends BaseIntSpec {

    def "should setup and start a new game with players"() {
        given:
        def playerOneName = aRandom.playerName()
        def playerTwoName = aRandom.playerName()

        when:
        def game = createGame()

        then:
        game.id
        game.status == GameStatus.PENDING

        when:
        def gameId = game.id
        def playerOne = createPlayer(gameId, playerOneName)
        def playerTwo = createPlayer(gameId, playerTwoName)

        then:
        playerOne.id
        playerOne.name == playerOneName
        playerTwo.id
        playerTwo.name == playerTwoName

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
        startedGame.nextPlayer == playerOne.id
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
        def game = setupPlayableGame()
        game = startGame(game.id)

        expect:
        game.status == GameStatus.STARTED

        when:
        createPlayer(game.id, aRandom.playerName(), status().isForbidden())

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

}
