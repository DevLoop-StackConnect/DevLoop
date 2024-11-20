package com.devloop.order.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.service.CartService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.order.entity.Order;
import com.devloop.order.enums.OrderStatus;
import com.devloop.order.repository.OrderRepository;
import com.devloop.product.entity.Product;
import com.devloop.purchase.repository.PurchaseRepository;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.stock.entity.Stock;
import com.devloop.stock.service.StockService;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PurchaseRepository purchaseRepository; // 순환 참조로 인해 레파지토리 직접 주입
    private final UserService userService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final StockService stockService;

    // 주문 하기 (주문 요청, 주문 객체 생성)
    // todo : 진입 시점에 Lock
    @Transactional
    public Order createOrder(AuthUser authUser) {

        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // 장바구니 객체 가져오기
        Cart cart = cartService.findByUserId(authUser.getId());

        //주문 전 재고 확인 & 재구매 확인
        for (CartItem cartItem : cart.getItems()) {
            Product product = (Product) ((HibernateProxy)cartItem.getProduct()).getHibernateLazyInitializer().getImplementation();
            boolean isRepurchase = purchaseRepository.existsByUserIdAndProductId(user.getId(), product.getId());
            // 재구매인 경우
            if (isRepurchase) {
                cartService.deleteProductItemFromCart(user, product.getId());
                throw new ApiException(ErrorStatus._ALREADY_PURCHASE);
            }
            if (product.getClass() == ProjectWithTutor.class) {
                // PWT 모집 중 인지 확인
                if (((ProjectWithTutor) product).getStatus().equals(ProjectWithTutorStatus.COMPLETED)) {
                    cartService.deleteProductItemFromCart(user, product.getId());
                    throw new ApiException(ErrorStatus._ALREADY_FULL);
                }
                // Stock 찾기
                Stock stock = stockService.findByProductId(product.getId());

                // 재고 확인
                if (stock.getQuantity() <= 0) {
                    cartService.deleteProductItemFromCart(user, product.getId());
                    throw new ApiException(ErrorStatus._STOCK_EMPTY);
                }
            }
        }

        // Order 객체 생성
        Order order = Order.of(
                cart.getTotalPrice(),
                UUID.randomUUID().toString(),
                String.format("%s 외 %d 개", cart.getItems().get(0).getProduct().getTitle(), cart.getItems().size()),
                user,
                cart
        );
        orderRepository.save(order);

        return order;
    }

    // 주문 요청됨
    @Transactional
    public void orderRequested(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = findByOrderRequestId(orderRequestId);

        // 주문 상태 "REQUESTED("주문 요청됨")"으로 변경
        order.updateStatus(OrderStatus.REQUESTED);
    }

    // 주문 실패됨(주문 취소)
    @Transactional
    public void orderFailed(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = findByOrderRequestId(orderRequestId);

        // 주문 삭제
        orderRepository.delete(order);
    }

    // 주문 완료됨 (주문 승인됨)
    @Transactional
    public void orderApproved(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = findByOrderRequestId(orderRequestId);

        // 주문 상태 "APPROVED("주문 승인됨")"으로 변경
        order.updateStatus(OrderStatus.APPROVED);

        List<CartItem> cartItems = order.getCart().getItems();
        for (CartItem cartItem : cartItems) {
            Product product = (Product) Hibernate.unproxy(cartItem.getProduct());
            if (product.getClass().getSimpleName().equals("ProjectWithTutor")){
                stockService.updateStock(product.getId());
            }
        }
        // 주문 항목 저장
        orderItemService.saveOrderItem(orderRequestId);
    }

    // Utile Method
    public Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_ORDER));
    }

    public Order findByOrderRequestId(String orderRequestId) {
        return orderRepository.findByOrderRequestId(orderRequestId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_ORDER));
    }
}
