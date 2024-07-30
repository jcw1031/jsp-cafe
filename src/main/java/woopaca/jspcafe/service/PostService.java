package woopaca.jspcafe.service;

import woopaca.jspcafe.error.BadRequestException;
import woopaca.jspcafe.error.ForbiddenException;
import woopaca.jspcafe.error.NotFoundException;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.model.Post;
import woopaca.jspcafe.model.User;
import woopaca.jspcafe.repository.PostRepository;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.servlet.dto.request.PostEditRequest;
import woopaca.jspcafe.servlet.dto.request.WritePostRequest;
import woopaca.jspcafe.servlet.dto.response.PageInfo;
import woopaca.jspcafe.servlet.dto.response.PostDetailsResponse;
import woopaca.jspcafe.servlet.dto.response.PostEditResponse;
import woopaca.jspcafe.servlet.dto.response.PostsResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void writePost(WritePostRequest writePostRequest, Authentication authentication) {
        User user = authentication.principal();
        validatePostTitleAndContent(writePostRequest.title(), writePostRequest.content());
        Post post = new Post(writePostRequest.title(), writePostRequest.content(), user.getId());
        postRepository.save(post);
    }

    public List<PostsResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Post::getWrittenAt).reversed())
                .map(post -> {
                    User user = userRepository.findById(post.getWriterId())
                            .orElseThrow(() -> new NotFoundException("[ERROR] 작성자를 찾을 수 없습니다."));
                    return PostsResponse.of(post, user);
                })
                .toList();
    }

    public PostDetailsResponse getPostDetails(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 게시글을 찾을 수 없습니다."));
        updateViewCount(post);
        PageInfo pageInfo = getPageInfo(post);
        User user = userRepository.findById(post.getWriterId())
                .orElseThrow(() -> new NotFoundException("[ERROR] 작성자를 찾을 수 없습니다."));
        return PostDetailsResponse.of(post, pageInfo, user);
    }

    private void updateViewCount(Post post) {
        post.increaseViewCount();
        postRepository.save(post);
    }

    private PageInfo getPageInfo(Post post) {
        List<Post> posts = postRepository.findAll();
        posts.sort(Comparator.comparing(Post::getWrittenAt).reversed());
        int postsSize = posts.size();
        int postIndex = posts.indexOf(post);
        boolean hasNext = postIndex < postsSize - 1;
        boolean hasPrevious = postIndex > 0;
        Long previousPostId = hasPrevious ? posts.get(postIndex - 1).getId() : null;
        Long nextPostId = hasNext ? posts.get(postIndex + 1).getId() : null;
        return new PageInfo(hasNext, hasPrevious, nextPostId, previousPostId);
    }

    public PostEditResponse getPostTitleContent(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 게시글을 찾을 수 없습니다."));
        return new PostEditResponse(post.getId(), post.getTitle(), post.getContent());
    }

    public void validateWriter(Long postId, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 존재하지 않는 게시글입니다."));
        if (!authentication.isPrincipal(post.getWriterId())) {
            throw new ForbiddenException("[ERROR] 작성자만 수정할 수 있습니다.");
        }
    }

    public void updatePost(Long postId, PostEditRequest postEditRequest, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 존재하지 않는 게시글입니다."));
        if (!authentication.isPrincipal(post.getWriterId())) {
            throw new ForbiddenException("[ERROR] 작성자만 수정할 수 있습니다.");
        }

        validatePostTitleAndContent(postEditRequest.title(), postEditRequest.content());
        post.update(postEditRequest.title(), postEditRequest.content());
        postRepository.save(post);
    }

    private void validatePostTitleAndContent(String title, String content) {
        if (Objects.isNull(title) || Objects.isNull(content) || title.isBlank() || content.isBlank()) {
            throw new BadRequestException("[ERROR] 제목과 내용은 필수 입력 사항입니다.");
        }

        if (title.length() < 2 || content.length() < 2) {
            throw new BadRequestException("[ERROR] 제목과 내용은 2자 이상 입력해야 합니다.");
        }
    }
}
