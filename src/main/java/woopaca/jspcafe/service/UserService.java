package woopaca.jspcafe.service;

import woopaca.jspcafe.model.User;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.servlet.dto.MembersResponse;
import woopaca.jspcafe.servlet.dto.SignUpRequest;
import woopaca.jspcafe.servlet.dto.UserProfile;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void signUp(SignUpRequest signUpRequest) {
        validateSignUpRequest(signUpRequest);
        User user = new User(signUpRequest.username(), signUpRequest.nickname(), signUpRequest.password());
        userRepository.save(user);
    }

    private void validateSignUpRequest(SignUpRequest signUpRequest) {
        if (signUpRequest.username().isBlank()) {
            throw new IllegalArgumentException("[ERROR] 이메일은 비어있을 수 없습니다.");
        }
        if (signUpRequest.nickname().length() < 2) {
            throw new IllegalArgumentException("[ERROR] 닉네임은 비어있을 수 없습니다.");
        }
        if (signUpRequest.password().length() < 4) {
            throw new IllegalArgumentException("[ERROR] 비밀번호는 비어있을 수 없습니다.");
        }
    }

    public List<MembersResponse> getAllMembers() {
        return userRepository.findAll()
                .stream()
                .map(MembersResponse::from)
                .toList();
    }

    public UserProfile getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 사용자를 찾을 수 없습니다."));
        return UserProfile.from(user);
    }
}
