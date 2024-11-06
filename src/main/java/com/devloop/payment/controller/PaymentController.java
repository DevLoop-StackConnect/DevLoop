package com.devloop.payment.controller;

import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import com.devloop.payment.service.PaymentService;
import com.devloop.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final PurchaseService purchaseService;
    private final PaymentService paymentService;

    @Value("${toss.payment.secret.key}")
    private String widgetSecretKey;

    // 결제 요청
    @GetMapping("/payments-request")
    public String paymentsRequest(
            @RequestParam("orderId") Long orderId,
            Model model
    ) {
        Order order = orderService.findByOrderId(orderId);

        // 모델에 필요한 속성 추가
        model.addAttribute("orderName", order.getOrderName());
        model.addAttribute("customerName", order.getUser().getUsername());
        model.addAttribute("customerEmail", order.getUser().getEmail());
        model.addAttribute("orderRequestId", order.getOrderRequestId());
        model.addAttribute("totalPrice", order.getTotalPrice());
        model.addAttribute("customerKey", "customerKey-" + order.getUser().getId());

        return "payment-request";   // payment-request.html 템플릿 렌더링
    }

    // 주문 성공 결제 승인 요청 (주문 요청됨)
    @GetMapping("/payments-success")
    public String paymentsSuccess(
            @RequestParam("orderRequestId") String orderRequestId
    ) {
        orderService.orderRequested(orderRequestId);
        return "payment-success";
    }

    // 결제 실패
    @GetMapping("/payments-fail")
    public String paymentsFail(
            @RequestParam("message") String message,
            @RequestParam("code") Integer code,
            Model model
    ) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "payment-fail";
    }

    // 결제 승인 요청
    @RequestMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {

        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        // 주문 객체 상태 변경 , 구매내역, 줌문 내역 객체 생성
        if (isSuccess) {
            // 구매 내역 생성
            purchaseService.createPurchase(jsonObject.get("orderId").toString());
            // 주문 상태 완료로 변경
            orderService.orderApproved(jsonObject.get("orderId").toString());
            // 결제 내역 생성
            paymentService.createPayment(jsonObject);
        }

        return ResponseEntity.status(code).body(jsonObject);
    }

}
