package com.devloop.stock.service;

import com.devloop.product.entity.Product;
import com.devloop.product.service.ProductService;
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

    public void createStock(Long productId, Integer quantity) {

        // 상품 객체 찾기
        Product product = productService.findByProductId(productId);

        // Stock 객체 생성
        Stock stock = Stock.of(product, quantity);

        // Stock 객체 저장
        stockRepository.save(stock);
    }

}
