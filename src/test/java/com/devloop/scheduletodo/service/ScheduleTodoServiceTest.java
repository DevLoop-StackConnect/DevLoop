package com.devloop.scheduletodo.service;

import com.devloop.common.AuthUser;
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
    private User currentUser;
    private ScheduleTodoRequest scheduleTodoRequest;
    private ProjectWithTutor projectWithTutor;
    private User tutor;
    private User user;
    private AuthUser authTutor;
    private AuthUser authUser;



    @BeforeEach
    public void setUp() {
        //ProjectWithTutor 필드 초기화
        String title = "Test pwt";
        String description = "내용";
        BigDecimal price = BigDecimal.valueOf(1000);
        LocalDateTime deadline = LocalDateTime.now().plusDays(10);
        Integer maxParticipants = 5;
        Level level = Level.EASY;
        Category category = Category.APP_DEV;

        // 유저 및 튜터 객체 생성
        user = User.of("일반유저", "user@example.com", "password123", UserRole.ROLE_USER);
        tutor = User.of("튜터유저", "tutor@example.com", "password123", UserRole.ROLE_TUTOR);

        // AuthUser 객체 생성
        authUser = new AuthUser(user.getId(), user.getEmail(), UserRole.ROLE_USER);
        authTutor = new AuthUser(tutor.getId(), tutor.getEmail(), UserRole.ROLE_TUTOR);


        //ScheduleBoard from사용해서 객체 만들때 사용
        projectWithTutor = ProjectWithTutor.of(
                title,
                description,
                price,
                deadline,
                maxParticipants,
                level,
                category,
                tutor//게시글 작성자는 어차피 튜터니까
        );
        // ScheduleBoard 객체 생성
        scheduleBoard = ScheduleBoard.from(projectWithTutor);
    }


    @Test
    void 구매자_일정_작성_성공() throws Exception{
        //given
        // 테스트에 필요한 객체 초기화
        User currentUser = User.of("권한있는유저","test@naver.com","qwer123",UserRole.ROLE_USER);
        AuthUser authUser = new AuthUser(1L,"test@naver.com",UserRole.ROLE_USER);
        ScheduleBoard scheduleBoard = ScheduleBoard.from(projectWithTutor);//없어도되나

        // 리플렉션을 통해 ScheduleTodoRequest 객체 생성
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        ScheduleTodoRequest scheduleTodoRequest = constructor.newInstance(
                "권한있는 유저",
                "성공",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );


        ScheduleTodo scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                currentUser,
                scheduleTodoRequest.getTitle(),
                scheduleTodoRequest.getContent(),
                scheduleTodoRequest.getStartDate(),
                scheduleTodoRequest.getEndDate()
        );

        Mockito.when(scheduleBoardService.findByScheduleBoardById(scheduleBoard.getId())).thenReturn(scheduleBoard);
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(currentUser);
        Mockito.when(boardAssignmentService.getBoardAssignmentRepository()).thenReturn(boardAssignmentRepository);
        // currentUser와 projectWithTutor.getUser()가 다르다고 가정
        if (!currentUser.equals(scheduleBoard.getProjectWithTutor().getUser())) {
            Mockito.when(boardAssignmentService.getBoardAssignmentRepository()
                            .existsByScheduleBoardAndPurchase_User(Mockito.eq(scheduleBoard), Mockito.eq(currentUser)))
                    .thenReturn(true);
        }
        Mockito.when(scheduleTodoRepository.save(any(ScheduleTodo.class))).thenReturn(scheduleTodo);

        //when
        ScheduleTodoResponse response = scheduleTodoService.createScheduleTodo(scheduleBoard.getId(),scheduleTodoRequest,authUser);

        //then
        Assertions.assertEquals(scheduleTodo.getId(),response.getId());
        Assertions.assertEquals(scheduleTodo.getTitle(),response.getTitle());
        Mockito.verify(scheduleTodoRepository,Mockito.times(1)).save(any(ScheduleTodo.class));
    }

    @Test
    void 미구매자_일정작성_실패() throws Exception{
        //given
        User currentUser = User.of("권한없는유저","test@naver.com","qwer123",UserRole.ROLE_USER);
        AuthUser authUser = new AuthUser(1L,"test@naver.com",UserRole.ROLE_USER);
        ScheduleBoard scheduleBoard = ScheduleBoard.from(projectWithTutor);

        // 리플렉션을 통해 ScheduleTodoRequest 객체 생성
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
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(currentUser);
        Mockito.when(boardAssignmentService.getBoardAssignmentRepository()).thenReturn(boardAssignmentRepository);
        // 권한이 없는 경우 (false 반환)
        Mockito.when(boardAssignmentRepository.existsByScheduleBoardAndPurchase_User(
                Mockito.eq(scheduleBoard), Mockito.eq(currentUser))
        ).thenReturn(false);

        // when&then
        // 예외 발생을 테스트하는 코드
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            scheduleTodoService.createScheduleTodo(scheduleBoard.getId(), scheduleTodoRequest, authUser);
        });
        System.out.println("예외발생 : " + exception.getMessage());

        // 일정 생성이 호출되지 않았는지 검증
        Mockito.verify(scheduleTodoRepository, Mockito.times(0)).save(any(ScheduleTodo.class));

    }

    @Test
    void 일정_다건조회_성공(){
        // given
        Long scheduleBoardId = 1L;
        ScheduleBoard scheduleBoard = ScheduleBoard.from(projectWithTutor);

        // ScheduleTodo 객체들을 생성하여 스케줄 보드에 포함시킴
        ScheduleTodo todo1 = ScheduleTodo.of(scheduleBoard, currentUser, "Title 1", "Content 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        ScheduleTodo todo2 = ScheduleTodo.of(scheduleBoard, currentUser, "Title 2", "Content 2", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));

        Mockito.when(scheduleBoardService.findByScheduleBoardById(scheduleBoardId)).thenReturn(scheduleBoard);
        Mockito.when(scheduleTodoRepository.findByScheduleBoard(scheduleBoard)).thenReturn(Arrays.asList(todo1, todo2));

        // when
        List<ScheduleTodoSimpleResponse> response = scheduleTodoService.getTodoByScheduleBoard(scheduleBoardId);

        //then
        Assertions.assertEquals(2, response.size());  // 결과 개수 화긴
        Assertions.assertEquals("Title 1", response.get(0).getTitle());  // todo1 제목 화긴
        Assertions.assertEquals("Title 2", response.get(1).getTitle());  // todo2 제목 화긴
        // 각각 todo1과 todo2의 startDate, endDate가 올바르게 매핑되었는지 화긴
        Assertions.assertEquals(todo1.getStartDate(), response.get(0).getStartDate());
        Assertions.assertEquals(todo1.getEndDate(), response.get(0).getEndDate());
        Assertions.assertEquals(todo2.getStartDate(), response.get(1).getStartDate());
        Assertions.assertEquals(todo2.getEndDate(), response.get(1).getEndDate());
    }

    @Test
    void 일정_단건조회_성공(){
        // given
        Long scheduleTodoId = 1L;
        ScheduleBoard scheduleBoard = ScheduleBoard.from(projectWithTutor);
        User createdByUser = User.of("작성자", "creator@example.com", "password123", UserRole.ROLE_USER);

        // 테스트할 ScheduleTodo 생성
        ScheduleTodo scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                createdByUser,
                "Test Title",
                "Test Content",
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
    void 본인일정_수정_성공() throws Exception{
        // given
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,
                "Original Title",
                "Original Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // ScheduleTodoRequest 객체 생성 (리플렉션 사용)
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class);
        constructor.setAccessible(true);
        scheduleTodoRequest = constructor.newInstance(
                "Updated Title",
                "Updated Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);

        // when
        ScheduleTodoResponse response = scheduleTodoService.updateScheduleTodo(authUser, scheduleTodo.getId(), scheduleTodoRequest);

        // then
        Assertions.assertEquals("Updated Title", response.getTitle());
        Assertions.assertEquals("Updated Content", response.getContent());
    }

    @Test
    void 튜터_일반유저글_수정_성공() throws Exception{
        // given
        // ScheduleTodo 객체 생성: 일반 유저가 작성한 일정
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                user,  // 일반 유저가 작성자
                "Original Title",
                "Original Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // ScheduleTodoRequest 객체 생성 (리플렉션 사용)
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        scheduleTodoRequest = constructor.newInstance(
                "Tutor Updated Title",
                "Tutor Updated Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // given
        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authTutor.getId())).thenReturn(tutor);

        // when
        ScheduleTodoResponse response = scheduleTodoService.updateScheduleTodo(authTutor, scheduleTodo.getId(), scheduleTodoRequest);

        // then
        Assertions.assertEquals("Tutor Updated Title", response.getTitle());
        Assertions.assertEquals("Tutor Updated Content", response.getContent());
    }

    @Test
    void 일반유저_다른사람일정_수정_실패() throws Exception{
        // given
        // ScheduleTodo 객체 생성: 튜터가 작성한 일정
        scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                tutor,  // 튜터가 작성자
                "Original Title",
                "Original Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // ScheduleTodoRequest 객체 생성 (리플렉션 사용)
        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
                String.class, String.class, LocalDateTime.class, LocalDateTime.class
        );
        constructor.setAccessible(true);
        scheduleTodoRequest = constructor.newInstance(
                "Unauthorized Update Title",
                "Unauthorized Update Content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        // given
        Mockito.when(scheduleTodoRepository.findById(scheduleTodo.getId())).thenReturn(Optional.of(scheduleTodo));
        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user); // 일반 유저가 요청하는 경우

        // 예외 발생을 테스트하는 코드
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            scheduleTodoService.updateScheduleTodo(authUser, scheduleTodo.getId(), scheduleTodoRequest);
        });
        System.out.println("예외 발생: " + exception.getMessage());

        // 일정 수정이 호출되지 않았는지 검증
        Mockito.verify(scheduleTodoRepository, Mockito.times(0)).save(any(ScheduleTodo.class));
    }
//
//    @Test
//    void 일정_삭제_성공(){ }
//
//
//    @Test
//    void 튜터_일반유저글_삭제_성공(){ }
//
//
//    @Test
//    void 일반유저_다른사람일정_삭제_실패(){ }


}
