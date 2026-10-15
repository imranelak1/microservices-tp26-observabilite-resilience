package lab;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockIntegrationTest {
  @Autowired BookRepository books;
  @Autowired StockService stock;

  @BeforeEach
  void clear() {
    books.deleteAll();
  }

  @Test
  void fiftyConcurrentBorrowersCannotOversell() throws Exception {
    Long id = books.saveAndFlush(new Book("Concurrency", "Demo", 10)).getId();
    ExecutorService pool = Executors.newFixedThreadPool(12);
    try {
      List<Callable<Boolean>> work = new ArrayList<>();
      for (int i = 0; i < 50; i++)
        work.add(
            () -> {
              try {
                stock.borrow(id);
                return true;
              } catch (IllegalStateException e) {
                return false;
              }
            });
      long successes = 0;
      for (Future<Boolean> future : pool.invokeAll(work)) if (future.get()) successes++;
      assertEquals(10, successes);
      assertEquals(0, books.findById(id).orElseThrow().getStock());
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void exhaustedStockRollsBack() {
    Long id = books.saveAndFlush(new Book("Empty", "Demo", 0)).getId();
    assertThrows(IllegalStateException.class, () -> stock.borrow(id));
    assertEquals(0, books.findById(id).orElseThrow().getStock());
  }

  @Test
  void missingBookIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> stock.borrow(9999));
  }
}
