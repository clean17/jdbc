package spring.jdbc.domain;

import lombok.Data;

/**
 * @Data = getter, setter, toString, equals, hashCode, 모든 필드 초기화 기본 생성자
 * @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
 * 단점 - 과도한 메서드 생성, 모든 필드에 setter 생성,
 * 참고) @Value - 불변 객체 생성 : 모든 필드 private final + setter 금지
 */
@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
