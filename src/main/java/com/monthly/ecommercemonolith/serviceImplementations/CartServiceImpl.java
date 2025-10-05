package com.monthly.ecommercemonolith.serviceImplementations;

import com.monthly.ecommercemonolith.entities.Cart;
import com.monthly.ecommercemonolith.entities.CartItem;
import com.monthly.ecommercemonolith.entities.Product;
import com.monthly.ecommercemonolith.exceptions.APIException;
import com.monthly.ecommercemonolith.exceptions.ResourceNotFoundException;
import com.monthly.ecommercemonolith.payload.CartDTO;
import com.monthly.ecommercemonolith.payload.ProductDTO;
import com.monthly.ecommercemonolith.repositories.CartItemRepository;
import com.monthly.ecommercemonolith.repositories.CartRepository;
import com.monthly.ecommercemonolith.repositories.ProductRepository;
import com.monthly.ecommercemonolith.services.CartService;
import com.monthly.ecommercemonolith.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service

public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AuthUtils authUtil;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository,
                           AuthUtils authUtil,
                           CartItemRepository cartItemRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.authUtil = authUtil;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Id",
                        productId, "Product"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) throw new APIException("No cart exists");
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).toList();

            cartDTO.setProducts(products);

            return cartDTO;

        }).toList();
        return cartDTOs;
    }

    @Override
    public CartDTO getUserCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) throw new ResourceNotFoundException("Cart Id", cartId,
                "Cart");
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products =
                cart.getCartItems().stream().map(p -> modelMapper.map(p,
                        ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        Cart cart =
                cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart Id", cartId, "Cart"));
        Product product =
                productRepository.findById(productId).orElseThrow(() ->
                        new ResourceNotFoundException("Product Id", productId,
                                "Product"));
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not " +
                    "available");
        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Please make an order of " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity());
        }
        CartItem cartItem =
                cartItemRepository.findCartItemByProductIdAndCartId(cartId,
                        productId);
        if (cartItem == null)
            throw new APIException("Product:" + product.getProductName() + "not available in the cart");

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new APIException("The quantity cannot be negative");
        }
        if (newQuantity == 0) deleteProductFromCart(cartId, productId);
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);
        if (updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(),
                    ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart =
                cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart Id", cartId, "Cart"));
        CartItem cartItem =
                cartItemRepository.findCartItemByProductIdAndCartId(cartId
                        , productId);
        if (cartItem == null) throw new ResourceNotFoundException("Product",
                productId, "Product Id");
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        return "Cart item:" + cartItem.getProduct().getProductName() + " removed from the cart!!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart =
                cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart Id", cartId, "Cart"));
        Product product =
                productRepository.findById(productId).orElseThrow(() ->
                        new ResourceNotFoundException("Product Id", productId,
                                "Product"));
        CartItem cartItem =
                cartItemRepository.findCartItemByProductIdAndCartId(cartId,
                        productId);

        if (cartItem == null)
            throw new APIException("Product " + product.getProductName() + " not available in the cart");

        double cartPrice =
                cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItem = cartItemRepository.save(cartItem);

    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) return userCart;
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }
}
