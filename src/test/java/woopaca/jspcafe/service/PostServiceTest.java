package woopaca.jspcafe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import woopaca.jspcafe.error.BadRequestException;
import woopaca.jspcafe.error.ForbiddenException;
import woopaca.jspcafe.error.NotFoundException;
import woopaca.jspcafe.mock.MockPostRepository;
import woopaca.jspcafe.mock.MockUserRepository;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.model.Post;
import woopaca.jspcafe.model.User;
import woopaca.jspcafe.repository.PostRepository;
import woopaca.jspcafe.repository.ReplyRepository;
import woopaca.jspcafe.repository.UserRepository;
import woopaca.jspcafe.servlet.dto.request.PostEditRequest;
import woopaca.jspcafe.servlet.dto.request.WritePostRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostServiceTest {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private ReplyRepository replyRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = new MockPostRepository();
        userRepository = new MockUserRepository();
        postService = new PostService(postRepository, userRepository, replyRepository);
    }

    @Nested
    class writePost_메서드는 {

        @Nested
        class 유효한_게시글_작성_요청 {

            @Test
            void 정상적으로_게시글_작성에_성공한다() {
                WritePostRequest writePostRequest = new WritePostRequest("title", "content");
                User user = new User("test", "test", "test");
                userRepository.save(user);
                Authentication authentication = new Authentication(user, LocalDateTime.now());

                assertThatNoException()
                        .isThrownBy(() -> postService.writePost(writePostRequest, authentication));
                assertThat(postRepository.findAll()).hasSize(1);
            }
        }

        @Nested
        class 유효하지_않은_게시글_작성_요청 {

            @Test
            void 제목이_비어있으면_예외가_발생한다() {
                WritePostRequest writePostRequest = new WritePostRequest("", "content");
                User user = new User("test", "test", "test");
                userRepository.save(user);
                Authentication authentication = new Authentication(user, LocalDateTime.now());

                assertThatThrownBy(() -> postService.writePost(writePostRequest, authentication))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 제목과 내용은 필수 입력 사항입니다.");
            }

            @Test
            void 내용이_비어있으면_예외가_발생한다() {
                WritePostRequest writePostRequest = new WritePostRequest("title", "");
                User user = new User("test", "test", "test");
                userRepository.save(user);
                Authentication authentication = new Authentication(user, LocalDateTime.now());

                assertThatThrownBy(() -> postService.writePost(writePostRequest, authentication))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 제목과 내용은 필수 입력 사항입니다.");
            }

            @Test
            void 제목과_내용이_모두_비어있으면_예외가_발생한다() {
                WritePostRequest writePostRequest = new WritePostRequest("", "");
                User user = new User("test", "test", "test");
                userRepository.save(user);
                Authentication authentication = new Authentication(user, LocalDateTime.now());

                assertThatThrownBy(() -> postService.writePost(writePostRequest, authentication))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("[ERROR] 제목과 내용은 필수 입력 사항입니다.");
            }
        }
    }

    @Nested
    class getAllPosts_메서드는 {

        @Test
        void 모든_게시글_목록을_반환한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            postRepository.save(new Post("title1", "content1", user.getId()));
            postRepository.save(new Post("title2", "content2", user.getId()));

            assertThat(postService.getAllPosts()).isNotEmpty();
            assertThat(postService.getAllPosts()).hasSize(2);
            assertThat(postService.getAllPosts().get(0).title()).isEqualTo("title2");
        }

        @Test
        void 게시글이_없으면_빈_리스트를_반환한다() {
            assertThat(postService.getAllPosts()).isEmpty();
        }

        @Test
        void 게시글이_삭제되었으면_리스트에서_제외한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post1 = new Post("title1", "content1", user.getId());
            Post post2 = new Post("title2", "content2", user.getId());
            postRepository.save(post1);
            postRepository.save(post2);
            post1.softDelete();
            postRepository.save(post1);

            assertThat(postService.getAllPosts()).isNotEmpty();
            assertThat(postService.getAllPosts()).hasSize(1);
            assertThat(postService.getAllPosts().get(0).title()).isEqualTo("title2");
        }

        @Test
        void 작성자가_존재하지_않으면_예외가_발생한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", -1L);
            postRepository.save(post);

            assertThatThrownBy(() -> postService.getAllPosts())
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("[ERROR] 작성자를 찾을 수 없습니다.");
        }
    }

    @Nested
    class getPostDetails_메서드는 {

        @Test
        void 게시글_상세정보를_반환한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", user.getId());
            postRepository.save(post);

            assertThat(postService.getPostDetails(post.getId()).title()).isEqualTo("title");
            assertThat(postService.getPostDetails(post.getId()).content()).isEqualTo("content");
        }

        @Test
        void 게시글이_삭제되었으면_예외가_발생한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", user.getId());
            postRepository.save(post);
            post.softDelete();
            postRepository.save(post);

            assertThatThrownBy(() -> postService.getPostDetails(post.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 삭제된 게시글입니다.");
        }

        @Test
        void 게시글이_존재하지_않으면_예외가_발생한다() {
            assertThatThrownBy(() -> postService.getPostDetails(-1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("[ERROR] 게시글을 찾을 수 없습니다.");
        }

        @Test
        void 작성자가_존재하지_않으면_예외가_발생한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", -1L);
            postRepository.save(post);

            assertThatThrownBy(() -> postService.getPostDetails(post.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("[ERROR] 작성자를 찾을 수 없습니다.");
        }

        @Test
        void 호출되면_게시글_조회수가_증가한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", user.getId());
            postRepository.save(post);

            assertThat(post.getViewCount()).isEqualTo(0);
            postService.getPostDetails(post.getId());
            postService.getPostDetails(post.getId());
            assertThat(post.getViewCount()).isEqualTo(2);
        }
    }

    @Nested
    class getPostTitleContent_메서드는 {

        @Test
        void 게시글_제목과_내용을_반환한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", user.getId());
            postRepository.save(post);

            assertThat(postService.getPostTitleContent(post.getId()).title()).isEqualTo("title");
            assertThat(postService.getPostTitleContent(post.getId()).content()).isEqualTo("content");
        }

        @Test
        void 게시글이_존재하지_않으면_예외가_발생한다() {
            assertThatThrownBy(() -> postService.getPostTitleContent(-1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("[ERROR] 게시글을 찾을 수 없습니다.");
        }
    }

    @Nested
    class validateWriter_메서드는 {

        @Test
        void 게시글이_존재하지_않으면_예외가_발생한다() {
            assertThatThrownBy(() -> postService.validateWriter(-1L, new Authentication(new User("test", "test", "test"), LocalDateTime.now())))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("[ERROR] 존재하지 않는 게시글입니다.");
        }

        @Test
        void 게시글_작성자가_아니면_예외가_발생한다() {
            User writer = new User("test1", "test1", "test1");
            User other = new User("test2", "test2", "test2");
            userRepository.save(writer);
            userRepository.save(other);
            Post post = new Post("title", "content", writer.getId());
            postRepository.save(post);

            assertThatThrownBy(() -> postService.validateWriter(post.getId(), new Authentication(other, LocalDateTime.now())))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("[ERROR] 작성자만 수정•삭제할 수 있습니다.");
        }
    }

    @Nested
    class updatePost_메서드는 {

        @Test
        void 게시글_수정에_성공한다() {
            User user = new User("test", "test", "test");
            userRepository.save(user);
            Post post = new Post("title", "content", user.getId());
            postRepository.save(post);
            PostEditRequest postEditRequest = new PostEditRequest("new title", "new content");
            Authentication authentication = new Authentication(user, LocalDateTime.now());

            assertThatNoException()
                    .isThrownBy(() -> postService.updatePost(post.getId(), postEditRequest, authentication));
            assertThat(postRepository.findById(post.getId()).get().getTitle()).isEqualTo("new title");
            assertThat(postRepository.findById(post.getId()).get().getContent()).isEqualTo("new content");
        }

        @Test
        void 게시글_작성자가_아니면_예외가_발생한다() {
            User writer = new User("test1", "test1", "test1");
            User other = new User("test2", "test2", "test2");
            userRepository.save(writer);
            userRepository.save(other);
            Post post = new Post("title", "content", writer.getId());
            postRepository.save(post);
            PostEditRequest postEditRequest = new PostEditRequest("new title", "new content");
            Authentication authentication = new Authentication(other, LocalDateTime.now());

            assertThatThrownBy(() -> postService.updatePost(post.getId(), postEditRequest, authentication))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("[ERROR] 작성자만 수정•삭제할 수 있습니다.");
        }
    }
}
