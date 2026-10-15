package lab;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
  Optional<Book> findByTitle(String title);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select b from Book b where b.id=:id")
  Optional<Book> findByIdForUpdate(@Param("id") Long id);
}
