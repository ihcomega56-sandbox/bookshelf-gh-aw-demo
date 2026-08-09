package com.example.bookshelf.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 蔵書登録リクエスト。
 */
public record BookRequest(
        @NotBlank(message = "タイトルは必須です") String title,
        @NotBlank(message = "著者は必須です") String author,
        @Pattern(regexp = "\\d{13}", message = "ISBN はハイフンなしの 13 桁で指定してください") String isbn,
        @Min(value = 1, message = "蔵書数は 1 以上で指定してください") int totalCopies) {
}
