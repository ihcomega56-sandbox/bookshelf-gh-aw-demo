package com.example.bookshelf.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * 貸出記録を表すエンティティ。返却されると returnedOn がセットされる。
 */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    /** 借りた社員の識別子。認証基盤がないため、いまはリクエストの値をそのまま保存している。 */
    @Column(nullable = false)
    private String borrower;

    @Column(nullable = false)
    private LocalDate borrowedOn;

    @Column(nullable = false)
    private LocalDate dueOn;

    private LocalDate returnedOn;

    protected Loan() {
        // JPA 用
    }

    public Loan(Long bookId, String borrower, LocalDate borrowedOn, LocalDate dueOn) {
        this.bookId = bookId;
        this.borrower = borrower;
        this.borrowedOn = borrowedOn;
        this.dueOn = dueOn;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBorrower() {
        return borrower;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public LocalDate getDueOn() {
        return dueOn;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public boolean isReturned() {
        return returnedOn != null;
    }

    public void markReturned(LocalDate returnedOn) {
        this.returnedOn = returnedOn;
    }

    /**
     * 返却期限日を強制的に上書きする。延滞状態のテストデータを作成する用途を想定している。
     */
    public void forceSetDueOn(LocalDate dueOn) {
        this.dueOn = dueOn;
    }
}
