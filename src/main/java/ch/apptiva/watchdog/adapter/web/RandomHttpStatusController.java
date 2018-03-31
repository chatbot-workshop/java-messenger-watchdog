package ch.apptiva.watchdog.adapter.web;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class RandomHttpStatusController {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> randomHttpStatus() {
        if (Math.random() < 0.8) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
