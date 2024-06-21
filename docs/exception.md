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
### 체크 예외 인터페이스
체크 예외를 런타임 예외로 변경하여 런타임을 처리한다<br>
인터페이스의 구현체가 예외를 던지려면 선언 메서드도 `throws`로 선언되어 있어야 하므로 비효율적이다<br>

예를들어 `throws SQLException` 같이 만들게 되면 JDBC에 종속되므로 코드변경이 불가피한 코드가 된다<br>
인터페이스를 만드는 목적이 구현체가 쉽게 다른 기능을 추가하기 위함인데 종속이 되므로 잘못된 구현이된다<br>

위에서 설명한 동일한 방법으로 아래와 같은 코드 구성이 된다
```java
public static void main(String[] args) {
    try {
        new Example().processQueries();
    } catch (CustomRuntimeException e) {
        e.printStackTrace();
    }
}

// 서비스 로직에서는 익셉션을 제거하여 순수한 로직만 남긴다
public void processQueries() {
    for (int i = 0; i < 10; i++) {
        executeQuery(i); // 런타임 익셉션이 발생 시 즉시 중단 후 스택 종료
    }
}

// 데이터 접근 계층에서 런타임 예외로 변경
public void executeQuery(int index) {
    try {
        //SQL 로직
        if (index == 5) { //예외 발생
            throw new SQLException("Simulated database error");
        }
    } catch (SQLException e) {
        throw new CustomRuntimeException("Error executing query for index " + index, e);
    }
}
```
### 특정 에러코드 대응
아래와 같은 방법으로 특정 DB 에러코드에 대응되는 방법도 있다<br>
하지만 DB마다 에러코드가 다르므로 매번 변경해야 하는 단점이 존재
```java
    // 데이터 접근 계층
    public void executeQuery() {
        try {
            // SQL 로직
            throw new SQLException("Database error", "08001", 1001);
        } catch (SQLException e) {
            // 특정 예외 코드마다 대응되는 런타임 익셉션으로 대체한다
            // if (e.getErrorCode() == 1001) 
            if ("08001".equals(e.getSQLState())) {
                // 복구가 가능한 예외
                // 복구가 불가능한 예외는 전역 처리기로 보낸다
                throw new CustomDatabaseException("Custom message for error code 1001", 1001);
            } else {
                throw new CustomDatabaseException("General database error", 0);
            }
        }
    }

    // 서비스 로직
    public void performService() {
        try {
            dataAccess.executeQuery();
        } catch (CustomDatabaseException e) {
            // 특정 오류를 처리하는 로직
        }
    }
```
위가 같은 문제를 스프링이 추상화하여 처리할 수 있다

[Back to main README](../README.md)