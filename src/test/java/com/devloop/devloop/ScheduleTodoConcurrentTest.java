//package com.devloop.devloop;
//
//import com.devloop.common.AuthUser;
//import com.devloop.common.enums.Category;
//import com.devloop.pwt.entity.ProjectWithTutor;
//import com.devloop.pwt.enums.Level;
//import com.devloop.scheduleBoard.entity.ScheduleBoard;
//import com.devloop.scheduleTodo.dto.request.ScheduleTodoRequest;
//import com.devloop.scheduleTodo.entity.ScheduleTodo;
//import com.devloop.scheduleTodo.repository.ScheduleTodoRepository;
//import com.devloop.scheduleTodo.service.ScheduleTodoService;
//import com.devloop.user.entity.User;
//import com.devloop.user.enums.UserRole;
//import com.devloop.user.repository.UserRepository;
//import jakarta.persistence.OptimisticLockException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.OptimisticLockingFailureException;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.concurrent.*;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ScheduleTodoConcurrentTest {
//    @Mock
//    private ScheduleTodoRepository scheduleTodoRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private ScheduleTodoService scheduleTodoService;
//
//
//    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
//
//
//    @Test
//    public void 낙관적락_동시성_예외_테스트() throws InterruptedException {
//        // Given
//        Long todoId = 1L;
//        User tutor = User.of("튜터", "Qwer1234", "tutor@mail.com", UserRole.ROLE_TUTOR);
//        User user = User.of("유저", "Asdf1234", "user@mail.com", UserRole.ROLE_USER);
//
//        // ProjectWithTutor와 ScheduleBoard Mock 객체 생성
//        ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
//                "테스트 프로젝트",
//                "프로젝트 설명",
//                50000,
//                LocalDateTime.now().plusDays(7),
//                5,
//                Level.EASY,
//                Category.APP_DEV,
//                tutor
//        );
//
//        ScheduleBoard scheduleBoard = ScheduleBoard.of(projectWithTutor);
//        ScheduleTodo scheduleTodo = ScheduleTodo.of(scheduleBoard, tutor, "기존 제목", "기존 내용", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
//
//        // Mock 동작 설정
//        when(scheduleTodoRepository.findById(todoId)).thenReturn(Optional.of(scheduleTodo));
//        when(userRepository.findById(tutor.getId())).thenReturn(Optional.of(tutor));
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
//
//        // 첫 번째 호출에서는 정상 처리되도록 하고, 두 번째 호출에서는 낙관적 락 예외를 발생시키도록 설정
//        doThrow(new OptimisticLockingFailureException("낙관적 락 예외"))
//                .when(scheduleTodoRepository).save(any(ScheduleTodo.class));
//
//        ScheduleTodoRequest updateRequest = new ScheduleTodoRequest("새 제목", "새 내용", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
//
//        // When
//        Callable<Void> tutorTask = () -> {
//            AuthUser authTutor = new AuthUser(tutor.getId(), tutor.getEmail(), tutor.getUserRole());
//            scheduleTodoService.updateScheduleTodo(authTutor, todoId, updateRequest);
//            return null;
//        };
//
//        Callable<Void> userTask = () -> {
//            AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
//            scheduleTodoService.updateScheduleTodo(authUser, todoId, updateRequest);
//            return null;
//        };
//
//        Future<Void> tutorFuture = executorService.submit(tutorTask);
//        Future<Void> userFuture = executorService.submit(userTask);
//
//        // Then
//        try {
//            tutorFuture.get();
//            userFuture.get();
//        } catch (ExecutionException e) {
//            Throwable cause = e.getCause();
//            // 발생한 예외가 OptimisticLockException을 원인으로 갖는지 확인합니다.
//            assertTrue(cause instanceof OptimisticLockException || cause.getCause() instanceof OptimisticLockException);
//
//        } finally {
//            executorService.shutdown();
//        }
//
//    }
//}