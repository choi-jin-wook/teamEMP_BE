package emp.emp.community.controller;


import emp.emp.auth.custom.CustomUserDetails;
import emp.emp.community.dto.request.CommentRequest;
import emp.emp.community.dto.request.PostRequest;
import emp.emp.community.dto.response.PostResponse;
import emp.emp.community.entity.Comment;
import emp.emp.community.entity.Like;
import emp.emp.community.entity.Post;
import emp.emp.community.enums.HealthCategory;
import emp.emp.community.enums.PostType;
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
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;
    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;

    // 0. 초기화면 (완료)
    @GetMapping("/main")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }


    // 1. 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @RequestParam String title,
            @RequestParam String bodyText,
            @RequestParam PostType postType,
            @RequestParam HealthCategory healthCategory,
            @RequestParam(required = false) MultipartFile image) {
        Member member = securityUtil.getCurrentMember();
        PostResponse postResponse = postService.createPost(member, title, bodyText, postType, healthCategory, image);

        return ResponseEntity.ok(postResponse);
    }



    // 2. 게시글 조회 (완료)
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = securityUtil.getCurrentMember();
        PostResponse post = postService.getPostByIdAndMember(postId, member);
        return ResponseEntity.ok(post);
    }


// 3. 좋아요 누르기 (완료)
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostResponse> createOrDeleteLike(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = securityUtil.getCurrentMember();
        PostResponse postResponse = likeService.createOrDeleteLike(member, postId);
        return ResponseEntity.ok(postResponse);
    }


    //    4-1 게시글 수정 폼 불러오기
    @GetMapping("/posts/{postId}/edit")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable long postId) {
        Map<String, Object> response = postService.getModifyForm(postId);
        return ResponseEntity.ok(response);
    }


// 4. 게시글 수정
    @PostMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @RequestParam String title,
            @RequestParam String bodyText,
            @RequestParam PostType postType,
            @RequestParam HealthCategory healthCategory,
            @RequestParam(required = false) MultipartFile image,
            @PathVariable long postId) {
        PostResponse postResponse = postService.modifyPost(postId, title, bodyText, postType, healthCategory, image);
        return ResponseEntity.ok(postResponse);
    }





// 5. 게시글 삭제
// 빈환값 제외 구현 완료
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


// 6. 카테코리별 글 조회 (완료)
    @GetMapping("/categories/{healthCategory}")
    public ResponseEntity<List<Post>> getPost(@PathVariable String healthCategory) {
        try {
            HealthCategory category = HealthCategory.valueOf(healthCategory.toUpperCase());
            List<Post> post = postService.getPostsByHealthCategory(category);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 잘못된 카테고리명
        }
    }


    // 7. 댓글 달기
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<PostResponse> registerComment(@PathVariable long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestBody CommentRequest commentRequest) {
        Member member = securityUtil.getCurrentMember();
        PostResponse postResponse = commentService.registerComment(postId, member, commentRequest);

        return ResponseEntity.ok(postResponse);
    }



    // 8 - 1. 댓글 수정 폼 불러오가
    @GetMapping("/posts/{postId}/comments/{commentId}/edit")
    public ResponseEntity<Map<String, Object>> getPatchCommentForm(@PathVariable long commentId) {
        Map<String, Object> commentUpdateForm = commentService.getModifyForm(commentId);
        return ResponseEntity.ok(commentUpdateForm);
    }

    // 8 - 2. 댓글 수정 구현
    @PostMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<PostResponse> patchComment(@PathVariable long commentId, @PathVariable long postId,@RequestBody CommentRequest commentRequest) {
        PostResponse modifyCommentPostResponse = commentService.modifyComment(commentId, postId ,commentRequest);
        return ResponseEntity.ok(modifyCommentPostResponse);
    }

    // 9. 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        String message = commentService.deleteComment(commentId);
        return ResponseEntity.ok(message);
    }


}
