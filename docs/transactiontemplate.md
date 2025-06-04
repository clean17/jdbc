## TransactionTemplate
Spring Framework에서 트랜잭션 관리를 보다 쉽고 안전하게 수행하기 위한 방법을 제공하는 디자인 패턴<br>
반복되는 트랜잭션 관리 코드를 템플릿으로 정의하고, 비즈니스 로직만 사용자가 제공하도록 구조화<br>

Spring에서는 이 패턴을 `TransactionTemplate` 클래스를 통해 구현한다<br>
`TransactionTemplate`는 `PlatformTransactionManager`을 사용하여 트랜잭션 관리작업을 캡슐화한다<br>

1. `PlatformTransactionManager` 생성 후 `TransactionTemplate`에 주입
2. `TransactionTemplate`의 `execute()` 호출
3. 필요한 비즈니스 로직을 `TransactionCallback` 인터페이스를 통해 전달

```java
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionTemplateExample {

    private final TransactionTemplate transactionTemplate;

    public TransactionTemplateExample(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void performBusinessLogic() {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                // 비즈니스 로직
                updateDatabase();
            } catch (Exception ex) {
                // 언체크 예외는 자동 롤백, 체크 예외는 롤백되지 않으므로 롤백을 명시
                status.setRollbackOnly();
                throw new RuntimeException(ex);
            }
        });
    }
}
```
트랜잭션 템플릿을 사용하여 트랜잭션을 시작하고, 커밋, 롤백하는 코드가 모두 제거된다<br>
언체크 예외가 발생하면 롤백한다<br>

람다 표현식에서 체크 예외(Checked Exception)를 직접 던지는 것은 Java 언어 사양에 의해 제한된다<br>
(자바 표준 함수형 인터페이스(`Runnable`, `Consumer<T>`, `Supplier<T>`, `Function<T, R>`)는 `thrhows Exception`이 없는 시그니처로 정의되어 있음)<br>
일반적인 해결 방법은 직접 `try-catch`로 감싸 체크 예외를 언체크 예외로 바꿔서 던진다
```java
    transactionTemplate.executeWithoutResult(status -> {
        try {
            updateDatabase();
        } catch (CheckedException  ex) {
            throw new RuntimeException(ex);
        }
    });
```

[Back to main README](../README.md)
