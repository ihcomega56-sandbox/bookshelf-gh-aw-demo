package com.example.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bookshelf.domain.Book;
import com.example.bookshelf.domain.Loan;
import com.example.bookshelf.repository.BookRepository;
import com.example.bookshelf.repository.LoanRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
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

    @Test
    void 貸出期限を二回更新できる() {
        Loan loan = bookshelfService.borrow(book.getId(), "alice");
        LocalDate originalDueOn = loan.getDueOn();

        Loan firstRenewal = bookshelfService.renew(loan.getId());
        assertThat(firstRenewal.getDueOn()).isEqualTo(originalDueOn.plusDays(14));
        assertThat(firstRenewal.getRenewalCount()).isEqualTo(1);

        Loan secondRenewal = bookshelfService.renew(loan.getId());
        assertThat(secondRenewal.getDueOn()).isEqualTo(originalDueOn.plusDays(28));
        assertThat(secondRenewal.getRenewalCount()).isEqualTo(2);
    }

    @Test
    void 返却済みの貸出は更新できない() {
        Loan loan = bookshelfService.borrow(book.getId(), "alice");
        bookshelfService.giveBack(loan.getId());

        assertThatThrownBy(() -> bookshelfService.renew(loan.getId()))
                .isInstanceOf(BookshelfException.class)
                .hasMessageContaining("返却済み");
    }

    @Test
    void 延滞中の貸出は更新できない() {
        Loan overdue = loanRepository.save(new Loan(book.getId(), "alice",
                LocalDate.now().minusDays(15), LocalDate.now().minusDays(1)));

        assertThatThrownBy(() -> bookshelfService.renew(overdue.getId()))
                .isInstanceOf(BookshelfException.class)
                .hasMessageContaining("延滞中");
    }

    @Test
    void 存在しない貸出は更新できない() {
        assertThatThrownBy(() -> bookshelfService.renew(Long.MAX_VALUE))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("貸出記録が見つかりません");
    }
}
