package org.logbook.controller;

import org.junit.jupiter.api.Test;
import org.logbook.config.TestSecurityConfig;
import org.logbook.model.ActivityType;
import org.logbook.model.UserId;
import org.logbook.service.ActivityLogService;
import org.logbook.service.JwtService;
import org.logbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityLogService activityLogService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "test-user")
    void testGetGoals_returnsInitialGoals() throws Exception {
        mockMvc.perform(get("/user/goals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.pushups").exists())
                .andExpect(jsonPath("$.goals.situps").exists())
                .andExpect(jsonPath("$.goals.squats").exists())
                .andExpect(jsonPath("$.actual").doesNotExist());
    }

    @Test
    @WithMockUser(username = "test-user")
    void testGetTodaySummary_returnsActivitySummary() throws Exception {
        Map<ActivityType, Integer> summary = Map.of(
                ActivityType.PUSHUPS, 30,
                ActivityType.SITUPS, 40
        );

        when(activityLogService.getActivityLogsCountForType(new UserId("test-user")))
                .thenReturn(summary);

        mockMvc.perform(get("/user/today/summary")
                        .param("userId", "test-user")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.pushups").exists())
                .andExpect(jsonPath("$.goals.situps").exists())
                .andExpect(jsonPath("$.goals.squats").exists())
                .andExpect(jsonPath("$.actual.pushups").value(30))
                .andExpect(jsonPath("$.actual.situps").value(40));
    }
}
