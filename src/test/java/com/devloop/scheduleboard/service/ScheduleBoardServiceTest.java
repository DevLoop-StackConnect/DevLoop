package com.devloop.scheduleboard.service;

import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.scheduleboard.repository.ScheduleBoardRepository;
import com.devloop.scheduleboard.response.ScheduleBoardResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleBoardServiceTest {

    @Mock
    private ScheduleBoardRepository scheduleBoardRepository;
    @Mock
    private ProjectWithTutorService projectWithTutorService;

    @InjectMocks
    private ScheduleBoardService scheduleBoardService;

    private ProjectWithTutor projectWithTutor;
    private ScheduleBoard scheduleBoard;
    private User user;
    private User tutor;

    @BeforeEach
    void setUp() {

        // 유저 및 튜터 객체 생성
        user = User.of("일반유저", "user@example.com", "password123", UserRole.ROLE_USER);
        tutor = User.of("튜터유저", "tutor@example.com", "password123", UserRole.ROLE_TUTOR);

        //ScheduleBoard from사용해서 객체 만들때 사용
        projectWithTutor = ProjectWithTutor.of(
                "Test pwt",
                "내용",
                BigDecimal.valueOf(1000),
                LocalDateTime.now().plusDays(10),
                5,
                Level.EASY,
                Category.APP_DEV,
                tutor//게시글 작성자는 어차피 튜터니까
        );
        // ScheduleBoard 객체 생성
        scheduleBoard = ScheduleBoard.from(projectWithTutor);
    }

    @Test
    void createScheduleBoard_스케줄_보드_생성_성공() {
        // given
        when(scheduleBoardRepository.save(any(ScheduleBoard.class))).thenAnswer(invocation -> {
            ScheduleBoard savedBoard = invocation.getArgument(0);
            // 리플렉션을 사용해 ID 설정
            Field idField = ScheduleBoard.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedBoard, 1L); // 저장 후 ID가 설정된 것으로 가정
            return savedBoard;
        });

        // when
        ScheduleBoard createdScheduleBoard = scheduleBoardService.createScheduleBoard(projectWithTutor);

        // then
        assertNotNull(createdScheduleBoard);
        assertEquals(projectWithTutor, createdScheduleBoard.getProjectWithTutor());
        assertEquals(1L, createdScheduleBoard.getId());
        verify(scheduleBoardRepository, times(1)).save(createdScheduleBoard); // ScheduleBoard가 저장되었는지 검증
    }

    @Test
    void getScheduleBoard_스케줄_보드_조회_성공() {
        // given
        when(projectWithTutorService.findByPwtId(projectWithTutor.getId())).thenReturn(projectWithTutor);
        when(scheduleBoardRepository.findByProjectWithTutor(projectWithTutor)).thenReturn(Optional.of(scheduleBoard));

        // when
        ScheduleBoardResponse response = scheduleBoardService.getScheduleBoard(projectWithTutor.getId());

        // then
        assertEquals(scheduleBoard.getId(), response.getId());
        assertEquals(projectWithTutor.getId(), response.getPwtId());
        assertEquals(tutor.getUsername(), response.getManagerTutorName());
    }

    @Test
    void getScheduleBoard_스케줄_보드_조회_예외() {
        // given
        when(projectWithTutorService.findByPwtId(projectWithTutor.getId())).thenReturn(projectWithTutor);
        when(scheduleBoardRepository.findByProjectWithTutor(projectWithTutor)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ApiException.class, () -> scheduleBoardService.getScheduleBoard(projectWithTutor.getId()));
    }
}
