package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    /**
     * 포인트 조회 API 테스트
     * 주어진 userId를 사용하여 Mock 데이터를 설정하고, mockMvc 를 통해 API 엔드포인트를 호출하여
     * 특정 유저의 포인트 정보를 올바르게 반환하는지 확인
     * given: 특정 조건 설정, when: 실제 동작을 수행, then: 기대하는 결과 검증
     */
    @Test
    @DisplayName("포인트 조회 API")
    void point() throws Exception {
        // given
        long userId = 1L;

        // Mock 데이터 설정
        UserPoint mockUser = new UserPoint(userId, 1000, 0);
        given(pointService.getUserPoint(userId)).willReturn(mockUser);

        // when & then
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())     // HTTP 응답 상태가 200인지 확인
                .andExpect(jsonPath("$.id").value(userId))                  // JSON 응답에서 id 필드가 주어진 userId와 일치하는지 확인
                .andExpect(jsonPath("$.point").value(1000))     // JSON 응답에서 point 필드가 1000인지 확인
                .andExpect(jsonPath("$.updateMillis").value(0)) // JSON 응답에서 updateMillis 필드가 0인지 확인
                .andDo(print());
    }

    /**
     * 포인트 충전/이용 내역 조회 API 테스트
     * 주어진 userId를 사용하여 Mock 데이터를 설정하고, mockMvc 를 통해 API 엔드포인트를 호출하여
     * 특정 유저의 포인트 내역을 올바르게 반환하는지 확인
     */
    @Test
    @DisplayName("포인트 충전/이용 내역 조회 API")
    void history() throws Exception {
        // given
        long userId = 1L;

        // Mock 데이터 설정
        PointHistory history1 = new PointHistory(1,userId, 500, TransactionType.CHARGE, 0);
        PointHistory history2 = new PointHistory(2, userId, 300, TransactionType.USE, 0);
        given(pointService.getUserPointHistory(userId)).willReturn(Arrays.asList(history1, history2));

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())     // HTTP 응답 상태가 200인지 확인
                .andExpect(jsonPath("$[0].id").value(1))            // 첫 번째 JSON 응답에서 id 필드가 1인지 확인
                .andExpect(jsonPath("$[0].userId").value(userId))               // 첫 번째 JSON 응답에서 userId 필드가 주어진 userId와 일치하는지 확인
                .andExpect(jsonPath("$[0].amount").value(500))      // 첫 번째 JSON 응답에서 amount 필드가 500인지 확인
                .andExpect(jsonPath("$[0].type").value("CHARGE"))   // 첫 번째 JSON 응답에서 type 필드가 "CHARGE"인지 확인
                .andExpect(jsonPath("$[0].updateMillis").value(0))  // 첫 번째 JSON 응답에서 updateMillis 필드가 0인지 확인
                .andExpect(jsonPath("$[1].id").value(2))            // 두 번째 JSON 응답에서 id 필드가 2인지 확인
                .andExpect(jsonPath("$[1].userId").value(userId))               // 두 번째 JSON 응답에서 userId 필드가 주어진 userId와 일치하는지 확인
                .andExpect(jsonPath("$[1].amount").value(300))      // 두 번째 JSON 응답에서 amount 필드가 300인지 확인
                .andExpect(jsonPath("$[1].type").value("USE"))      // 두 번째 JSON 응답에서 type 필드가 "USE"인지 확인
                .andExpect(jsonPath("$[1].updateMillis").value(0))  // 두 번째 JSON 응답에서 updateMillis 필드가 0인지 확인
                .andDo(print());
    }

    /**
     * 포인트 충전 API 테스트
     * 주어진 userId와 충전할 포인트를 사용하여 Mock 데이터를 설정하고, mockMvc 를 통해 API 엔드포인트를 호출하여
     * 특정 유저의 포인트가 정확히 충전되는지 확인
     */
    @Test
    @DisplayName("포인트 충전 API")
    void charge() throws Exception {
        // given
        long userId = 1L;
        long originalPoint = 1000;
        long amount = 400;
        UserPoint mockUser = new UserPoint(userId, originalPoint + amount, 0);
        given(pointService.chargePoint(userId, amount)).willReturn(mockUser);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount)))   // 요청 본문에 충전할 포인트를 JSON 형식으로 전달
                .andExpect(status().isOk())         // HTTP 응답 상태가 200인지 확인
                .andExpect(jsonPath("$.id").value(userId))                                  // JSON 응답에서 id 필드가 주어진 userId와 일치하는지 확인
                .andExpect(jsonPath("$.point").value(originalPoint + amount))   // JSON 응답에서 point 필드가 충전 후의 포인트와 일치하는지 확인
                .andExpect(jsonPath("$.updateMillis").value(0))                 // JSON 응답에서 updateMillis 필드가 0인지 확인
                .andDo(print());
    }

    /**
     * 포인트 사용 API 테스트
     * 주어진 userId와 사용할 포인트를 사용하여 Mock 데이터를 설정하고, mockMvc 를 통해 API 엔드포인트를 호출하여
     * 특정 유저의 포인트가 정확히 사용되는지 확인
     */
    @Test
    @DisplayName("포인트 사용 API")
    void use() throws Exception {
        // given
        long userId = 1L;
        long originalPoint = 1000;
        long amount = 400;
        UserPoint mockUser = new UserPoint(userId, originalPoint - amount, 0);
        given(pointService.usePoint(userId, amount)).willReturn(mockUser);

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount)))   // 요청 본문에 충전할 포인트를 JSON 형식으로 전달
                .andExpect(status().isOk())         // HTTP 응답 상태가 200인지 확인
                .andExpect(jsonPath("$.id").value(userId))                                  // JSON 응답에서 id 필드가 주어진 userId와 일치하는지 확인
                .andExpect(jsonPath("$.point").value(originalPoint - amount))   // JSON 응답에서 point 필드가 사용 후의 포인트와 일치하는지 확인
                .andExpect(jsonPath("$.updateMillis").value(0))                 // JSON 응답에서 updateMillis 필드가 0인지 확인
                .andDo(print());
    }
}
