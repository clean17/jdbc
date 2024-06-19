package spring.jdbc.global.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // 스택 트레이스의 상위 5줄을 추출
        StackTraceElement[] stackTrace = ex.getStackTrace();
        StringBuilder top5StackTrace = new StringBuilder();
        top5StackTrace.append(ex.toString()).append("\n");

        int lines = Math.min(stackTrace.length, 5);
        for (int i = 0; i < lines; i++) {
            top5StackTrace.append("\tat ").append(stackTrace[i].toString()).append("\n");
        }

        // 상위 5줄의 스택 트레이스를 로그에 출력
        logger.error("Exception: {}", top5StackTrace.toString());

        return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
