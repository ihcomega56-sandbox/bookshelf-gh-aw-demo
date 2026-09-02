package com.example.bookshelf.web;

import com.example.bookshelf.service.BookshelfService;
import com.example.bookshelf.web.dto.BorrowRequest;
import com.example.bookshelf.web.dto.LoanResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 貸出・返却に関する REST API。
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final BookshelfService bookshelfService;

    public LoanController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }

    @PostMapping("/books/{bookId}")
    public LoanResponse borrow(@PathVariable("bookId") Long bookId, @Valid @RequestBody BorrowRequest request) {
        return LoanResponse.from(bookshelfService.borrow(bookId, request.borrower()));
    }

    @PostMapping("/{loanId}/return")
    public LoanResponse giveBack(@PathVariable("loanId") Long loanId) {
        return LoanResponse.from(bookshelfService.giveBack(loanId));
    }

    @PostMapping("/{loanId}/renew")
    public LoanResponse renew(@PathVariable("loanId") Long loanId) {
        return LoanResponse.from(bookshelfService.renew(loanId));
    }

    @GetMapping("/overdue")
    public List<LoanResponse> overdue() {
        return bookshelfService.findOverdueLoans().stream()
                .map(LoanResponse::from)
                .toList();
    }
}
