package lab;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class PricingResilienceTest {
  @Autowired RestTemplate rest;
  @Autowired PricingClient prices;
  @Autowired CircuitBreakerRegistry breakers;

  @BeforeEach
  void reset() {
    breakers.circuitBreaker("pricing").reset();
  }

  @Test
  void retriesTwiceBeforeSuccessfulResponse() {
    var server = MockRestServiceServer.bindTo(rest).build();
    server.expect(requestTo("http://localhost:8082/api/prices/1")).andRespond(withServerError());
    server.expect(requestTo("http://localhost:8082/api/prices/1")).andRespond(withServerError());
    server
        .expect(requestTo("http://localhost:8082/api/prices/1"))
        .andRespond(withSuccess("55.0", org.springframework.http.MediaType.APPLICATION_JSON));
    assertEquals(55.0, prices.getPrice(1));
    server.verify();
  }

  @Test
  void outageFallsBackAndOpensCircuit() {
    var server = MockRestServiceServer.bindTo(rest).ignoreExpectOrder(true).build();
    server
        .expect(
            org.springframework.test.web.client.ExpectedCount.times(15),
            requestTo("http://localhost:8082/api/prices/1"))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    for (int i = 0; i < 5; i++) assertEquals(0.0, prices.getPrice(1));
    assertEquals(
        io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN,
        breakers.circuitBreaker("pricing").getState());
    assertEquals(0.0, prices.getPrice(1));
    server.verify();
  }
}
