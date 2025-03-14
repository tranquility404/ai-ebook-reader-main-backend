package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.dto.GenerateDataRequest;
import com.tranquility.ebookreader.model.Summary;
import com.tranquility.ebookreader.repository.SummaryRepository;
import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {
    private final SummaryRepository summaryRepository;

    public SummaryService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public void addSummary(String resourceId, GenerateDataRequest request) {
        Summary summary = new Summary(resourceId, request.getBookId(), request.getChapterHref(), AuthUtils.getUsername());
        summaryRepository.save(summary);
    }

    public Summary getSummary(String bookId, String chapterHref) {
        return summaryRepository.findByBookIdAndChapterHref(bookId, chapterHref);
    }
}
