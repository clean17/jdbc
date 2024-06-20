## Exception

체크 예외는 예외가 발생할 가능성이 있는 위치를 컴파일러가 예측한다<br>
예외 처리를 하지 않으면 컴파일 에러가 나므로 직접처리하거나 호출한곳으로 예외를 넘겨야한다

```java
    try {
        FileReader reader = new FileReader("example.txt");
    } catch (FileNotFoundException e) {        
        e.printStackTrace();    
    }
```
```java
public void readFile() throws FileNotFoundException {
    FileReader reader = new FileReader("example.txt");
}
```
처리하지 않으면 컴파일러가 체크 예외를 찾아서 에러를 표시해준다
```java
import java.io.*;

public class Main {
    public static void main(String[] args) {
        FileReader reader = new FileReader("example.txt"); // 컴파일 에러
    }
}
```
### RuntimeException 활용
심각한 비즈니스 로직같은 경우 개발자가 잊지 않도록 체크 익셉션을 명시해준다<br>
일반적으로는 익셉션이 발생하면 런타임 예외를 사용하는편이 좋다<br>
아래와 같이 체크예외도 런타임 예외로 바꾼다면 throw를 붙일 필요가 없으므로 기술을 변경해도 코드를 변경하지 않아도 된다<br>
```java
public class CheckedException extends Exception { // 체크예외가 있다고 가정
    public CheckedException(String message) {
        super(message);
    }
}
```
`RuntimeException`을 상속하여 런타임 익셉션을 만든다
```java
public class CustomRuntimeException extends RuntimeException { 
    public CustomRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

@Service
public class ExampleService {

    public void performAction() {
        try {
            // 비즈니스 로직 수행 중 체크 예외 발생 가능성 있음
            throw new CheckedException("Checked exception occurred");
        } catch (CheckedException e) {
            // 체크 예외를 런타임 예외로 변환
            throw new CustomRuntimeException("Checked exception wrapped in runtime exception", e);
        }
    }
}
```
런타임 예외이므로 `throw`를 명시않으면 자동으로 호출한 곳으로 예외가 던져진다

전역 익셉션 핸들러가 해당 익셉션을 처리하도록 한다
```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<String> handleCustomRuntimeException(CustomRuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
[Back to main README](../README.md)