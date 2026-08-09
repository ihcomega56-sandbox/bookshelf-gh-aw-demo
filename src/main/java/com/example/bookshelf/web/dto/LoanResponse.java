package com.example.bookshelf.web.dto;

import com.example.bookshelf.domain.Loan;
import java.time.LocalDate;

/**
 * 貸出レスポンス。
 */
public record LoanResponse(Long id, Long bookId, String borrower, LocalDate borrowedOn, LocalDate dueOn,
        LocalDate returnedOn) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(loan.getId(), loan.getBookId(), loan.getBorrower(), loan.getBorrowedOn(),
                loan.getDueOn(), loan.getReturnedOn());
    }
}
