//package com.devloop.scheduleboard.controller;
//
//import com.devloop.common.apipayload.status.ErrorStatus;
//import com.devloop.common.exception.ApiException;
//import com.devloop.common.utils.JwtUtil;
//import com.devloop.config.WebSecurityConfig;
//import com.devloop.scheduleboard.response.ScheduleBoardResponse;
//import com.devloop.scheduleboard.service.ScheduleBoardService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ScehduleBoardController.class)
//@AutoConfigureMockMvc
//@Import({WebSecurityConfig.class, JwtUtil.class})
//@TestPropertySource(properties = {
//        "jwt.secret.key=7Iqk7YyM66W07YOA7L2U65Sp7YG065+9U3ByaW5n6rCV7J2Y7Yqc7YSw7LWc7JuQ67mI7J6F64uI64ukLg==" // 테스트용 JWT 키
//})
//@MockBean(JpaMetamodelMappingContext.class)
//
//public class ScheduleBoardControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ScheduleBoardService scheduleBoardService; //컨트롤러는 서비스계층에 의존하기 때문에 정상 동작을 시뮬레이션 하려면 서비스도 mock해야한다고함
//
//
//    @Test
//    void getScheduleBoard_성공() throws Exception {
//        Long mockId = 1L;
//        Long mockPwtId = 10L;
//        String mockManagerTutorName = "test";
//        ScheduleBoardResponse mockResponse = ScheduleBoardResponse.of(mockId, mockPwtId, mockManagerTutorName);
//
//        Mockito.when(scheduleBoardService.getScheduleBoard(mockPwtId)).thenReturn(mockResponse);
//
//        mockMvc.perform(get("/api/search/v2/scheduleBoards/{pwtId}", mockPwtId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()) // HTTP 200 기대
//                .andExpect(jsonPath("$.data.id").value(mockId)) // JSON 경로를 사용한 검증
//                .andExpect(jsonPath("$.data.pwtId").value(mockPwtId))
//                .andExpect(jsonPath("$.data.managerTutorName").value(mockManagerTutorName));
//
//    }
//
//    @Test
//    void getScheduleBoard_존재하지_않는_ScheduleBoard() throws Exception {
//        // Mock 데이터 준비
//        Long invalidPwtId = 99L;
//
//        // ScheduleBoardService가 예외를 던지도록 Mocking
//        Mockito.when(scheduleBoardService.getScheduleBoard(invalidPwtId))
//                .thenThrow(new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_BOARD));
//
//        // MockMvc를 사용해 컨트롤러 호출 및 검증
//        mockMvc.perform(get("/api/search/v2/scheduleBoards/{pwtId}", invalidPwtId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//}
//
