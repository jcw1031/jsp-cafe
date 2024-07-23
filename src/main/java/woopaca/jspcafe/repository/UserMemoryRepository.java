package woopaca.jspcafe.repository;

import woopaca.jspcafe.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserMemoryRepository implements UserRepository {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    /**
     * 사용자 저장 - 사용자의 UUID id를 생성 후 저장
     * @param user 사용자
     */
    @Override
    public void save(User user) {
        String uniqueId = user.generateUniqueId();
        if (users.containsKey(uniqueId)) {
            throw new IllegalArgumentException("[ERROR] duplicate key!: " + uniqueId);
        }
        users.put(uniqueId, user);
    }

    /**
     * 모든 사용자 조회
     * @return 사용자 목록
     */
    @Override
    public List<User> findAll() {
        return users.values()
                .stream()
                .toList();
    }

    /**
     * id로 사용자 조회
     * @param id 조회할 사용자의 id
     * @return 만약 존재하면 사용자, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<User> findById(String id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }
}
