package lab;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "pricing.failure-percent=0")
@AutoConfigureMockMvc
class PricingApiTest {
  @Autowired MockMvc mvc;

  @Test
  void stablePrice() throws Exception {
    mvc.perform(get("/api/prices/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("55.0"));
  }

  @Test
  void forcedFailureIsExplicit() {
    org.junit.jupiter.api.Assertions.assertThrows(
        Exception.class, () -> mvc.perform(get("/api/prices/1?fail=true")));
  }
}
