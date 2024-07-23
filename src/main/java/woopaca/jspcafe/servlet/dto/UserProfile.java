package woopaca.jspcafe.servlet.dto;

import woopaca.jspcafe.model.User;

public record UserProfile(String nickname, String email, String createdAt, String password) {

    public static UserProfile from(User user) {
        return new UserProfile(user.getNickname(), user.getUsername(), user.getCreatedAt().toString(), user.getPassword());
    }
}
