package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.dto.TestUpdateRequest;
import com.tranquility.ebookreader.model.Test;
import com.tranquility.ebookreader.model.TestType;
import com.tranquility.ebookreader.repository.TestRepository;
import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public void updateTestHistory(TestUpdateRequest request, TestType type) {
        Test test = new Test(request.getTestId(), AuthUtils.getUsername(), request.getScore(), type);
        testRepository.save(test);
    }
}
