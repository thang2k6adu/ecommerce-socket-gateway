package com.ecommerce.socketgateway.common.pagination;

import com.ecommerce.socketgateway.common.api.PageMeta;
import com.ecommerce.socketgateway.common.api.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public final class PageResponses {

	private PageResponses() {
	}

	public static <T, E> PageResponse<T> map(Page<E> page, Function<E, T> mapper) {
		List<T> content = page.getContent().stream().map(mapper).toList();
		return PageResponse.<T>builder()
				.content(content)
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.last(page.isLast())
				.first(page.isFirst())
				.build();
	}

	public static PageMeta metaFrom(Page<?> page) {
		return PageMeta.builder()
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.last(page.isLast())
				.first(page.isFirst())
				.build();
	}

}
