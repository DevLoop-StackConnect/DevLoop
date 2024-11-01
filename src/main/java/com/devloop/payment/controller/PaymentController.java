package com.devloop.payment.controller;

import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

//@RequestMapping(value="/")
@Controller
@RequiredArgsConstructor
public class PaymentController {
    @Value("${toss.payment.secret.key}")
    private String widgetSecretKey;

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderService orderService;

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
        ;
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

//        // 주문 객체 상태 변경 , 구매내역, 줌문 내역 객체 생성
//        if(isSuccess){
//            orderService.orderRequested(orderId);
//        }

        return ResponseEntity.status(code).body(jsonObject);
    }

//    @GetMapping(value = "success")
//    public String paymentResult(
//            Model model,
//            @RequestParam(value = "orderId") String orderId,
//            @RequestParam(value = "amount") Integer amount,
//            @RequestParam(value = "paymentKey") String paymentKey) throws Exception {
//
//        String secretKey = "시크릿 키 전역변수로 설정되어 있는것 사용";
//
//        Base64.Encoder encoder = Base64.getEncoder();
//        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
//        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);
//
//        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);
//
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("Authorization", authorizations);
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//        JSONObject obj = new JSONObject();
//        obj.put("orderId", orderId);
//        obj.put("amount", amount);
//
//        OutputStream outputStream = connection.getOutputStream();
//        outputStream.write(obj.toString().getBytes("UTF-8"));
//
//        int code = connection.getResponseCode();
//        boolean isSuccess = code == 200 ? true : false;
//        model.addAttribute("isSuccess", isSuccess);
//
//        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
//
//        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
//        JSONParser parser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) parser.parse(reader);
//        responseStream.close();
//        model.addAttribute("responseStr", jsonObject.toJSONString());
//        System.out.println(jsonObject.toJSONString());
//
//        model.addAttribute("method", (String) jsonObject.get("method"));
//        model.addAttribute("orderName", (String) jsonObject.get("orderName"));
//
//        if (((String) jsonObject.get("method")) != null) {
//            if (((String) jsonObject.get("method")).equals("카드")) {
//                model.addAttribute("cardNumber", (String) ((JSONObject) jsonObject.get("card")).get("number"));
//            } else if (((String) jsonObject.get("method")).equals("가상계좌")) {
//                model.addAttribute("accountNumber", (String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
//            } else if (((String) jsonObject.get("method")).equals("계좌이체")) {
//                model.addAttribute("bank", (String) ((JSONObject) jsonObject.get("transfer")).get("bank"));
//            } else if (((String) jsonObject.get("method")).equals("휴대폰")) {
//                model.addAttribute("customerMobilePhone", (String) ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
//            }
//        } else {
//            model.addAttribute("code", (String) jsonObject.get("code"));
//            model.addAttribute("message", (String) jsonObject.get("message"));
//        }
//
//        return "success";
//    }
//
//    @GetMapping(value = "fail")
//    public String paymentResult(
//            Model model,
//            @RequestParam(value = "message") String message,
//            @RequestParam(value = "code") Integer code
//    ) throws Exception {
//
//        model.addAttribute("code", code);
//        model.addAttribute("message", message);
//
//        return "fail";
//    }

}
