package com.devloop.scheduleboard.service;

import com.devloop.product.entity.Product;
import com.devloop.purchase.entity.Purchase;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.scheduleboard.entity.BoardAssignment;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.scheduleboard.repository.BoardAssignmentRepository;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardAssignmentServiceTest {

    @Mock
    private BoardAssignmentRepository boardAssignmentRepository;

    @InjectMocks
    private BoardAssignmentService boardAssignmentService;

    private Purchase purchase;
    private ScheduleBoard scheduleBoard;
    private ProjectWithTutor projectWithTutor;
    private BoardAssignment boardAssignment;

    @BeforeEach
    void setUp() {
        // ScheduleBoard 및 ProjectWithTutor 생성
        scheduleBoard = Mockito.mock(ScheduleBoard.class);
        projectWithTutor = Mockito.mock(ProjectWithTutor.class);

        // ProjectWithTutor의 ScheduleBoard 설정
        when(projectWithTutor.getScheduleBoard()).thenReturn(scheduleBoard);

        // LazyInitializer Mock 설정
        LazyInitializer lazyInitializer = Mockito.mock(LazyInitializer.class);
        when(lazyInitializer.getImplementation()).thenReturn(projectWithTutor);

        // Purchase 및 Product 설정
        purchase = Mockito.mock(Purchase.class);
        Product productProxy = Mockito.mock(Product.class, withSettings().extraInterfaces(HibernateProxy.class));
        when(((HibernateProxy) productProxy).getHibernateLazyInitializer()).thenReturn(lazyInitializer);
        when(purchase.getProduct()).thenReturn(productProxy);
    }

    @Test
    void 생성_성공_테스트() {
        // given
        List<Purchase> purchases = List.of(purchase);

        // when
        boardAssignmentService.createBoardAssignment(purchases);

        // then
        verify(boardAssignmentRepository, times(1)).save(any(BoardAssignment.class));
    }

}
