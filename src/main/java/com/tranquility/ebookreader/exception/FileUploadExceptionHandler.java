package com.tranquility.ebookreader.exception;

import com.tranquility.ebookreader.model.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class FileUploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.badRequest().body(new ErrorMessage(
                "The uploaded file exceeds the maximum allowed size."
        ));
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorMessage> handleRuntimeException() {
//        return Re
//    }

}

