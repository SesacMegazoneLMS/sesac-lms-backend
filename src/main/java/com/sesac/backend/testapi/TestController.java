package com.sesac.backend.testapi;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/tests")
@CrossOrigin("*")
@RestController
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDto> getTest(@PathVariable long id) {
        try {
            return ResponseEntity.ok(testService.getTestById(id));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<TestDto>> getAllTests() {
        try {
            return ResponseEntity.ok(testService.getAllTests());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<TestDto> createTest(@RequestBody TestDto testDto) {
        try {
            return ResponseEntity.ok(testService.saveTest(testDto));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TestDto> deleteTest(@PathVariable long id) {
        try {
            testService.deleteTest(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
