package com.java.electro.store.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseMessage {
    private String message;
    private boolean success;
    private HttpStatus status;

}
