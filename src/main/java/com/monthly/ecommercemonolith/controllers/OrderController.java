package com.monthly.ecommercemonolith.controllers;

import com.monthly.ecommercemonolith.payload.OrderDTO;
import com.monthly.ecommercemonolith.payload.OrderRequestDTO;
import com.monthly.ecommercemonolith.services.OrderService;
import com.monthly.ecommercemonolith.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AuthUtils authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@RequestBody OrderRequestDTO orderRequestDTO, @PathVariable String paymentMethod) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod, orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
