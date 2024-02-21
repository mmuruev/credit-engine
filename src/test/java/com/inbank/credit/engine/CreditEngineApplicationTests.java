package com.inbank.credit.engine;

import com.inbank.credit.engine.config.TestContainersConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

@Import(TestContainersConfiguration.class)
class CreditEngineApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final String DECISION_URL = "/api/decision/make";

    @Test
    void testAlreadyInDebt() throws Exception {
        var request = """
                {
                   "personalCode": "49002010965",
                   "loanAmount": "5000",
                   "months": 20
                }
                """;

        mockMvc.perform(post(DECISION_URL)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", equalTo("Negative decision")));
    }
    @Test
    void testPersonNotExists() throws Exception {
        var request = """
                {
                   "personalCode": "unknown",
                   "loanAmount": "5000",
                   "months": 20
                }
                """;

        mockMvc.perform(post(DECISION_URL)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", equalTo("No score for client")));
    }
    @Test
    void testPersonMinScoreSegmentOne_approve_less_amount() throws Exception {
        var request = """
                {
                   "personalCode": "49002010976",
                   "loanAmount": "5000",
                   "months": 20
                }
                """;

        mockMvc.perform(post(DECISION_URL)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvedAmount", equalTo(2000)))
                .andExpect(jsonPath("$.months", equalTo(20)))
        ;
    }
    @Test
    void testPersonMinScoreSegmentOne_new_range_approve() throws Exception {
        var request = """
                {
                   "personalCode": "49002010976",
                   "loanAmount": "5000",
                   "months": 12
                }
                """;

        mockMvc.perform(post(DECISION_URL)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvedAmount", equalTo(2000)))
                .andExpect(jsonPath("$.months", equalTo(20)))
        ;
    }
    @Test
    @Disabled("Todo.. remain to be fixed")
    void testPersonMinScoreSegmentOne_new_range_fail() throws Exception {
        var request = """
                {
                   "personalCode": "49002010976",
                   "loanAmount": "10000",
                   "months": 60
                }
                """;

        mockMvc.perform(post(DECISION_URL)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", equalTo("Negative decision")))
        ;
    }
    @Test
    void testPersonMinScoreSegmentThee_approve() throws Exception {
        var request = """
                {
                   "personalCode": "49002010998",
                   "loanAmount": "2000",
                   "months": 12
                }
                """;

        mockMvc.perform(post(DECISION_URL)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvedAmount", equalTo(10000)))
                .andExpect(jsonPath("$.months", equalTo(12)))
        ;
    }
}
