package woopaca.jspcafe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import woopaca.jspcafe.error.BadRequestException;
import woopaca.jspcafe.error.ForbiddenException;
import woopaca.jspcafe.error.NotFoundException;
import woopaca.jspcafe.error.UnauthorizedException;
import woopaca.jspcafe.mock.MockUserRepository;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.model.User;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.servlet.dto.request.SignUpRequest;
import woopaca.jspcafe.servlet.dto.request.UpdateProfileRequest;
import woopaca.jspcafe.servlet.dto.response.MembersResponse;
import woopaca.jspcafe.servlet.dto.response.UserProfile;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new MockUserRepository();
        userService = new UserService(userRepository);
    }

    @Nested
    class signUp_메서드는 {

        @Nested
        class 유효한_회원가입_요청 {

            @Test
            void 정상적으로_회원가입에_성공한다() {
                SignUpRequest signUpRequest = new SignUpRequest("new", "new", "new");
                assertThatNoException()
                        .isThrownBy(() -> userService.signUp(signUpRequest));
                assertThat(userRepository.findAll()).hasSize(1);
            }
        }

        @Nested
        class 유효하지_않은_회원가입_요청 {

            @Test
            void 이메일이_비어있으면_예외가_발생한다() {
                SignUpRequest signUpRequest = new SignUpRequest("", "new", "new");
                assertThatThrownBy(() -> userService.signUp(signUpRequest))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 이메일은 비어있을 수 없습니다.");
            }

            @Test
            void 닉네임이_비어있으면_예외가_발생한다() {
                SignUpRequest signUpRequest = new SignUpRequest("new", "", "new");
                assertThatThrownBy(() -> userService.signUp(signUpRequest))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 닉네임은 비어있을 수 없습니다.");
            }

            @Test
            void 비밀번호가_비어있으면_예외가_발생한다() {
                SignUpRequest signUpRequest = new SignUpRequest("new", "new", "");
                assertThatThrownBy(() -> userService.signUp(signUpRequest))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 비밀번호는 비어있을 수 없습니다.");
            }

            @Test
            void 이미_사용중인_이메일이면_예외가_발생한다() {
                userRepository.save(new User("test", "test", "test"));

                SignUpRequest signUpRequest = new SignUpRequest("test", "new", "new");
                assertThatThrownBy(() -> userService.signUp(signUpRequest))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 이미 사용 중인 이메일입니다.");
            }

            @Test
            void 이미_사용중인_닉네임이면_예외가_발생한다() {
                userRepository.save(new User("test", "test", "test"));

                SignUpRequest signUpRequest = new SignUpRequest("new", "test", "new");
                assertThatThrownBy(() -> userService.signUp(signUpRequest))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 이미 사용 중인 닉네임입니다.");
            }
        }
    }

    @Nested
    class getAllMembers_메서드는 {

        @Test
        void 모든_회원을_조회한다() {
            userRepository.save(new User("test1", "test1", "test1"));
            userRepository.save(new User("test2", "test2", "test2"));
            userRepository.save(new User("test3", "test3", "test3"));

            List<MembersResponse> members = userService.getAllMembers();
            assertThat(members).isNotNull();
            assertThat(members).hasSize(3);
        }
    }

    @Nested
    class getUserProfile_메서드는 {

        @Test
        void 사용자_프로필을_조회한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);

            UserProfile userProfile = userService.getUserProfile(user.getId());
            assertThat(userProfile).isNotNull();
            assertThat(userProfile.nickname()).isEqualTo("test");
        }

        @Test
        void 사용자_프로필을_조회할_수_없는_경우_예외가_발생한다() {
            Long userId = -1L;
            assertThatThrownBy(() -> userService.getUserProfile(userId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("[ERROR] 사용자를 찾을 수 없습니다.");
        }
    }

    @Nested
    class updateUserProfile_메서드는 {

        @Test
        void 사용자_프로필을_수정한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("new", "test");
            userService.updateUserProfile(user.getId(), updateProfileRequest, new Authentication(user, LocalDateTime.now()));
        }

        @Test
        void 다른_사용자의_프로필을_수정하려는_경우_예외가_발생한다() {
            User user1 = new User("test1", "test1", "test1");
            User user2 = new User("test2", "test2", "test2");
            userRepository.save(user1);
            userRepository.save(user2);

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("new", "test2");
            Authentication authentication = new Authentication(user1, LocalDateTime.now());
            assertThatThrownBy(() -> userService.updateUserProfile(user2.getId(), updateProfileRequest, authentication))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("[ERROR] 다른 사용자의 프로필을 수정할 수 없습니다.");
        }

        @Test
        void 비밀번호가_일치하지_않으면_예외가_발생한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("new", "invalid");
            assertThatThrownBy(() -> userService.updateUserProfile(user.getId(), updateProfileRequest, new Authentication(user, LocalDateTime.now())))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("[ERROR] 비밀번호가 일치하지 않습니다.");
        }

        @Test
        void 닉네임이_중복되면_예외가_발생한다() {
            User user1 = new User("test1", "test1", "test1");
            User user2 = new User("test2", "test2", "test2");
            userRepository.save(user1);
            userRepository.save(user2);

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("test2", "test1");
            assertThatThrownBy(() -> userService.updateUserProfile(user1.getId(), updateProfileRequest, new Authentication(user1, LocalDateTime.now())))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 이미 사용 중인 닉네임입니다.");
        }

        @Test
        void 닉네임이_변경되지_않으면_수정하지_않는다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("test", "test");
            userService.updateUserProfile(user.getId(), updateProfileRequest, new Authentication(user, LocalDateTime.now()));
        }
    }
}
