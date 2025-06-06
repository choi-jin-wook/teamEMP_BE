package emp.emp.community.service;

import emp.emp.community.dto.request.CommentRequest;
import emp.emp.community.dto.response.PostResponse;
import emp.emp.community.entity.Comment;
import emp.emp.community.entity.Post;
import emp.emp.community.repository.CommentRepository;
import emp.emp.community.repository.LikeRepository;
import emp.emp.community.repository.PostRepository;
import emp.emp.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;



    public PostResponse registerComment(long postId, Member member, CommentRequest commentRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다. ID: " + postId));

        Comment comment = new Comment();
        comment.setPostId(post.getId());
        comment.setMemberId(member.getId());
        comment.setContent(commentRequest.getComment());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        Post addCommentPost = postRepository.findById(postId).get();
        PostResponse postResponse = new PostResponse();
        postResponse.setPostId(addCommentPost.getId());
        postResponse.setMemberName(addCommentPost.getMember().getUsername());
        postResponse.setTitle(addCommentPost.getTitle());
        postResponse.setBodyText(addCommentPost.getBodyText());
        postResponse.setImageUrl(addCommentPost.getImageUrl());
        postResponse.setLikes(likeRepository.countByPost(addCommentPost));
        postResponse.setComments(commentRepository.findByPostId(addCommentPost.getId()));
        postResponse.setIsLiked(!likeRepository.findByMemberAndPost(member, addCommentPost).isEmpty());
        postResponse.setPostType(addCommentPost.getPostType());
        postResponse.setHealthCategory(addCommentPost.getHealthCategory());





        return postResponse;


    }

    public String deleteComment(long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            commentRepository.delete(comment.get());
            return "삭제완료";
        } else {
            return "삭제할 댓글이 없습니다";
        }

    }

    public Map<String, Object> getModifyForm(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setComment(comment.getContent());

        Map<String, Object> commentUpdateForm = new HashMap<>();
        commentUpdateForm.put("commentId", comment.getId()); // 파라미터가 아닌 실제 Comment 객체의 id 사용
        commentUpdateForm.put("comment", commentRequest);

        return commentUpdateForm;
    }

    public PostResponse modifyComment(long commentId, long postId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"));
        comment.setContent(commentRequest.getComment());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);


        Post modifyCommentPost = postRepository.findById(postId).get();
        PostResponse postResponse = new PostResponse();
        postResponse.setPostId(modifyCommentPost.getId());
        postResponse.setMemberName(modifyCommentPost.getMember().getUsername());
        postResponse.setTitle(modifyCommentPost.getTitle());
        postResponse.setBodyText(modifyCommentPost.getBodyText());
        postResponse.setImageUrl(modifyCommentPost.getImageUrl());
        postResponse.setComments(commentRepository.findByPostId(modifyCommentPost.getId()));
        postResponse.setLikes(likeRepository.countByPost(modifyCommentPost));
        postResponse.setIsLiked(likeRepository.findByMemberAndPost(modifyCommentPost.getMember(), modifyCommentPost).isEmpty());

        postResponse.setPostType(modifyCommentPost.getPostType());
        postResponse.setHealthCategory(modifyCommentPost.getHealthCategory());

        return postResponse;
    }
//    addComment(Long postId, CommentRequest dto, Member member)
//    getCommentsByPost(Long postId)
//    updateComment(Long commentId, CommentRequest dto, Member member)
//    deleteComment(Long commentId, Member member)
}
