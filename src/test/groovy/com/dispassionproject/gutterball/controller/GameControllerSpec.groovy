package com.dispassionproject.gutterball.controller

import com.dispassionproject.gutterball.BaseIntSpec
import com.dispassionproject.gutterball.api.GameStatus

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

}
