package woopaca.jspcafe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import woopaca.jspcafe.error.UnauthorizedException;
import woopaca.jspcafe.mock.MockUserRepository;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.model.User;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.servlet.dto.request.LoginRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = new MockUserRepository();
        authService = new AuthService(userRepository);
    }

    @Nested
    class authenticate_메서드는 {

        @Nested
        class 유효한_로그인_요청 {

            @Test
            void 정상적으로_인증에_성공한다() {
                User user = new User("test", "test", "test");
                userRepository.save(user);
                LoginRequest loginRequest = new LoginRequest("test", "test");
                Authentication authentication = authService.authenticate(loginRequest);
                assertThat(authentication).isNotNull();
                assertThat(authentication.isPrincipal(user.getId())).isTrue();
            }
        }

        @Nested
        class 유효하지_않은_로그인_요청 {

            @Test
            void 존재하지_않는_사용자이면_예외가_발생한다() {
                LoginRequest loginRequest = new LoginRequest("test", "test");
                assertThatThrownBy(() -> authService.authenticate(loginRequest))
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage("[ERROR] 로그인 실패.");
            }

            @Test
            void 비밀번호가_일치하지_않으면_예외가_발생한다() {
                userRepository.save(new User("test", "test", "test"));
                LoginRequest loginRequest = new LoginRequest("test", "invalid");
                assertThatThrownBy(() -> authService.authenticate(loginRequest))
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage("[ERROR] 로그인 실패.");
            }
        }
    }
}