## ORM

![img_3.png](img_3.png)
객체를 RDBMS 테이블과 매핑시켜주는 기술<br>
ORM이 개발자 대신 동적으로 sql을 만들어 준다<br>
대표적인 예 - `JPA`, `Hibernate`

### Hibernate
객체 관계 매핑(ORM) 프레임워크
```java
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateExample {
    public static void main(String[] args) {
        // 순수 Hibernate 방식으로 src/main/resources/hibernate.cfg.xml 파일의 DB 설정정보를 읽어온다; 
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john.doe@example.com");

            // Hibernate 가 User 엔티티의 정보를 보고 동적으로 삽입 쿼리를 생성한다
            session.save(user);
            session.getTransaction().commit();
        }
    }
}

// User.java
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Table(name = "users")
public class User {    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    // getters and setters
}
```

### JPA
JPA는 자바 표준 ORM API/기술 명세<br>
대표적인 구현체가 `Hibernate`<br>
스프링에서 JPA를 통해 DB에 접근한다면 `Hibernate`가 구현체로 사용된다<br>
```java
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAExample {
    public static void main(String[] args) {
        // META-INF/persistence.xml 파일의 my-persistence-unit 이름의 persistence-unit을 찾아 DB 설정정보을 읽어온다
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        
        User user = new User();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");

        em.persist(user);
        em.getTransaction().commit();

        em.close();
        emf.close();
    }
}

// User.java
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
// @Table 생략하면 자동으로 클래스 이름으로 매핑한다; @Column 도 마찬가지
public class User {
    @Id
    private Long id;
    private String name;
    private String email;

    // getters and setters
}
```

### Spring Data JPA

스프링이 제공하는 JPA를 기반으로 하여, JPA를 더 쉽게 쓰도록 데이터 접근 계층의 구현을 추상화/자동화한 프레임워크(라이브러리)<br>
`@Repository` 인터페이스를 정의하는 것만으로 CRUD 작업을 쉽게 구현<br>
별도의 xml/설정파일 없이 `yml`이나 `properties`파일에서 DB정보를 알아서 읽어온다<br>
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataJpaExample implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaExample.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        userRepository.save(user);
        
        userRepository.findAll().forEach(System.out::println);
    }
}

// UserRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository를 구현하면 SimpleJpaRepository에서 @Repository로 스프링 컨테이너가 관리하는 빈이 된다
public interface UserRepository extends JpaRepository<User, Long> {
}

// User.java
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private Long id;
    private String name;
    private String email;

    // getters and setters
}
```
```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: user
    password: pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

[Back to main README](../README.md)
