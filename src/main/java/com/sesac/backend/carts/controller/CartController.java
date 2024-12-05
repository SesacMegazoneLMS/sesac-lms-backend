package com.sesac.backend.carts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sesac.backend.carts.domain.Cart;
import com.sesac.backend.carts.dto.request.CartRequest;
import com.sesac.backend.carts.dto.response.CartResponse;
import com.sesac.backend.carts.exception.CartNotFoundException;
import com.sesac.backend.carts.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequestMapping("/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private final UUID USER_ID = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

    //장바구니 추가
    @PostMapping("/items")
    public ResponseEntity<String> addItem(@RequestBody CartRequest cartRequest) {
        try{
            cartService.addCourseToCart(USER_ID, cartRequest);
            return ResponseEntity.ok("장바구니에 강의가 성공적으로 추가되었습니다.");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //장바구니 삭제
    @DeleteMapping("/items/{index}")
    public ResponseEntity<String> deleteItem(@PathVariable Integer index) {
        try{
            cartService.removeCourseFromCart(USER_ID, index);
            return ResponseEntity.ok("성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //장바구니 목록
    @GetMapping
    public ResponseEntity<?> getCarts() {
        try{
            CartResponse cartResponse = cartService.getCart(USER_ID);
            return ResponseEntity.ok(cartResponse);
        }catch(CartNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
