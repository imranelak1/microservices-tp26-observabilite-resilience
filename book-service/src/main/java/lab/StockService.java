package lab;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Le verrou est partagé par toutes les JVM car il appartient à MySQL. */
@Service
public class StockService {
  private final BookRepository books;

  public StockService(BookRepository books) {
    this.books = books;
  }

  @Transactional
  public Book borrow(long id) {
    Book book =
        books
            .findByIdForUpdate(id)
            .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));
    book.decrementStock();
    return book;
  }
}
