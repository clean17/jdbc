package spring.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static spring.jdbc.connection.ConnectionConst.*;


@Slf4j
public class DBConnectionUtilTest {

    @Test
    void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
        // get connection=conn0: url=jdbc:h2:tcp://localhost/mem:test user=SA, class=class org.h2.jdbc.JdbcConnection

    }

    /**
     * DriverManager는 매번 커넥션을 연결하지만
     * DriverManager.getConnection(URL, USERNAME, PASSWORD);
     * DriverManager.getConnection(URL, USERNAME, PASSWORD);
     * DriverManager.getConnection(URL, USERNAME, PASSWORD);
     *
     * DataSource는 한번엔 연결하고 커넥션 풀을 사용한다
     * new DriverManagerDataSource(URL, USERNAME, PASSWORD);
     *
     * 설정을 한번만 하므로 설정을 분리할 수 있다 -> yml버전 별로 별도로 관리
     *
     * @throws SQLException
     */
    @Test
    void driverManagerDataSource() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
        // connection=conn0: url=jdbc:h2:tcp://localhost/mem:testdb user=SA, class=class org.h2.jdbc.JdbcConnection
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(URL);
        hikariDataSource.setUsername(USERNAME);
        hikariDataSource.setPassword(PASSWORD);
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setPoolName("MyHikariPool");

        useDataSource(hikariDataSource);
        Thread.sleep(1000); // 커넥션 생성 시간 대기, 멈추지 않으면 커넥션 풀 생성 도중에 종료되어 버린다
        // 풀이 10개가 생성되고, 커넥션 2개가 활성화 된다
        //00:30:19.028 [MyHikariPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - MyHikariPool - Added connection conn9: url=jdbc:h2:tcp://localhost/~/test user=SA
        //00:30:19.031 [MyHikariPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - MyHikariPool - Added connection conn10: url=jdbc:h2:tcp://localhost/~/test user=SA
        //00:30:19.031 [MyHikariPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - MyHikariPool - After adding stats (total=10, active=2, idle=8, waiting=0)
        //00:30:19.923 [main] DEBUG org.springframework.jdbc.datasource.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:h2:tcp://localhost/~/test]
        //00:30:19.925 [main] DEBUG org.springframework.jdbc.datasource.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:h2:tcp://localhost/~/test]
        //00:30:19.927 [main] INFO spring.jdbc.connection.DBConnectionUtilTest - connection=conn11: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class org.h2.jdbc.JdbcConnection
        //00:30:19.927 [main] INFO spring.jdbc.connection.DBConnectionUtilTest - connection=conn12: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class org.h2.jdbc.JdbcConnection

        //HikariPool
        //별도의 스레드에서 커넥션 풀에 커넥션을 채우는 이유<br>
        //시간이 조금 걸리므로 애플리케이션 실행시간에 영향을 주지 않기 위해
    }
}
