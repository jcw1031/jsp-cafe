package woopaca.jspcafe.repository;

import woopaca.jspcafe.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    List<User> findAll();

    Optional<User> findById(String id);
}
