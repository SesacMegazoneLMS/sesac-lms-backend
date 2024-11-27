package com.sesac.backend.testapi;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestDto {

    private long id;
    private String message;
}
