package com.sesac.backend.testapi;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public TestDto getTestById(long id) {
        Test entity = testRepository.findById(id).orElseThrow(RuntimeException::new);
        return TestDto.builder().id(entity.getId()).message(entity.getMessage()).build();
    }

    public List<TestDto> getAllTests() {
        return testRepository.findAll().stream().map(
                entity -> TestDto.builder().id(entity.getId()).message(entity.getMessage()).build())
            .toList();
    }

    public TestDto saveTest(TestDto testDto) {
        Test entity = testRepository.save(Test.builder().id(testDto.getId()).message(
            testDto.getMessage()).build());
        return TestDto.builder().id(entity.getId()).message(entity.getMessage()).build();
    }

    public void deleteTest(long id) {
        testRepository.deleteById(id);
    }
}
