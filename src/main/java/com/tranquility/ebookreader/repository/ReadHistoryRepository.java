package com.tranquility.ebookreader.repository;

import com.tranquility.ebookreader.model.ReadHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReadHistoryRepository extends MongoRepository<ReadHistory, String> {

    Page<ReadHistory> findByUserId(String userId, PageRequest pageRequest);
    Optional<ReadHistory> findByBookIdAndUserId(String bookId, String userId);

}
