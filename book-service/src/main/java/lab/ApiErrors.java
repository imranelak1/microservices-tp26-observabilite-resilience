package lab;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiErrors {
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> invalid(IllegalArgumentException e) {
    return Map.of("error", e.getMessage());
  }

  @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, String> conflict(Exception e) {
    return Map.of(
        "error", e instanceof IllegalStateException ? e.getMessage() : "Contrainte de données");
  }
}
