package com.tranquility.ebookreader.service;

import com.tranquility.ebookreader.dto.UpdateReadHistoryRequest;
import com.tranquility.ebookreader.model.ReadHistory;
import com.tranquility.ebookreader.repository.BookRepository;
import com.tranquility.ebookreader.repository.ReadHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReadHistoryService {
    private final ReadHistoryRepository readHistoryRepository;
    private final BookRepository bookRepository;

    public ReadHistoryService(ReadHistoryRepository readHistoryRepository, BookRepository bookRepository) {
        this.readHistoryRepository = readHistoryRepository;
        this.bookRepository = bookRepository;
    }

    public void updateReadHistory(String bookId, String userId, UpdateReadHistoryRequest updateReadHistoryRequest) {
        Optional<ReadHistory> optional = readHistoryRepository.findByBookIdAndUserId(bookId, userId);
        ReadHistory readHistory = null;
        if (optional.isEmpty()) {
            readHistory = new ReadHistory(bookId, userId, updateReadHistoryRequest.getPageIdx(), updateReadHistoryRequest.getTime());
        } else {
            readHistory = optional.get();
            int pageIdx = updateReadHistoryRequest.getPageIdx();
            if (pageIdx > readHistory.getPageIdx()) {
                readHistory.setPageIdx(pageIdx);
                readHistory.setTime(updateReadHistoryRequest.getTime());
            }
        }
        readHistoryRepository.save(readHistory);
    }

    public List<ReadHistory> getLast30ReadHistories(String userId) {
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Order.desc("time")));
        return readHistoryRepository.findByUserId(userId, pageRequest).getContent();
    }
}
