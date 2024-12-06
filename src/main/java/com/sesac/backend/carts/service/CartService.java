package com.sesac.backend.carts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sesac.backend.carts.domain.Cart;
import com.sesac.backend.carts.dto.request.CartRequest;
import com.sesac.backend.carts.dto.response.CartResponse;
import com.sesac.backend.carts.exception.CartNotFoundException;
import com.sesac.backend.carts.repository.CartRepository;
import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    //장바구니 추가 메서드-------------------------------------
    public void addCourseToCart(UUID userId, CartRequest cartRequest) {
        try {
            // UUID를 사용하여 user 정보 조회
            User user = userRepository.findByUserId(userId).orElseThrow();

            // courseId를 사용하여 코스 정보를 조회
            Course course = courseRepository.findById(cartRequest.getCourseId()).orElseThrow(
                    () -> new NoSuchElementException("해당 강의에 대한 정보가 없습니다."));

            // 기존 Cart 조회 or 생성
            Cart cart = cartRepository.findByUser(user)
                    .orElse(Cart.builder().user(user).cartInfo(new ObjectMapper().createObjectNode()).build());

            // cartInfo 업데이트
            // ObjectMapper : Java 객체와 JSON 간의 변환을 쉽게 해주는 기능
            ObjectNode cartInfo = (ObjectNode) cart.getCartInfo();
            int nextIndex = findNextIndex(cartInfo);

            // 새로운 course 정보를 담을 객체 생성
            ObjectNode newItem = createCourseNode(course);

            // 순차적 인덱스로 course 추가
            cartInfo.put(String.valueOf(nextIndex), newItem);

            cart.setUser(user);
            cart.setCartInfo(cartInfo);

            cartRepository.save(cart); // Cart 저장
        } catch (Exception e) {
            throw new RuntimeException("장바구니 담기 실패", e);
        }
    }

    private int findNextIndex(ObjectNode cartInfo) {
        int nextIndex = 1;
        while (cartInfo.has(String.valueOf(nextIndex))) {
            nextIndex++;
        }
        return nextIndex;
    }

    private ObjectNode createCourseNode(Course course) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode newItem = objectMapper.createObjectNode();
        newItem.put("courseId", course.getId());
        newItem.set("instructorName", TextNode.valueOf(course.getInstructor().getNickname()));
        newItem.put("title", course.getTitle());
        newItem.put("price", course.getPrice());
        newItem.put("level", course.getLevel().toString());
        newItem.put("category", course.getCategory().toString());
        newItem.put("thumbnail", course.getThumbnail());

        return newItem;
    }
    //--------------------------------------------------------------

    // 장바구니에서 삭제------------------------------------------------
    public void removeCourseFromCart(UUID userId, int index) {
        try {
            User user = userRepository.findByUserId(userId).orElseThrow();
            Cart cart = cartRepository.findByUser(user).orElseThrow();

            ObjectNode cartInfo = (ObjectNode) cart.getCartInfo();

            // 특정 인덱스 삭제
            cartInfo.remove(String.valueOf(index));

            // 인덱스 재정렬
            ObjectNode reorderedCartInfo = reorderCartInfo(cartInfo);

            cart.setCartInfo(reorderedCartInfo);
            cartRepository.save(cart);
        } catch (Exception e) {
            throw new RuntimeException("장바구니 항목 삭제 실패", e);
        }
    }

    // 인덱스 재정렬 메서드
    private ObjectNode reorderCartInfo(ObjectNode cartInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reorderedCartInfo = objectMapper.createObjectNode();
        int newIndex = 1;

        for (int i = 1; i <= cartInfo.size(); i++) {
            if (cartInfo.has(String.valueOf(i))) {
                reorderedCartInfo.set(String.valueOf(newIndex), cartInfo.get(String.valueOf(i)));
                newIndex++;
            }
        }

        return reorderedCartInfo;
    }
    //-----------------------------------------------------------------


    // getCart pagination---------------------------------------------------------------------
    public CartResponse getCart(UUID userId, int page, int size){
        try{
            User user = userRepository.findByUserId(userId).orElseThrow(
                    () -> new UserPrincipalNotFoundException("로그인을 해주세요"));

            Cart cart = cartRepository.findByUser(user).orElseThrow(
                    () -> new CartNotFoundException("장바구니 목록이 없습니다."));

            // 페이징 처리를 위해 장바구니 정보를 가져옴
            ObjectNode cartInfo = (ObjectNode) cart.getCartInfo();

            // cartInfo의 키로 리스트를 생성 -> json에 객체로 담겨있기 때문
            List<String> keys = new ArrayList<>();
            cartInfo.fieldNames().forEachRemaining(keys::add);

            // 키 정렬 (필요에 따라 정렬 기준을 설정)
            Collections.sort(keys);

            // 페이징된 키 리스트 생성
            List<String> pagedKeys = getPagedKeys(keys, page, size);

            // 다음, 이전 페이지가 호출될 때마다 그에 해당하는 CartResponse 생성
            ObjectNode filteredCartInfo = new ObjectNode(JsonNodeFactory.instance);
            for (String key : pagedKeys) {
                filteredCartInfo.set(key, cartInfo.get(key)); // cartInfo에서 직접 가져옴
            }

            return CartResponse.builder()
                    .cartInfo(filteredCartInfo)
                    .totalItems(keys.size())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("장바구니 목록 호출 실패", e);
        }
    }

    private List<String> getPagedKeys(List<String> keys, int page, int size) {
        try {
            // 유효성 검사
            if (page < 1 || size < 1) {
                throw new IllegalArgumentException();
            }

            // 페이징 처리
            // 시작 인덱스 계산 (index는 0부터 시작하므로 -1을 해 줌)
            int start = (page - 1) * size;

            // 시작 인덱스가 리스트의 크기를 초과하는 경우
            if (start >= keys.size()) {
                throw new IndexOutOfBoundsException();
            }

            int end = Math.min(start + size, keys.size());

            return keys.subList(start, end);
        }catch( IndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 페이지 요청입니다.", e);
        }
    }
    //-------------------------------------------------------------------------

}
