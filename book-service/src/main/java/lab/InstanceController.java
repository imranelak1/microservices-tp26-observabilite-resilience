package lab;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class InstanceController {
  @Value("${server.port}")
  int port;

  @GetMapping("/api/debug/instance")
  public Map<String, Object> instance() {
    return Map.of(
        "instance", System.getenv().getOrDefault("HOSTNAME", "local"), "internalPort", port);
  }
}
