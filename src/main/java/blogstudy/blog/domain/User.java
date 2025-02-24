package blogstudy.blog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Table(name = "users")
@NoArgsConstructor // JPA는 기본 생성자 필요
@Entity
@Getter
@Setter
public class User implements UserDetails, OAuth2User {

    @Transient
    private User user; // 컴포지션

    // OAuth2 전용
    @Transient
    private Map<String, Object> attribute;

    // 일반 로그인 저장용 생성자
    public User(User user) {
        this.user = user;
    }

    // OAuth전용 생성자, 생성자로 attribute 저장, 후처리 return할 때 필요
    public User(User user, Map<String, Object> attribute) {
        this.user = user;
        this.attribute = attribute;
    }

    // PrincipalOauth2UserService에서 후처리에 사용할 생성자
    @Builder
    public User(String email, String password, String role, String provider, String providerId, String email2, LocalDateTime createdDate) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.email2 = email2;
        this.createdDate = createdDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = true, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "provider")
    private String provider;

    @Column(name = "providerId")
    private String providerId;

    @Column(name = "createdDate")
    private LocalDateTime createdDate;

    @Column(name = "email2")
    private String email2;

    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }

    /**
     * UserDetails 인터페이스 메소드
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return this.user.email2;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { // 계정이 만료되지 않았는지 확인하는 로직
        return true; // 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() { // 계정이 잠기지 않았는지 확인하는 로직
        return true; // 잠기지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() { // 패스워드가 만료되지 않았는지 확인하는 로직
        return true; // 만료되지 않았음
    }

    @Override
    public boolean isEnabled() { // 계정 사용이 가능한지 여부
        return true;
    }

    /**
     * OAuth2User 인터페이스 메소드
     */
    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    // OAuth를 통한 로그인 정보를 맵핑
    @Override
    public Map<String, Object> getAttributes() {
        return attribute;
    }

    @Override
    public String getName() { // id 리턴, 그러나 잘 안쓰므로 null 리턴
        return (String) attribute.get("sub");
    }
}
