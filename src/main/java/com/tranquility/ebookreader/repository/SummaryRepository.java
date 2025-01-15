package com.tranquility.ebookreader.repository;

import com.tranquility.ebookreader.model.Summary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends MongoRepository<Summary, String> {
    Summary findByBookIdAndChapterHref(String bookId, String chapterId);
}
