package com.devloop.stock.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.product.entity.Product;
import com.devloop.product.service.ProductService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.stock.entity.Stock;
import com.devloop.stock.repository.StockRepository;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProjectWithTutorService projectWithTutorService;

    @InjectMocks
    private StockService stockService;

    private Product product;
    private Stock stock;
    private ProjectWithTutor projectWithTutor;
    private User user;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", BigDecimal.TEN);
        stock = Stock.of(product, 10);
        user = User.of("홍길동", "test@test.com", "hong1234!", UserRole.ROLE_TUTOR);
        projectWithTutor = ProjectWithTutor.of(
                "Test Product",
                "Test Product Description",
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(10),
                10,
                Level.EASY,
                Category.ETC,
                user);
        projectWithTutor.changeStatus(ProjectWithTutorStatus.IN_PROGRESS);
    }

    @Test
    void 재고_생성_성공(){
        // given
        Long productId = 1L;
        Integer quantity = 10;
        when(productService.findByProductId(productId)).thenReturn(product);

        // when
        stockService.createStock(productId, quantity);

        // then
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

//    @Test
//    void 재고_모집_완료_상태_성공(){
//        // given
//        Long productId = 1L;
//        stock = Stock.of(product, 1);    // stock 수량 1로 설정해 -1로 변경돼 모집 완료 조건 충족
//        when(projectWithTutorService.findByPwtId(productId)).thenReturn(projectWithTutor);
//        when(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stock));
//
//        // when
//        stockService.updateStock(productId);
//
//        // then
//        assertEquals(ProjectWithTutorStatus.COMPLETED, projectWithTutor.getStatus());
//    }

    @Test
    void updateStock_PWT_모집_완료_상태여서_실패() {
        // given
        Long productId = 1L;
        projectWithTutor.changeStatus(ProjectWithTutorStatus.COMPLETED);
        when(projectWithTutorService.findByPwtId(productId)).thenReturn(projectWithTutor);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> stockService.updateStock(productId));
        assertEquals(ErrorStatus._ALREADY_FULL, exception.getErrorCode());
    }

//    @Test
//    void updateStock_재고_없어_실패() {
//        // given
//        Long productId = 1L;
//        when(projectWithTutorService.findByPwtId(productId)).thenReturn(projectWithTutor);
//        when(stockRepository.findByProductId(productId)).thenReturn(Optional.empty());
//
//        // when & then
//        ApiException exception = assertThrows(ApiException.class, () -> stockService.updateStock(productId));
//        assertEquals(ErrorStatus._NOT_FOUND_STOCK, exception.getErrorCode());
//    }

    @Test
    void findByProductId_성공() {
        // given
        Long productId = 1L;
        when(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stock));

        // when
        Stock result = stockService.findByProductId(productId);

        // then
        assertNotNull(result);
        assertEquals(stock, result);
    }

    @Test
    void findByProductId_실패() {
        // given
        Long productId = 1L;
        when(stockRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> stockService.findByProductId(productId));
        assertEquals(ErrorStatus._NOT_FOUND_STOCK, exception.getErrorCode());
    }

}