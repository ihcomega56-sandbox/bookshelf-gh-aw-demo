package com.example.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bookshelf.domain.Book;
import com.example.bookshelf.domain.Loan;
import com.example.bookshelf.repository.BookRepository;
import com.example.bookshelf.repository.LoanRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 貸出・返却のユースケースに対するテスト。
 *
 * <p>注意: 貸出上限（5 冊）や延滞チェックのテストは意図的に未整備にしてある
 * （Agentic Workflow によるテスト追加提案のデモ対象）。</p>
 */
@SpringBootTest
@Transactional
class BookshelfServiceTest {

    @Autowired
    private BookshelfService bookshelfService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        book = bookRepository.save(new Book("ドメイン駆動設計", "Eric Evans", "9784798121963", 1));
    }

    @Test
    void 在庫がある蔵書は貸し出せる() {
        Loan loan = bookshelfService.borrow(book.getId(), "alice");

        assertThat(loan.getBookId()).isEqualTo(book.getId());
        assertThat(loan.getBorrower()).isEqualTo("alice");
        assertThat(loan.getDueOn()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(loan.isReturned()).isFalse();
    }

    @Test
    void 在庫がない蔵書は貸し出せない() {
        bookshelfService.borrow(book.getId(), "alice");

        assertThatThrownBy(() -> bookshelfService.borrow(book.getId(), "bob"))
                .isInstanceOf(BookshelfException.class)
                .hasMessageContaining("在庫がありません");
    }

    @Test
    void 返却すると再び貸し出せる() {
        Loan loan = bookshelfService.borrow(book.getId(), "alice");

        Loan returned = bookshelfService.giveBack(loan.getId());

        assertThat(returned.isReturned()).isTrue();
        assertThat(bookshelfService.borrow(book.getId(), "bob")).isNotNull();
    }

    @Test
    void 同じ蔵書を重複して借りることはできない() {
        Book manyCopies = bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9784621303252", 5));
        bookshelfService.borrow(manyCopies.getId(), "alice");

        assertThatThrownBy(() -> bookshelfService.borrow(manyCopies.getId(), "alice"))
                .isInstanceOf(BookshelfException.class)
                .hasMessageContaining("重複");
    }
}
