package blogstudy.blog.config.oauth;

import blogstudy.blog.config.oauth.provider.FacebookUserInfo;
import blogstudy.blog.config.oauth.provider.GoogleUserInfo;
import blogstudy.blog.config.oauth.provider.NaverUserInfo;
import blogstudy.blog.config.oauth.provider.OAuth2UserInfo;
import blogstudy.blog.domain.User;
import blogstudy.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("userRequest = " + userRequest.getClientRegistration());
        // registrationId='google'로 구글에서 로그인한걸 확인할 수 있다.
        System.out.println("userRequest.getAccessToken().getTokenValue() = " + userRequest.getAccessToken().getTokenValue());
        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        // 회원가입 강제 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response")); // response 리턴값을 뽑아내 전달

        } else {
            System.out.println("구글, 페이스북, 네이버만 지원");
        }

        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId(); // sub
        String username = provider + "_" + providerId; // google_01234509127411234
        String password = passwordEncoder.encode("meaningless");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";


        // 이미 가입되어있는지 검증
        User userEntity = new User();
        if (userRepository.findByEmail2(email).isEmpty()) {
            System.out.println("최초 OAuth로그인");
            userEntity = User.builder()
                    .email(username)
                    .password(password)
                    .email2(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .createdDate(LocalDateTime.now())
                    .build();
            userEntity.setUser(userEntity);
            userRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        // 이렇게 리턴된 객체는 SecurityContext에 Authentication으로 들어가게된다!!!!
        return new User(userEntity, oAuth2User.getAttributes()); // Authentication은 UserDetails와 authorities를 가지고 저장됨
    }

}
