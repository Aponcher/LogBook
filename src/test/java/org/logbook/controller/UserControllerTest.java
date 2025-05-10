package org.logbook.controller;

import org.junit.jupiter.api.Test;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityGoals;
import org.logbook.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityLogService activityLogService;

    @Test
    void testGetGoals_returnsInitialGoals() throws Exception {
        mockMvc.perform(get("/user/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.pushups").value(25))
                .andExpect(jsonPath("$.goals.situps").value(25))
                .andExpect(jsonPath("$.goals.squats").value(10));
    }

    @Test
    void testGetTodaySummary_returnsActivitySummary() throws Exception {
        Map<ActivityType, Integer> summary = Map.of(
                ActivityType.PUSHUPS, 30,
                ActivityType.SITUPS, 40
        );

        when(activityLogService.getActivityLogsCountForType(new org.logbook.model.UserId("test-user")))
                .thenReturn(summary);

        mockMvc.perform(get("/user/today/summary")
                        .param("userId", "test-user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.pushups").value(25))
                .andExpect(jsonPath("$.goals.situps").value(25))
                .andExpect(jsonPath("$.goals.squats").value(10))
                .andExpect(jsonPath("$.actual.pushups").value(30))
                .andExpect(jsonPath("$.actual.situps").value(40));
    }
}
