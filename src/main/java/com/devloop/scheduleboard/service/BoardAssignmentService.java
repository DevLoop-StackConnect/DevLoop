package com.devloop.scheduleboard.service;

import com.devloop.purchase.entity.Purchase;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.scheduleboard.entity.BoardAssignment;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.scheduleboard.repository.BoardAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardAssignmentService {
    private final BoardAssignmentRepository boardAssignmentRepository;

    //BoardAssignment 생성 메서드
    @Transactional
    public void createBoardAssignment(List<Purchase> purchases) {
       for (Purchase purchase : purchases) {
           if (((HibernateProxy)purchase.getProduct()).getHibernateLazyInitializer().getImplementationClass()== ProjectWithTutor.class){

               // ProjectWithTutor로 실제 객체를 가져오기
               ProjectWithTutor projectWithTutor = (ProjectWithTutor) ((HibernateProxy) purchase.getProduct()).getHibernateLazyInitializer().getImplementation();
               ScheduleBoard scheduleBoard = projectWithTutor.getScheduleBoard();

               // ScheduleBoard가 있는 경우에만 BoardAssignment 생성
               if (scheduleBoard != null) {
                   BoardAssignment boardAssignment = BoardAssignment.of(scheduleBoard, purchase);
                   boardAssignmentRepository.save(boardAssignment);
               }
           }
       }
    }
}
//           purchase.getProduct().