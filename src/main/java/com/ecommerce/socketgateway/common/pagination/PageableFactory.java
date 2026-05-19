package com.ecommerce.socketgateway.common.pagination;

import com.ecommerce.socketgateway.common.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableFactory {

	private PageableFactory() {
	}

	public static Pageable sorted(int page, int size, String sortBy, String sortDirection, Set<String> allowedFields) {
		String field = sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy;
		if (!allowedFields.contains(field)) {
			throw new BadRequestException("Invalid sort field: " + field);
		}
		Sort sort = "desc".equalsIgnoreCase(sortDirection)
				? Sort.by(field).descending()
				: Sort.by(field).ascending();
		int normalizedPage = Math.max(page, PaginationConstants.DEFAULT_PAGE);
		int normalizedSize = Math.min(Math.max(size, 1), PaginationConstants.MAX_SIZE);
		return PageRequest.of(normalizedPage, normalizedSize, sort);
	}

}
