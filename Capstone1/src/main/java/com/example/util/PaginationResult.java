package com.example.util;

import java.util.List;

public class PaginationResult<T> {
    private List<T> items;
    private int currentPage;
    private int pageSize;
    private long totalItems;

    public PaginationResult(List<T> items, int currentPage, int pageSize, long totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public List<T> getItems() { return items; }
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public long getTotalItems() { return totalItems; }


    public int getTotalPages() {
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    public boolean hasNext() {
        return currentPage * pageSize < totalItems;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }
}