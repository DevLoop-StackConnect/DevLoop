package com.devloop.stock.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.product.entity.Product;
import com.devloop.product.service.ProductService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.stock.entity.Stock;
import com.devloop.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final ProductService productService;
    private final ProjectWithTutorService projectWithTutorService;

    @Transactional
    public void createStock(Long productId, Integer quantity) {

        // 상품 객체 찾기
        Product product = productService.findByProductId(productId);

        // Stock 객체 생성
        Stock stock = Stock.of(product, quantity);

        // Stock 객체 저장
        stockRepository.save(stock);
    }

    @Transactional
    public void updateStock(Long productId) {

        // PWT 찾기
        ProjectWithTutor pwt = projectWithTutorService.findByPwtId(productId);

        // PWT 모집 중 인지 확인
        if (pwt.getStatus().equals(ProjectWithTutorStatus.COMPLETED)) {
            throw new ApiException(ErrorStatus._ALREADY_FULL);
        }

        // Stock 찾기
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_STOCK));
        stock.updateQuantity(stock.getQuantity());

        // Stock 0 일 때 PWT 상태 변경
        if (stock.getQuantity() == 0) {
            pwt.changeStatus(ProjectWithTutorStatus.COMPLETED);
        }
    }
}
