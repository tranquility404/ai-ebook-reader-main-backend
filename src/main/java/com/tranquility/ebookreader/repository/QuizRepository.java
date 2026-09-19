package com.tranquility.ebookreader.repository;

import com.tranquility.ebookreader.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
    Quiz findByBookIdAndChapterHref(String bookId, String chapterId);
}
