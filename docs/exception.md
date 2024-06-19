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