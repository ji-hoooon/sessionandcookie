package shop.mtcoding.hiberpc.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.mtcoding.hiberpc.handler.ex.MyException;

@RestControllerAdvice
public class MyExceptionAdvice {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<?> error(MyException e){
        return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
    }
}
