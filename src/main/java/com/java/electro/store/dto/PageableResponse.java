package com.java.electro.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int PageSize;
    private long totalElement;
    private int totalPages;
    private boolean lastPage;
}
