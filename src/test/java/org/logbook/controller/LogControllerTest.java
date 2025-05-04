package org.logbook.controller;

import org.junit.jupiter.api.Test;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        RestActivityLogEntry mockLog =
                RestActivityLogEntry.builder()
                        .type(ActivityType.PUSHUPS.getValue())
                        .quantity(10)
                        .unit("reps")
                        .build();
                new ActivityLogEntry(ActivityType.PUSHUPS, 10, "reps");

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
        RestActivityLogEntry mockLog =
                RestActivityLogEntry.builder()
                .type(ActivityType.SLEEP.getValue())
                .quantity(3)
                .unit("hours")
                .build();

        when(service.logActivity(any(), anyLong(), anyString()))
                .thenReturn(mockLog);

        mockMvc.perform(post("/log/sleep?quantity=10&unit=hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("sleep"))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.unit").value("hours"));
    }

    // MEH Path: Missing required param could still be an activity we want to log but of specific types
    @Test
    void testLogEmptyRequest() throws Exception {
        RestActivityLogEntry mockLog = RestActivityLogEntry.builder()
                .type(ActivityType.ATE.getValue())
                .quantity(0L)
                .unit("reps")
                .build();

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

    @Test
    void getLogs_withStartAndEnd_returns200() throws Exception {
        String type = ActivityType.PUSHUPS.getValue();
        Instant end = Instant.now();
        Instant start = end.minus(Duration.ofDays(5));

        when(service.getActivityLogsForType(type, start, end))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/log/{type}", type)
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value(type));
    }

    @Test
    void getLogs_withoutStart_usesDefaultStart_returns200() throws Exception {
        String type = ActivityType.PUSHUPS.getValue();
        Instant end = Instant.now();

        when(service.getActivityLogsForType(eq(type), any(), eq(end)))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/log/{type}", type)
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(type));
    }

    @Test
    void getLogs_withoutEnd_usesNowAsEnd_returns200() throws Exception {
        String type = ActivityType.PUSHUPS.getValue();
        Instant start = Instant.now().minus(Duration.ofDays(7));

        when(service.getActivityLogsForType(eq(type), eq(start), any()))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/log/{type}", type)
                        .param("start", start.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(type));
    }

    @Test
    void getLogs_withNoResults_returnsEmptyList() throws Exception {
        String type = ActivityType.PUSHUPS.getValue();
        Instant end = Instant.now().minus(Duration.ofDays(29));
        Instant start = end.minus(Duration.ofDays(1));

        when(service.getActivityLogsForType(type, start, end))
                .thenReturn(Optional.of(List.of()));

        mockMvc.perform(get("/log/{type}", type)
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private Optional<List<RestActivityLogEntry>> sampleResponse() {
        return Optional.of(List.of(RestActivityLogEntry.builder()
                .type(ActivityType.PUSHUPS.getValue())
                .quantity(50)
                .build()));
    }
}