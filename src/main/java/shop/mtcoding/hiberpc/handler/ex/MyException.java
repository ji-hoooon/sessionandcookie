package shop.mtcoding.hiberpc.handler.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MyException extends RuntimeException{
    private HttpStatus httpStatus;

    // 100프로 유효성 검사 실패
    public MyException(String msg) {
        this(msg, HttpStatus.BAD_REQUEST);
    }

    public MyException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }
}
