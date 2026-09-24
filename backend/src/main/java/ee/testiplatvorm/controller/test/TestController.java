package ee.testiplatvorm.controller.test;


import ee.testiplatvorm.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/api/tests")
    @Operation(summary = "Tagastab andmebaasist kõik testid.")
    public void findAllTests() {
        testService.findAllTests();
    }


}
