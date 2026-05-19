package com.ecommerce.socketgateway.common.pagination;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageParams {

	private int page = PaginationConstants.DEFAULT_PAGE;
	private int size = PaginationConstants.DEFAULT_SIZE;
	private String sortBy = "createdAt";
	private String sortDirection = "desc";

}
