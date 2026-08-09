package com.example.bookshelf.service;

/**
 * 業務ルール違反（在庫切れ・貸出上限超過など）を表す例外。
 */
public class BookshelfException extends RuntimeException {

    public BookshelfException(String message) {
        super(message);
    }
}
