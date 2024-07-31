package woopaca.jspcafe.service;

import woopaca.jspcafe.error.BadRequestException;
import woopaca.jspcafe.error.NotFoundException;
import woopaca.jspcafe.model.Authentication;
import woopaca.jspcafe.model.Post;
import woopaca.jspcafe.model.Reply;
import woopaca.jspcafe.repository.PostRepository;
import woopaca.jspcafe.repository.ReplyRepository;
import woopaca.jspcafe.servlet.dto.request.WriteReplyRequest;

import java.util.Objects;

public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;

    public ReplyService(ReplyRepository replyRepository, PostRepository postRepository) {
        this.replyRepository = replyRepository;
        this.postRepository = postRepository;
    }

    public void writeReply(WriteReplyRequest writeReplyRequest, Authentication authentication) {
        Long postId = writeReplyRequest.postId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 게시글을 찾을 수 없습니다."));
        if (post.isDeleted()) {
            throw new BadRequestException("[ERROR] 삭제된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        validateReplyContent(writeReplyRequest.content());
        Reply reply = new Reply(writeReplyRequest.content(), authentication.principal().getId(), postId);
        replyRepository.save(reply);
    }

    private void validateReplyContent(String content) {
        if (Objects.isNull(content) || content.isBlank()) {
            throw new BadRequestException("[ERROR] 댓글 내용을 입력해주세요.");
        }

        if (content.length() > 200) {
            throw new BadRequestException("[ERROR] 댓글 내용은 200자 이하여야 합니다.");
        }
    }
}
