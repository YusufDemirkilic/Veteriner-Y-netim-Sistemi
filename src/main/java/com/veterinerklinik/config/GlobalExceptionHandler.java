package com.veterinerklinik.config;


import com.veterinerklinik.config.exeption.NotFoundException;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public  ResponseEntity<Result> handleNotfoundExeption(NotFoundException e){
        return new ResponseEntity<>(ResultHelper.notFoundError(e.getMessage()),HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultData<List<String>>> handleValidationErrors(MethodArgumentNotValidException e){

        List<String> validationErrorList = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        //ResultData<List<String>> resultData = new ResultData<>(false, Msg.VALIDATE_ERROR,"400",validationErrorList);
        return  new ResponseEntity<>(ResultHelper.validateError(validationErrorList), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ResultData<String>> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultHelper.errorWithData(ex.getMessage(), null, HttpStatus.BAD_REQUEST));
    }
   @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultData<String>> handleRuntimeException(RuntimeException ex) {
        // Loglama işlemleri burada yapılabilir
        return new ResponseEntity<>(ResultHelper.Error500(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultData<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "Geçersiz tarih/saat formatı";
        return new ResponseEntity<>(ResultHelper.dateFormat(errorMessage), HttpStatus.BAD_REQUEST);
    }

}


