## JDBC
![img_1.png](img_1.png)
Java Database Connectivity<br>
수많은 데이터베이스에 접근하기 위한 데이터베이스에 종속적인 코드의 단점을 없애고<br> 표준 API를 통해 여러 데이터베이스에 접근하도록 한다

>jdbc 주요기능
- db 연결
- 쿼리 실행
- ResultSet 처리
- 트랜잭션 관리
- 메타데이터 접근

### DriverManager
JDBC API의 일부로 JDBC 드라이버를 관리하고 데이터베이스 연결을 설정하는 데 사용한다<br>
DriverManager를 통해 직접 데이터베이스에 연결한다<br>
- 드라이버 등록
```java
Class.forName("com.mysql.cj.jdbc.Driver");
```
- 데이터베이스 연결 설정
```java
Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "username", "password");
```
```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcExample {
    public static void main(String[] args) {
        // JDBC URL, 사용자명, 비밀번호를 설정
        String jdbcUrl = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "myusername";
        String password = "mypassword";

        try {
            // 데이터베이스 연결을 설정
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Statement 객체를 생성: sql쿼리 보내고 결과를 받는 역할
            Statement statement = connection.createStatement();

            // SQL 쿼리를 실행
            ResultSet resultSet = statement.executeQuery("SELECT * FROM mytable");

            // 결과를 처리
            while (resultSet.next()) {
                System.out.println("Column1: " + resultSet.getString("column1"));
                System.out.println("Column2: " + resultSet.getInt("column2"));
            }

            // 자원을 해제
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
- executeQuery<br>
  SELECT 쿼리를 실행하여 데이터베이스로부터 결과 집합(ResultSet)을 가져온다
```java
String sql = "SELECT * FROM mytable";
ResultSet resultSet = statement.executeQuery(sql);
```
- executeUpdate<br>
  INSERT, UPDATE, DELETE와 같은 데이터베이스를 수정하는 쿼리를 실행한다
```java
String sql = "UPDATE mytable SET column1 = 'value' WHERE column2 = 123";
int rowsAffected = statement.executeUpdate(sql);
```

jdbc 4.2 예제
```java
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "myusername";
        String password = "mypassword";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT id, name, birth_date FROM users WHERE birth_date = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setObject(1, LocalDate.of(1990, 1, 1));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        LocalDate birthDate = resultSet.getObject("birth_date", LocalDate.class);

                        System.out.println("ID: " + id + ", Name: " + name + ", Birth Date: " + birthDate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
```
DriverManager는 직접 커넥션을 생성하는 방법으로 사용할 때마다 연결 정보를 입력해야 한다<br>
따라서 반복적으로 사용하지 않고 한번만 간단히 사용할 때 주로 사용한다
```java
DriverManager.getConnection(URL, USERNAME, PASSWORD);
DriverManager.getConnection(URL, USERNAME, PASSWORD);
DriverManager.getConnection(URL, USERNAME, PASSWORD);
```
### JDBCUtils
데이터베이스 작업 후 예외를 발생시키지 않고 리소스를 안전하게 닫도록 해준다<br>
사용하게 되면 보일러 플레이트 코드를 줄인다<br>
디버깅/로깅 등 추가 확장시 유지보수가 필요하다 (유연하지 않음)<br>
결론은 자원 해제 패턴을 줄이고 싶을 때 사용한다
```java
import org.springframework.jdbc.support.JdbcUtils;

Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;
try {
    conn = dataSource.getConnection();
    stmt = conn.prepareStatement("SELECT * FROM my_table");
    rs = stmt.executeQuery();
    while (rs.next()) {
        // 데이터 처리 로직
    }
} catch (SQLException e) {
    // 예외 처리 로직
} finally {
    // 리소스 정리
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(conn);
}
```
[Back to main README](../README.md)
