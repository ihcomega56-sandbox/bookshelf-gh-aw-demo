package com.example.bookshelf.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 貸出リクエスト。
 *
 * <p>TODO: 認証・認可が未導入のため borrower をクライアントから受け取っている。
 * 認証基盤の導入後は認証済みユーザー情報から解決する。</p>
 */
public record BorrowRequest(@NotBlank(message = "借用者は必須です") String borrower) {
}
