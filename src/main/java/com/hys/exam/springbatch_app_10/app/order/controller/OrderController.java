package com.hys.exam.springbatch_app_10.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hys.exam.springbatch_app_10.app.member.entity.Member;
import com.hys.exam.springbatch_app_10.app.order.entity.Order;
import com.hys.exam.springbatch_app_10.app.order.exception.ActorCanNotSeeOrderException;
import com.hys.exam.springbatch_app_10.app.order.exception.OrderIdNotMatchedException;
import com.hys.exam.springbatch_app_10.app.order.service.OrderService;
import com.hys.exam.springbatch_app_10.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id, Model model) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);

        return "order/detail";
    }

    private final String SECRET_KEY = "payments 개발자센터, 내 개발 정보 , API 개별 연동 키, 시크릿 키";

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model) throws Exception {
        Order order = orderService.findForPrintById(id).get();
        long orderIdInputed = Integer.parseInt(orderId.split("__")[1]);
        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }
        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 이상 버전에서 지원
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(order.calculatePayPrice()));
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode successNode = responseEntity.getBody();
            model.addAttribute("orderId", successNode.get("orderId").asText());
            String secret = successNode.get("secret").asText(); // 가상계좌의 경우 입금 callback 검증을 위해서 secret을 저장하기를 권장함
            return "order/success";
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }
}
