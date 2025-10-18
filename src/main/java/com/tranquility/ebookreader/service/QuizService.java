package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.dto.GenerateDataRequest;
import com.tranquility.ebookreader.model.Quiz;
import com.tranquility.ebookreader.repository.QuizRepository;
import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz getQuiz(String bookId, String chapterHref) {
        return quizRepository.findByBookIdAndChapterHref(bookId, chapterHref);
    }

    public Quiz getQuiz(String quizId) {
        Optional<Quiz> optional = quizRepository.findById(quizId);
        return optional.orElse(null);
    }

    public String addQuiz(String resourceId, GenerateDataRequest request) {
        Quiz quiz = new Quiz(resourceId, request.getBookId(), request.getChapterHref(), AuthUtils.getUsername());
        Quiz res = quizRepository.save(quiz);
        return res.getId();
    }
}
