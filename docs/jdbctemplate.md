## JDBCTemplate
JDBCTemplate를 이용해서 기존의 보일러 플레이트 코드를 제거하고 예외처리를 단순화한다<br>
- 기존 코드
```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.support.JdbcUtils;

public class OldJdbcExample {
    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    public void saveData(String data) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "INSERT INTO my_table (data) VALUES (?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, data);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // 예외 처리
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(conn);
        }
    }
}
```
- jdbcTemplate 변환<br>
커넥션은 내부적으로 관리된다<br>
예외는 런타임 예외인 `DataAccessException`로 변환된다<br>
```java
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class NewJdbcRepository implements MemberRepository {
    private JdbcTemplate jdbcTemplate;

    public NewJdbcRepository(DataSource dataSource) {        
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";
        jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    @Override
    public Member findById (String memberId) {
        String sql = "select * from member where member_id = ?";
        return jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

    @Override
    public void update (String memberId,int money) {
        String sql = "update member set money=? where member_id=?";
        jdbcTemplate.update(sql, money, memberId);
    }

    @Override
    public void batchUpdate(List<Member> list) {
        String sql = "update member set money=? where member_id=?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Member request = list.get(i);
                ps.setInt(1, request.getMoney());
                ps.setString(2, request.getMemberId());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }
}
```
`jdbcTemplate`에서 커넥션 연결, 예외 처리등을 모두 해결해준다<br>
데이터 소스는 스프링이 HikariCP에서 가져와 주입해준다<br>
개발자는 `DataAccessException`를 예외 처리기로 처리하면 된다<br>

[Back to main README](../README.md)