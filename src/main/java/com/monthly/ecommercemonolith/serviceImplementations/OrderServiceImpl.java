package com.monthly.ecommercemonolith.serviceImplementations;

import com.monthly.ecommercemonolith.entities.*;
import com.monthly.ecommercemonolith.exceptions.APIException;
import com.monthly.ecommercemonolith.exceptions.ResourceNotFoundException;
import com.monthly.ecommercemonolith.payload.OrderDTO;
import com.monthly.ecommercemonolith.payload.OrderItemDTO;
import com.monthly.ecommercemonolith.repositories.*;
import com.monthly.ecommercemonolith.services.CartService;
import com.monthly.ecommercemonolith.services.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        //getting user cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) throw new ResourceNotFoundException("Email", emailId,
                "Cart");
        Address address =
                addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("AddressId", addressId, "Address"));
        //create a new order with payment info
        Order order = new Order();
        order.setEmail(emailId);
        order.setDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);
        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus,
                pgName, pgResponseMessage);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);
        //get items from cart into the order items
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) throw new APIException("Cart is Empty");
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);
        //update product stock
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            //clear the cart
            cartService.deleteProductFromCart(cart.getCartId(),
                    item.getProduct().getId());
        });
        //send back the order summary
        OrderDTO orderDTO=modelMapper.map(savedOrder,OrderDTO.class);
        orderItems.forEach(item->orderDTO.getOrderItems()
                .add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
