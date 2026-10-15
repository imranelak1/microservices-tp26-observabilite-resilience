package lab;

import io.github.resilience4j.circuitbreaker.*;
import io.github.resilience4j.retry.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Retry interne puis circuit breaker externe : le fallback ne masque pas les tentatives. */
@Component
public class PricingClient {
  private final RestTemplate rest;
  private final String baseUrl;
  private final Retry retry;
  private final CircuitBreaker breaker;

  public PricingClient(
      RestTemplate rest,
      @Value("${pricing.base-url}") String baseUrl,
      RetryRegistry retries,
      CircuitBreakerRegistry breakers) {
    this.rest = rest;
    this.baseUrl = baseUrl;
    retry = retries.retry("pricing");
    breaker = breakers.circuitBreaker("pricing");
  }

  public double getPrice(long id) {
    try {
      return breaker.executeSupplier(
          () ->
              retry.executeSupplier(
                  () -> {
                    Double value = rest.getForObject(baseUrl + "/api/prices/" + id, Double.class);
                    if (value == null) throw new IllegalStateException("Prix vide");
                    return value;
                  }));
    } catch (RuntimeException failure) {
      return 0.0;
    }
  }
}
