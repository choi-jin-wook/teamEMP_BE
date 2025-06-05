package emp.emp.community.service;

import emp.emp.community.dto.request.PostRequest;
import emp.emp.community.dto.response.PostResponse;
import emp.emp.community.entity.Comment;
import emp.emp.community.entity.Like;
import emp.emp.community.entity.Post;
import emp.emp.community.enums.HealthCategory;
import emp.emp.community.repository.CommentRepository;
import emp.emp.community.repository.LikeRepository;
import emp.emp.community.repository.PostRepository;
import emp.emp.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final S3Client s3Client;

    //    0. 초기화면
    public List<Post> getPosts() {
        return postRepository.findAll();
    }


//    1. 게시글 작성
    public PostResponse createPost(Member member, PostRequest postRequest, MultipartFile image) {
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setBodyText(postRequest.getBodyText());
        post.setPostType(postRequest.getPostType());
        post.setMember(member);
        post.setHealthCategory(postRequest.getHealthCategory());

        // 이미지 업로드 방식
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String filename = "post-images/" + UUID.randomUUID() + "-" + image.getOriginalFilename();

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket("team-emp")
                        .key(filename)
                        .contentType(image.getContentType())
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image.getBytes()));

                imageUrl = s3Client.utilities().getUrl(builder -> builder.bucket("team-emp").key(filename)).toExternalForm();

            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        post.setImageUrl(imageUrl);
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        Post savedPost = postRepository.save(post);
        PostResponse postResponse = new PostResponse();


        long likes = likeRepository.countByPost(savedPost);
        postResponse.setTitle(savedPost.getTitle()); // 제목 반환
        postResponse.setBodyText(savedPost.getBodyText()); // 내용
        postResponse.setPostType(savedPost.getPostType()); // 글 타입
        postResponse.setHealthCategory(savedPost.getHealthCategory()); // 카테고리 타입
        postResponse.setMember(savedPost.getMember()); // 멤버
        postResponse.setLikes(likes); // 좋아요 수
        postResponse.setImageUrl(savedPost.getImageUrl()); // 이미지

        boolean liked = false;
        postResponse.setIsLiked(liked);
        postResponse.setPostId(savedPost.getId());
        List<Comment> comments = commentRepository.findByPost(savedPost);
        postResponse.setComments(comments); // 댓글


        return postResponse;
    }

//    2. 게시글 조회
    public PostResponse getPostByIdAndMember(long postId, Member member) {
        PostResponse postResponse = new PostResponse();
        Optional<Post> post = postRepository.findById(postId);

        if (post.isPresent()) {
            long likes = likeRepository.countByPost(post.get());
            postResponse.setPostId(postId);
            postResponse.setTitle(post.get().getTitle()); // 제목 반환
            postResponse.setBodyText(post.get().getBodyText()); // 내용
            postResponse.setPostType(post.get().getPostType()); // 글 타입
            postResponse.setHealthCategory(post.get().getHealthCategory()); // 카테고리 타입
            postResponse.setMember(post.get().getMember()); // 멤버
            postResponse.setLikes(likes); // 좋아요 수
            postResponse.setImageUrl(post.get().getImageUrl()); // 이미지
            Optional<Like> isLiked = likeRepository.findByMemberAndPost(member, post.get());
            boolean liked;
            if (isLiked.isPresent()) {
                liked = true;
            } else {
                liked = false;
            }
            postResponse.setIsLiked(liked);
            List<Comment> comments = commentRepository.findByPost(post.get());
            postResponse.setComments(comments); // 댓글


        }
        return postResponse;
    }

    // 4-1. 게시물 수정 폼 불러오기
    public PostRequest getModifyForm(long postId) {
        PostRequest postInformation = new PostRequest();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        postInformation.setTitle(post.getTitle());
        postInformation.setBodyText(post.getBodyText());
        postInformation.setPostType(post.getPostType());
        postInformation.setHealthCategory(post.getHealthCategory());

        return postInformation;
    }

    // 4-2 게시글 수정
    public PostResponse modifyPost(long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        post.setTitle(postRequest.getTitle());
        post.setBodyText(postRequest.getBodyText());
        post.setPostType(postRequest.getPostType());
        post.setHealthCategory(postRequest.getHealthCategory());

        // 3. 저장
        Post updatedPost = postRepository.save(post);

        PostResponse postResponse = new PostResponse();
        postResponse.setPostId(updatedPost.getId());
        postResponse.setTitle(updatedPost.getTitle());
        postResponse.setBodyText(updatedPost.getBodyText());
        postResponse.setPostType(updatedPost.getPostType());
        postResponse.setMember(updatedPost.getMember());
        postResponse.setHealthCategory(updatedPost.getHealthCategory());
        List<Comment> comments = commentRepository.findByPost(updatedPost);
        postResponse.setComments(comments);
        postResponse.setImageUrl(updatedPost.getImageUrl());
        long likes = likeRepository.countByPost(updatedPost);
        postResponse.setLikes(likes);

        return postResponse;
    }




//    5. 게시글 삭제
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }


//    6. 카테고리별 글 조회
    public List<Post> getPostsByHealthCategory(HealthCategory healthCategory) {
        return postRepository.findByHealthCategory(healthCategory);
    }



}

