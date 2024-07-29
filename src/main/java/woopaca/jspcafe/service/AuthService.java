package woopaca.jspcafe.service;

import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.model.User;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.servlet.dto.request.LoginRequest;

import java.time.LocalDateTime;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Authentication authenticate(LoginRequest loginRequest) {
        String username = loginRequest.username();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 로그인 실패."));
        String password = loginRequest.password();
        if (!user.matchPassword(password)) {
            throw new IllegalArgumentException("[ERROR] 로그인 실패.");
        }

        return new Authentication(user, LocalDateTime.now());
    }
}
