package com.passbee.user;

import com.passbee.common.BaseTimeEntity;
import com.passbee.review.Review;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "users")
public class Users extends BaseTimeEntity implements UserDetails { // UserDetails 구현 추가

    // --- 기존 필드들은 그대로 유지됩니다 ---
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 120) // BCrypt로 암호화된 비밀번호는 길어지므로 넉넉하게 설정
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 120, unique = true)
    private String email;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();


    // --- ↓↓↓ Spring Security가 필요로 하는 UserDetails 관련 메소드들 추가 ↓↓↓ ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 이 사용자가 가진 권한(Role) 목록을 반환합니다.
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // Spring Security가 '사용자 이름'으로 인식할 필드를 지정합니다.
        // 우리는 이메일을 ID로 사용하므로 email을 반환합니다.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았는지 (true: 만료 안됨)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았는지 (true: 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호가 만료되지 않았는지 (true: 만료 안됨)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화 상태인지 (true: 활성화됨)
    }
}