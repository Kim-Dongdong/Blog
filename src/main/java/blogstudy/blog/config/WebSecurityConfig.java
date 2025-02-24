package blogstudy.blog.config;

import blogstudy.blog.config.oauth.PrincipalOauth2UserService;
import blogstudy.blog.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;


//    @Bean
//    public WebSecurityCustomizer configure() { // 비활성화 목록
//        return web -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/static/**");
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        AuthenticationManager authenticationManager = builder.build(); // build는 최초 한번만
//        AuthenticationManager authenticationManager1 = builder.getObject(); // 이후는 getObject로 가져온다.


        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console().toString(), "/static/**", "/login", "/signup", "/user").permitAll()
                        .requestMatchers("/user", "/manager", "/admin").authenticated()
                        .requestMatchers("/manager").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .authenticationProvider(authenticationProvider)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .defaultSuccessUrl("/"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
                .csrf(csrf -> csrf
                        .disable())
                .cors(cors-> cors
                        .disable())
                .oauth2Login(login -> login
                        .loginPage("/loginForm")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(principalOauth2UserService)))
                // 구글 로그인이 완료된 뒤에 후처리 필요.
                // 1. 코드받기(인증), 2.엑세스토근(권한),
                // 3. 사용자 프로필 정보를 가져오고
                // 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
                // 4-2.(이메일, 전화번호, 이름, 아이디) 쇼핑몰 -> (집주소) 백화점몰 -> (vip등급, 일반등급) => 추가적인 정보 제공 필요가능
                // 구글 로그인은 엑서스토큰+사용자 프로필정보를 리턴해준다.
                .build();
    }

}
