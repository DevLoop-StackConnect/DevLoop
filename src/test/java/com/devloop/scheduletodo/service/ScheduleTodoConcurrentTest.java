//package com.devloop.scheduletodo.service;
//
//import com.devloop.common.AuthUser;
//import com.devloop.common.enums.Category;
//import com.devloop.pwt.entity.ProjectWithTutor;
//import com.devloop.pwt.enums.Level;
//import com.devloop.pwt.repository.ProjectWithTutorRepository;
//import com.devloop.scheduleboard.entity.ScheduleBoard;
//import com.devloop.scheduleboard.repository.ScheduleBoardRepository;
//import com.devloop.scheduletodo.entity.ScheduleTodo;
//import com.devloop.scheduletodo.repository.ScheduleTodoRepository;
//import com.devloop.scheduletodo.request.ScheduleTodoRequest;
//import com.devloop.user.entity.User;
//import com.devloop.user.enums.UserRole;
//import com.devloop.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//
//import java.lang.reflect.Constructor;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//class ScheduleTodoConcurrentTest {
//    @Autowired
//    private ScheduleTodoService scheduleTodoService;
//
//    @Autowired
//    private ScheduleTodoRepository scheduleTodoRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ScheduleBoardRepository scheduleBoardRepository;
//
//    @Autowired
//    private ProjectWithTutorRepository projectWithTutorRepository;
//
//
//    private Long scheduleTodoId;
//    private ScheduleTodo scheduleTodo;
//    private User tutor;
//    private User user;
//
//    @BeforeEach
//    public void setup() {
//        user = userRepository.save(User.of("유저", "a@mail.com", "Qwer1234!", UserRole.ROLE_USER));
//        tutor = userRepository.save(User.of("튜터", "a@mail.com", "Qwer1234!", UserRole.ROLE_TUTOR));
//
//        ProjectWithTutor projectWithTutor = projectWithTutorRepository.save(ProjectWithTutor.of(
//                "PWT제목",
//                "내용",
//                BigDecimal.valueOf(20000),
//                LocalDateTime.now().plusDays(10),
//                5,
//                Level.EASY,
//                Category.APP_DEV,
//                tutor//어차피 게시글 작성자는 튜터
//        ));
//        ScheduleBoard scheduleBoard = scheduleBoardRepository.save(ScheduleBoard.from(projectWithTutor));
//
//
//        scheduleTodo = ScheduleTodo.of(
//                scheduleBoard,
//                user,
//                "원래제목",
//                "원래내용",
//                LocalDateTime.now(),
//                LocalDateTime.now().plusDays(10)
//        );
//        scheduleTodo = scheduleTodoRepository.save(scheduleTodo);
//        scheduleTodoId = scheduleTodo.getId();
//    }
//
//    @Test
//    void 낙관적락_적용후_동시성문제발생() throws Exception {
//        //스레드 수 : 일반 유저와 튜터로 총 두개
//        int threadCount = 2;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        //수정이 제대로 된 스레드 수를 추적
//        AtomicInteger optimisticLockExceptionCount = new AtomicInteger(0);
//
//        // 리플렉션을 사용하여 ScheduleTodoRequest 인스턴스 생성
//        Constructor<ScheduleTodoRequest> constructor = ScheduleTodoRequest.class.getDeclaredConstructor(
//                String.class, String.class, LocalDateTime.class, LocalDateTime.class
//        );
//        constructor.setAccessible(true);
//
//        //일반 유저 스레드
//        executorService.submit(() -> {
//            try {
//                ScheduleTodoRequest request = constructor.newInstance(
//                        "일반 유저가 수정한 제목", "일반 유저가 수정한 내용", LocalDateTime.now(), LocalDateTime.now().plusDays(5)
//                );
//                AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), UserRole.ROLE_USER);
//                scheduleTodoService.updateScheduleTodo(authUser, scheduleTodoId, request);
//            } catch (ObjectOptimisticLockingFailureException e) {
//                optimisticLockExceptionCount.incrementAndGet();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                latch.countDown();
//            }
//        });
//
//        //튜터 스레드
//        executorService.submit(() -> {
//            try {
//                ScheduleTodoRequest request = constructor.newInstance(
//                        "튜터 수정 제목", "튜터 수정 내용", LocalDateTime.now(), LocalDateTime.now().plusDays(5)
//                );
//                AuthUser authUser = new AuthUser(tutor.getId(), tutor.getEmail(), UserRole.ROLE_TUTOR);
//                scheduleTodoService.updateScheduleTodo(authUser, scheduleTodoId, request);
//            } catch (ObjectOptimisticLockingFailureException e) {
//                optimisticLockExceptionCount.incrementAndGet();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                latch.countDown();
//            }
//        });
//
//        latch.await();
//        executorService.shutdown();
//
//        System.out.println("발생한 낙관적 락 예외 수: " + optimisticLockExceptionCount.get());
//        assertTrue(optimisticLockExceptionCount.get() > 0, "낙관적 락 충돌이 발생해야 합니다.");
//
//    }
//}