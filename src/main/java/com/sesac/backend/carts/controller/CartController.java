package com.sesac.backend.carts.controller;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.carts.dto.request.CartRequest;
import com.sesac.backend.carts.dto.response.CartResponse;
import com.sesac.backend.carts.exception.CartNotFoundException;
import com.sesac.backend.carts.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequestMapping("/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    //장바구니 추가
    @PostMapping("/items")
    public ResponseEntity<String> addItem(@RequestBody CartRequest cartRequest, @CurrentUser UUID USER_ID) {
        try {
            cartService.addCourseToCart(USER_ID, cartRequest);
            return ResponseEntity.ok("장바구니에 강의가 성공적으로 추가되었습니다.");
        } catch (SecurityException e) {
            // 인증/인가 오류는 403 Forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch(IllegalArgumentException e) {
            // 잘못된 요청 데이터는 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(NoSuchElementException e) {
            // 요청한 리소스가 없는 경우는 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(RuntimeException e) {
            // 서버 내부 오류는 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //장바구니 삭제
    @DeleteMapping("/items")
    public ResponseEntity<String> deleteItems(@RequestBody List<Integer> indexes, @CurrentUser UUID USER_ID) {
        try{
            cartService.removeCourseFromCart(USER_ID, indexes);
            return ResponseEntity.ok("성공적으로 삭제되었습니다.");
        }catch (SecurityException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //장바구니 목록
    @GetMapping("")
    public ResponseEntity<?> getCarts( @CurrentUser UUID USER_ID, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size ) {
        try{
            CartResponse cartResponse = cartService.getCart(USER_ID, page, size);
            return ResponseEntity.ok(cartResponse);
        }catch(CartNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (SecurityException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
