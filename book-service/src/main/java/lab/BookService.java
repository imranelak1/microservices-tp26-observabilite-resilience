package lab;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookService {
  private final BookRepository books;
  private final StockService stocks;
  private final PricingClient prices;

  public BookService(BookRepository books, StockService stocks, PricingClient prices) {
    this.books = books;
    this.stocks = stocks;
    this.prices = prices;
  }

  public List<Book> all() {
    return books.findAll();
  }

  public Book create(Book book) {
    if (books.findByTitle(book.getTitle()).isPresent())
      throw new IllegalArgumentException("Titre déjà existant");
    return books.saveAndFlush(book);
  }

  public BorrowResult borrow(long id) {
    Book book = stocks.borrow(id);
    return new BorrowResult(book.getId(), book.getTitle(), book.getStock(), prices.getPrice(id));
  }

  public record BorrowResult(Long id, String title, int stockLeft, double price) {}
}
