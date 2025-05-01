package org.logbook.controller;

import org.junit.jupiter.api.Test;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityLogService service;

    // Happy Path
    @Test
    void testLogPushups() throws Exception {
        ActivityLogEntry mockLog = new ActivityLogEntry(ActivityType.PUSHUPS, 10, "reps");
        mockLog.setId(UUID.randomUUID());

        when(service.logActivity(any(), anyLong(), anyString()))
                .thenReturn(mockLog);

        // NOTE: does not use the optional param 'unit' and doesn't cause an issue
        mockMvc.perform(post("/log/pushups?quantity=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("pushups"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.unit").value("reps"));
    }

    // Happy path: sleep
    @Test
    void testLogSleep() throws Exception {
        ActivityLogEntry mockLog = new ActivityLogEntry(ActivityType.SLEEP, 3, "hours");
        mockLog.setId(UUID.randomUUID());

        when(service.logActivity(any(), anyLong(), anyString()))
                .thenReturn(mockLog);

        mockMvc.perform(post("/log/sleep?quantity=10&unit=hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("sleep"))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.unit").value("hours"));
    }

    // Meh Path: Missing required param could still be an activity we want to log but of specific types
    // like ATE or something TODO make some sort of ENUM
    @Test
    void testLogEmptyRequest() throws Exception {
        ActivityLogEntry mockLog = new ActivityLogEntry(ActivityType.ATE, 0, "reps");
        mockLog.setId(UUID.randomUUID());

        when(service.logActivity(any(), anyLong(), anyString()))
                .thenReturn(mockLog);
        mockMvc.perform(post("/log/ate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("ate"))
                .andExpect(jsonPath("$.quantity").value(0))
                .andExpect(jsonPath("$.unit").value("reps"));
    }

    @Test
    void testLogBadRequestType() throws Exception {

        mockMvc.perform(post("/log/badType"))
                .andExpect(status().isBadRequest());
    }
}