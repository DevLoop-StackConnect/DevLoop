package com.devloop.payment.controller;

import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//@RequestMapping(value="/")
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

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
        model.addAttribute("customerKey", "customerKey-"+order.getUser().getId());

        return "payment-request";   // payment-request.html 템플릿 렌더링
    }


    @GetMapping("/payments-success")
    public String paymentsSuccess(

    ){
        return "payment-success";
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
