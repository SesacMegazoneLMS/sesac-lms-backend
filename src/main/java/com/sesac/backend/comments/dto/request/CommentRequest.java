package com.sesac.backend.comments.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequest {

    private Long id;
}
