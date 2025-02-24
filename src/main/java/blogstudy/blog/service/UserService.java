package blogstudy.blog.service;

import blogstudy.blog.domain.User;
import blogstudy.blog.dto.AddUserRequest;
import blogstudy.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long save(AddUserRequest dto) {

        return userRepository.save(User.builder()
                .email2(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // 암호화
                .build())
                .getId();
    }
}
