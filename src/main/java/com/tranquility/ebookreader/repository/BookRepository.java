package com.tranquility.ebookreader.repository;

import com.tranquility.ebookreader.model.BookInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<BookInfo, String> {
    List<BookInfo> findByPostedBy(String postedBy, Pageable pageable);
    List<BookInfo> findByIdIn(List<String> bookIds, Pageable pageable);
    BookInfo findByGpbkIdAndPostedBy(String gpbkId, String postedBy);

    @Query(value = "{ '_id': ?0 }", fields = "{ 'chapters': 1 }")
    BookInfo findChaptersById(String id);

    @Query(value = "{ '_id': ?0 }", fields = "{ 'chapters': 1, 'cloudUrl': 1 }")
    BookInfo findChaptersAndCloudUrlById(String id);

    @Query(value = "{ '_id': ?0 }", fields = "{ 'cloudUrl': 1 }")
    BookInfo findCloudUrlById(String id);

    @Query("{'postedBy': { $ne: ?0 }}")
    List<BookInfo> findByPostedByNotOrderByUploadedAtDesc(String id, Pageable pageable);


    List<BookInfo> findByTitleContaining(String title, Pageable pageable);
}

