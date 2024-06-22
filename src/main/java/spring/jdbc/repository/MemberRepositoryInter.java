package spring.jdbc.repository;

import spring.jdbc.domain.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public interface MemberRepositoryInter {

    public Member save(Member member);

    public Member findById (String memberId);

    public void update (String memberId,int money);
}
