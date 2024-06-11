package spring.jdbc.connection;

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
}
