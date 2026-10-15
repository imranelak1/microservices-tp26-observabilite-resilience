package lab;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/** Le paramètre fail permet de reproduire une panne sans dépendre du hasard. */
@RestController
@RequestMapping("/api/prices")
public class PricingController {
  private final int failurePercent;

  public PricingController(@Value("${pricing.failure-percent}") int failurePercent) {
    this.failurePercent = failurePercent;
  }

  @GetMapping("/{bookId}")
  public double price(
      @PathVariable long bookId, @RequestParam(defaultValue = "false") boolean fail) {
    if (fail || ThreadLocalRandom.current().nextInt(100) < failurePercent)
      throw new IllegalStateException("Pricing unavailable");
    return 50.0 + (bookId % 10) * 5.0;
  }
}
