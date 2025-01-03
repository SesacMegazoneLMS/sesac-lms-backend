package com.sesac.backend.reviews.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse <T>{
    private List<T> content; // 페이지에 포함된 데이터 목록
    private int currentPage; // 현재 페이지 번호
    private int totalPages; // 총 페이지 수
    private long totalElements; // 전체 데이터 개수
}
