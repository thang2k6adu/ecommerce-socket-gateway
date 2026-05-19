package com.ecommerce.socketgateway.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "message", "data", "meta", "error", "timestamp"})
public class ApiResponse<T> {

	private Boolean success;
	private String message;
	private T data;
	private PageMeta meta;
	private String error;

	@Builder.Default
	private Instant timestamp = Instant.now();

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder().success(true).data(data).build();
	}

	public static <T> ApiResponse<T> successWithMeta(T data, PageMeta meta) {
		return ApiResponse.<T>builder().success(true).data(data).meta(meta).build();
	}

	public static <T> ApiResponse<T> error(String error) {
		return ApiResponse.<T>builder().success(false).error(error).build();
	}

}
