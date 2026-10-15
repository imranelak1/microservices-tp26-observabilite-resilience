package lab;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService service;

  public BookController(BookService service) {
    this.service = service;
  }

  @GetMapping
  public List<Book> all() {
    return service.all();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book create(@Valid @RequestBody Book book) {
    return service.create(book);
  }

  @PostMapping("/{id}/borrow")
  public BookService.BorrowResult borrow(@PathVariable long id) {
    return service.borrow(id);
  }
}
