package com.devloop.scheduletodo.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.scheduleboard.repository.BoardAssignmentRepository;
import com.devloop.scheduleboard.service.BoardAssignmentService;
import com.devloop.scheduleboard.service.ScheduleBoardService;
import com.devloop.scheduletodo.entity.ScheduleTodo;
import com.devloop.scheduletodo.repository.ScheduleTodoRepository;
import com.devloop.scheduletodo.request.ScheduleTodoRequest;
import com.devloop.scheduletodo.response.ScheduleTodoResponse;
import com.devloop.scheduletodo.response.ScheduleTodoSimpleResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ScheduleTodoServiceTest {
    @Mock
    private ScheduleTodoRepository scheduleTodoRepository;
    @Mock
    private UserService userService;
    @Mock
    private ScheduleBoardService scheduleBoardService;
    @Mock
    private BoardAssignmentService boardAssignmentService;
    @Mock
    private BoardAssignmentRepository boardAssignmentRepository;

    @InjectMocks
    public ScheduleTodoService scheduleTodoService;

    private ScheduleTodo scheduleTodo;
    private ScheduleBoard scheduleBoard;
    private ScheduleTodoRequest scheduleTodoRequest;
    private ProjectWithTutor projectWithTutor;
    private User tutor;
    private User user;
    private AuthUser authTutor;
    private AuthUser authUser;


    @BeforeEach
    public void setUp() throws Exception{

        // 유저 및 튜터 객체 생성
        user = User.of("일반유저", "user@example.com", "password123", UserRole.ROLE_USER);
        tutor = User.of("튜터유저", "tutor@example.com", "password123", UserRole.ROLE_TUTOR);

        // AuthUser 객체 생성
        authUser = new AuthUser(user.getId(), user.getEmail(), UserRole.ROLE_USER);
        authTutor = new AuthUser(tutor.getId(), tutor.getEmail(), UserRole.ROLE_TUTOR);


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
    void 구매자_일정_작성_성공() throws Exception {
        //given
        // 리플렉션으로 ScheduleTodoRequest 객체 생성
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        ScheduleTodoRequest scheduleTodoRequest = constructor.newInstance(
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );


        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(scheduleBoardService.findByScheduleBoardById(scheduleBoard.getId())).thenReturn(scheduleBoard);
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);
        Mockito.when(boardAssignmentService.getBoardAssignmentRepository()).thenReturn(boardAssignmentRepository);

        // pwt게시글 작성자(튜터)가 아닐때(=일반유저일때)
        if (!user.equals(scheduleBoard.getProjectWithTutor().getUser())) {
            Mockito.when(boardAssignmentService.getBoardAssignmentRepository()
                            .existsByScheduleBoardAndPurchase_User(Mockito.eq(scheduleBoard), Mockito.eq(user)))
                    .thenReturn(true);
        }
        Mockito.when(scheduleTodoRepository.save(any(ScheduleTodo.class))).thenReturn(scheduleTodo);

        //when
        ScheduleTodoResponse response = scheduleTodoService.createScheduleTodo(scheduleBoard.getId(), scheduleTodoRequest, authUser);

        //then
        Assertions.assertEquals(scheduleTodo.getId(), response.getId());
        Assertions.assertEquals(scheduleTodo.getTitle(), response.getTitle());
        Mockito.verify(scheduleTodoRepository, Mockito.times(1)).save(any(ScheduleTodo.class));
    }

    @Test
    void 미구매자_일정작성_실패() throws Exception {
        //given
        // 리플렉션으로 ScheduleTodoRequest 객체 생성
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        ScheduleTodoRequest scheduleTodoRequest = constructor.newInstance(
                "권한없는 유저",
                "실패",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(scheduleBoardService.findByScheduleBoardById(scheduleBoard.getId())).thenReturn(scheduleBoard);
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);
        Mockito.when(boardAssignmentService.getBoardAssignmentRepository()).thenReturn(boardAssignmentRepository);
        Mockito.when(boardAssignmentRepository.existsByScheduleBoardAndPurchase_User(
                Mockito.eq(scheduleBoard), Mockito.eq(user))
        ).thenReturn(false); // 권한이 없는 경우=false로 반환되게

        // when&then
        // 예외 발생 테스트하는 코드
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            scheduleTodoService.createScheduleTodo(scheduleBoard.getId(), scheduleTodoRequest, authUser);
        });
//
        assertEquals(ErrorStatus._PERMISSION_DENIED, exception.getErrorCode());

        // 일정 생성이 호출되지 않았는지 검증
        Mockito.verify(scheduleTodoRepository, Mockito.times(0)).save(any(ScheduleTodo.class));

    }

    @Test
    void 일정_다건조회_성공() {
        // given
        Long scheduleBoardId = 1L;

        // ScheduleTodo 객체들을 생성해서 스케줄 보드에 포함시킴
        ScheduleTodo todo1 = ScheduleTodo.of(scheduleBoard, user, "제목 1", "내용 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        ScheduleTodo todo2 = ScheduleTodo.of(scheduleBoard, user, "제목 2", "내용 2", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));

        Mockito.when(scheduleBoardService.findByScheduleBoardById(scheduleBoardId)).thenReturn(scheduleBoard);
        Mockito.when(scheduleTodoRepository.findByScheduleBoard(scheduleBoard)).thenReturn(Arrays.asList(todo1, todo2));

        // when
        List<ScheduleTodoSimpleResponse> response = scheduleTodoService.getTodoByScheduleBoard(scheduleBoardId);

        //then
        Assertions.assertEquals(2, response.size());  // 결과 개수 화긴
        Assertions.assertEquals("제목 1", response.get(0).getTitle());  // todo1 제목 확인
        Assertions.assertEquals("제목 2", response.get(1).getTitle());  // todo2 제목 화긴
        // 각각 todo1과 todo2의 startDate, endDate가 올바르게 매핑되었는지 확ㄱ인
        Assertions.assertEquals(todo1.getStartDate(), response.get(0).getStartDate());
        Assertions.assertEquals(todo1.getEndDate(), response.get(0).getEndDate());
        Assertions.assertEquals(todo2.getStartDate(), response.get(1).getStartDate());
        Assertions.assertEquals(todo2.getEndDate(), response.get(1).getEndDate());
    }

    @Test
    void 일정_단건조회_성공() {
        // given
        Long scheduleTodoId = 1L;

        // 테스트할 ScheduleTodo 생성
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // Mock 설정
        Mockito.when(scheduleTodoRepository.findById(scheduleTodoId)).thenReturn(Optional.of(scheduleTodo));

        // when
        ScheduleTodoResponse response = scheduleTodoService.getScheduleTodo(scheduleTodoId);

        // then
        Assertions.assertEquals(scheduleTodo.getId(), response.getId());
        Assertions.assertEquals(scheduleTodo.getCreatedBy().getUsername(), response.getCreatedBy());
        Assertions.assertEquals(scheduleTodo.getTitle(), response.getTitle());
        Assertions.assertEquals(scheduleTodo.getContent(), response.getContent());
        Assertions.assertEquals(scheduleTodo.getStartDate(), response.getStartDate());
        Assertions.assertEquals(scheduleTodo.getEndDate(), response.getEndDate());
    }

    @Test
    void 본인일정_수정_성공() throws Exception {
        // given
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // ScheduleTodoRequest 객체 생성 (리플렉션 사용)
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class);
        constructor.setAccessible(true);
        scheduleTodoRequest = constructor.newInstance(
                "제목수정",
                "내용수정",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);

        // when
        ScheduleTodoResponse response = scheduleTodoService.updateScheduleTodo(authUser, scheduleTodo.getId(), scheduleTodoRequest);

        // then
        Assertions.assertEquals("제목수정", response.getTitle());
        Assertions.assertEquals("내용수정", response.getContent());
    }

    @Test
    void 튜터_일반유저글_수정_성공() throws Exception {
        // given
        // ScheduleTodo 객체 생성: 일반 유저가 작성한 일정
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,  // 일반 유저가 작성자
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // ScheduleTodoRequest 객체 생성 (리플렉션 사용)
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        scheduleTodoRequest = constructor.newInstance(
                "튜터가 수정",
                "튜터가 수정",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // given
        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authTutor.getId())).thenReturn(tutor);

        // when
        ScheduleTodoResponse response = scheduleTodoService.updateScheduleTodo(authTutor, scheduleTodo.getId(), scheduleTodoRequest);

        // then
        Assertions.assertEquals("튜터가 수정", response.getTitle());
        Assertions.assertEquals("튜터가 수정", response.getContent());
    }

    @Test
    void 일반유저_튜터일정_수정_실패() throws Exception {
        // given
        // ScheduleTodo 객체 생성: 튜터가 작성한 일정
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                tutor,  // 튜터가 작성자
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // ScheduleTodoRequest 객체 생성 (리플렉션 사용)
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        scheduleTodoRequest = constructor.newInstance(
                "실패",
                "실패",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user); // 일반 유저가 요청하는 경우

        // when&then 예외 발생을 테스트하는 코드
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            scheduleTodoService.updateScheduleTodo(authUser, scheduleTodo.getId(), scheduleTodoRequest);
        });
        assertEquals(ErrorStatus._PERMISSION_DENIED, exception.getErrorCode());

        // 일정 수정이 호출되지 않았는지 검증
        Mockito.verify(scheduleTodoRepository, Mockito.times(0)).save(any(ScheduleTodo.class));
    }

    @Test
    void 일정_삭제_성공() {
        // given
        // ScheduleTodo 객체 생성: 일반 유저가 작성한 일정
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,  // 일반 유저가 작성자
                "제목",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );
        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);

        // when
        scheduleTodoService.deleteScheduleTodo(scheduleTodo.getId(), authUser);

        // then
        Mockito.verify(scheduleTodoRepository, Mockito.times(1)).delete(scheduleTodo);
    }


    @Test
    void 튜터_일반유저글_삭제_성공() {
        // given
        // 일반 유저가 작성한 스케줄Todo 생성
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,  // 일반 유저가 작성자
                "유저글",
                "내용",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );
        // Mock 설정
        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authTutor.getId())).thenReturn(tutor); // 요청한 유저가 튜터임을 설정

        // when
        scheduleTodoService.deleteScheduleTodo(scheduleTodo.getId(), authTutor);

        // then
        Mockito.verify(scheduleTodoRepository, Mockito.times(1)).delete(scheduleTodo); // 삭제가 호출되었는지 검증
    }


    @Test
    void 일반유저_튜터일정_삭제_실패() {
        // given
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                tutor,
                "튜터글",
                "삭제실패",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);

        // when & then
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            scheduleTodoService.deleteScheduleTodo(scheduleTodo.getId(), authUser);
        });
        assertEquals(ErrorStatus._PERMISSION_DENIED, exception.getErrorCode());

        // verify that delete was not called
        Mockito.verify(scheduleTodoRepository, Mockito.times(0)).delete(scheduleTodo);
    }


}
