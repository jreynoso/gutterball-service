package com.dispassionproject.gutterball

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
class BaseIntSpec extends BaseSpec {

    @Autowired
    ObjectMapper objectMapper
    @Autowired
    MockMvc mvc

    def createGame(ResultMatcher expectedStatus = status().isCreated()) {
        MvcResult result = mvc.perform(post("/game")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(expectedStatus)
                .andReturn()

        responseToGame(result.getResponse())
    }

    def createPlayer(UUID gameId, String playerName, ResultMatcher expectedStatus = status().isCreated()) {
        MvcResult result = mvc.perform(post("/game/${gameId}/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playerName)
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

    def responseToGame(MockHttpServletResponse response) {
        objectMapper.readValue(response.getContentAsByteArray(), Game)
    }

    def responseToPlayer(MockHttpServletResponse response) {
        objectMapper.readValue(response.getContentAsByteArray(), Player)
    }

}