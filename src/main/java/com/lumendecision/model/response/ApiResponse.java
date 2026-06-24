package com.lumendecision.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic API response envelope")
public class ApiResponse<T> {

    @Schema(description = "Whether the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Response payload")
    private T data;

    @Schema(description = "Error message, present only when success is false")
    private String message;

    @Schema(description = "Timestamp of the response")
    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
