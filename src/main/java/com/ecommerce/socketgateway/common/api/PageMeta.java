package com.ecommerce.socketgateway.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageMeta {

	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private Boolean first;
	private Boolean last;

}
