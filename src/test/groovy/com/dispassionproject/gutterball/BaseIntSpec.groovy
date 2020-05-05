package com.dispassionproject.gutterball

import com.dispassionproject.gutterball.api.BowlRequest
import com.dispassionproject.gutterball.api.CreatePlayerRequest
import com.dispassionproject.gutterball.api.Game
import com.dispassionproject.gutterball.api.Player
import com.dispassionproject.gutterball.repository.GameRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import spock.lang.Shared

import javax.servlet.http.HttpServletResponse

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
class BaseIntSpec extends BaseSpec {

    @Shared
    boolean resetDatabase = true
    @Autowired
    GameRepository gameRepository

    @Autowired
    ObjectMapper objectMapper
    @Autowired
    MockMvc mvc

    def setup() {
        if (resetDatabase) {
            gameRepository.reset()
            resetDatabase = false;
        }
    }

    def createGame(ResultMatcher expectedStatus = status().isCreated()) {
        MvcResult result = mvc.perform(post("/game")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andReturn()

        responseToGame(result.getResponse())
    }

    def createPlayer(UUID gameId, String playerName, ResultMatcher expectedStatus = status().isCreated()) {
        def requestBody = CreatePlayerRequest.builder().name(playerName).build()
        MvcResult result = mvc.perform(post("/game/${gameId}/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toBytes(requestBody))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andReturn()

        responseToPlayer(result.getResponse())
    }

    def getGame(UUID gameId, ResultMatcher expectedStatus = status().isOk()) {
        MvcResult result = mvc.perform(get("/game/${gameId}")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andReturn()

        responseToGame(result.getResponse())
    }

    def startGame(UUID gameId, ResultMatcher expectedStatus = status().isOk()) {
        MvcResult result = mvc.perform(post("/game/${gameId}/start")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andReturn()

        responseToGame(result.getResponse())
    }

    def bowl(UUID gameId, UUID playerId, int pins, ResultMatcher expectedStatus = status().isOk()) {
        def requestBody = BowlRequest.builder().pins(pins).build()
        MvcResult result = mvc.perform(post("/game/${gameId}/player/${playerId}/bowl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toBytes(requestBody))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andReturn()

        responseToGame(result.getResponse())
    }

    def responseToGame(MockHttpServletResponse response) {
        isSuccessResponse(response) ? objectMapper.readValue(response.getContentAsByteArray(), Game) : null
    }

    def responseToPlayer(MockHttpServletResponse response) {
        isSuccessResponse(response) ? objectMapper.readValue(response.getContentAsByteArray(), Player) : null
    }

    def toBytes(Object object) {
        return objectMapper.writeValueAsBytes(object)
    }

    static def isSuccessResponse(MockHttpServletResponse response) {
        response.status == HttpServletResponse.SC_OK || response.status == HttpServletResponse.SC_CREATED
    }

}