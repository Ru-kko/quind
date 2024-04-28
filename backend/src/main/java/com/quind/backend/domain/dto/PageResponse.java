package com.quind.backend.domain.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> { 
  private Integer page;
  private Long totalItems;
  private List<T> data;
  private Integer totalPages;

  public PageResponse(Page<T> data) {
    this.page = data.getNumber();
    this.totalItems = data.getTotalElements();
    this.data = data.getContent();
    this.totalPages = data.getTotalPages();
  }
}
