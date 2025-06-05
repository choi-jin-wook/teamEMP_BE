package emp.emp.community.controller;


import emp.emp.auth.custom.CustomUserDetails;
import emp.emp.community.dto.request.CommentRequest;
import emp.emp.community.dto.request.PostRequest;
import emp.emp.community.dto.response.PostResponse;
import emp.emp.community.entity.Comment;
import emp.emp.community.entity.Like;
import emp.emp.community.entity.Post;
import emp.emp.community.enums.HealthCategory;
import emp.emp.community.repository.CommentRepository;
import emp.emp.community.service.CommentService;
import emp.emp.community.service.LikeService;
import emp.emp.community.service.PostService;
import emp.emp.member.entity.Member;
import emp.emp.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommentRepository commentRepository;
    private SecurityUtil securityUtil;
    private PostService postService;
    private LikeService likeService;
    private CommentService commentService;

    // 0. 초기화면 (완료)
    @GetMapping("/community")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }


    // 1. 게시글 작성
    @PostMapping("community/createPost")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails, MultipartFile image) {
        Member member = securityUtil.getCurrentMember();
        PostResponse postResponse = postService.createPost(member, postRequest, image);

        return ResponseEntity.ok(postResponse);
    }



    // 2. 게시글 조회 (완료)
    @GetMapping("/community/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = securityUtil.getCurrentMember();
        PostResponse post = postService.getPostByIdAndMember(postId, member);
        return ResponseEntity.ok(post);
    }


// 3. 좋아요 누르기 (완료)
    @PostMapping("/community/{postId}/like")
    public ResponseEntity<PostResponse> createOrDeleteLike(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = securityUtil.getCurrentMember();
        PostResponse postResponse = likeService.createOrDeleteLike(member, postId);
        return ResponseEntity.ok(postResponse);
    }


    //    4-1 게시글 수정 폼 불러오기
    @GetMapping("/community/update/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable long postId) {
        Map<String, Object> response = postService.getModifyForm(postId);
        return ResponseEntity.ok(response);
    }


// 4. 게시글 수정
    @PostMapping("/community/update/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable long postId, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.modifyPost(postId, postRequest);
        return ResponseEntity.ok(postResponse);
    }





// 5. 게시글 삭제
// 빈환값 제외 구현 완료
    @DeleteMapping("/community/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


// 6. 카테코리별 글 조회 (완료)
    @GetMapping("/community/{healthCategory}")
    public ResponseEntity<List<Post>> getPost(@PathVariable HealthCategory healthCategory) {
        List<Post> post = postService.getPostsByHealthCategory(healthCategory);
        return ResponseEntity.ok(post);
    }


    // 7. 댓글 달기
    @PostMapping("/community/{postId}/comments")
    public ResponseEntity<Comment> registerComment(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestBody CommentRequest commentRequest) {
        Member member = securityUtil.getCurrentMember();
        Comment comment = commentService.registerComment(postId, member, commentRequest);

        return ResponseEntity.ok(comment);
    }



    // 8 - 1. 댓글 수정 폼 불러오가
    @GetMapping("community/{postId}/comment/modify/{commentId}")
    public ResponseEntity<Map<String, Object>> getPatchCommentForm(long commentId) {
        Map<String, Object> commentUpdateForm = commentService.getModifyForm(commentId);
        return ResponseEntity.ok(commentUpdateForm);
    }

    // 8 - 2. 댓글 수정 구현
    @PatchMapping("community/{postId}/comment/modify/{commentId}")
    public ResponseEntity<Comment> patchComment(@PathVariable long commentId, CommentRequest commentRequest) {
        Comment modifyComment = commentService.modifyComment(commentId ,commentRequest);
        return ResponseEntity.ok(modifyComment);

    }

    // 9. 댓글 삭제
    @DeleteMapping("/community/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        String message = commentService.deleteComment(commentId);
        return ResponseEntity.ok(message);
    }


}
