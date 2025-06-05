package emp.emp.community.ServiceTest;


import emp.emp.community.dto.request.CommentRequest;
import emp.emp.community.dto.request.PostRequest;
import emp.emp.community.dto.response.PostResponse;
import emp.emp.community.entity.Comment;
import emp.emp.community.entity.Post;
import emp.emp.community.enums.HealthCategory;
import emp.emp.community.enums.PostType;
import emp.emp.community.repository.PostRepository;
import emp.emp.community.service.CommentService;
import emp.emp.community.service.LikeService;
import emp.emp.community.service.PostService;
import emp.emp.member.entity.Member;
import emp.emp.member.enums.Role;
import emp.emp.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;




@SpringBootTest
@Transactional
public class ServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LikeService likeService;

    private Member testMember;

    private PostRequest validPostRequest;

    @Autowired
    private CommentService commentService;


    @BeforeEach
        void setUp() {
            testMember = Member.builder()
                    .provider("test")
                    .verifyId("test123")
                    .username("테스트사용자")
                    .email("test@example.com")
                    .password("password123")
                    .role(Role.ROLE_USER)
                    .gender("M")
                    .birthDay(LocalDate.of(1990, 1, 1))
                    .address("서울시 강남구")
                    .build();


            testMember = memberRepository.save(testMember);


            validPostRequest = new PostRequest();
            validPostRequest.setTitle("테스트 게시글 제목");
            validPostRequest.setBodyText("테스트 게시글 내용입니다.");
            validPostRequest.setPostType(PostType.QUESTION);
            validPostRequest.setHealthCategory(HealthCategory.NUTRITION);

        }


        @Test // 1. 게시글 작성
        void createPost () {
            // PostRequest 생성 - 모든 필수 필드 설정
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );


            try {
                PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);

                Optional<Post> post = postRepository.findById(postResponse.getPostId());
                assertThat(postResponse.getPostId()).isNotNull();
                assertThat(post.isPresent()).isTrue();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
        @Test // 2. 게시글 조회
        void getPostById () {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );



            PostResponse post = postService.createPost(testMember, validPostRequest, mockFile);
            Optional<Post> postEntity = postRepository.findById(post.getPostId());


            System.out.println(post.getTitle());
            System.out.println(post.getBodyText());
            System.out.println(post.getPostType());
            System.out.println(post.getHealthCategory());
            System.out.println(post.getMember());
            System.out.println(post.getComments());
            System.out.println(post.getLikes());
            System.out.println(post.getImageUrl());
            assertThat(post).isNotNull();
            assertThat(postEntity).isNotNull();
        }


        @Test // 3. 좋아요 누르기 (완료)
        void CreateAndDeleteLikePost () {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );



            PostResponse tempPost = postService.createPost(testMember, validPostRequest, mockFile);
            PostResponse postResponse = likeService.createOrDeleteLike(testMember, tempPost.getPostId());


            PostResponse post = postService.getPostByIdAndMember(postResponse.getPostId(), testMember);
            System.out.println(post.getTitle());
            System.out.println(post.getBodyText());
            System.out.println(post.getPostType());
            System.out.println(post.getHealthCategory());
            System.out.println(post.getMember());
            System.out.println(post.getComments());
            System.out.println(post.getLikes());
            System.out.println(post.getImageUrl());

            PostResponse post2 = likeService.createOrDeleteLike(testMember, tempPost.getPostId());



            System.out.println(post2.getTitle());
            System.out.println(post2.getBodyText());
            System.out.println(post2.getPostType());
            System.out.println(post2.getHealthCategory());
            System.out.println(post2.getMember());
            System.out.println(post2.getComments());
            System.out.println(post2.getLikes());
            System.out.println(post2.getImageUrl());

        }


        @Test
        void modifyPostForm () {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);

            Map<String, Object> modifyForm = postService.getModifyForm(postResponse.getPostId());

            assertThat(modifyForm).isNotNull();

        }

        @Test
        void realModifyPost () {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);

            Post post1 = postRepository.findById(postResponse.getPostId()).orElseThrow();

            System.out.println(post1.getId());
            System.out.println(post1.getTitle());
            System.out.println(post1.getBodyText());
            System.out.println(post1.getPostType());
            System.out.println(post1.getHealthCategory());
            System.out.println(post1.getMember());
            System.out.println(post1.getImageUrl());
            System.out.println(post1.getCreatedAt());
            System.out.println(post1.getUpdatedAt());

            Map<String, Object> modifyForm = postService.getModifyForm(postResponse.getPostId());

            PostRequest postRequest = (PostRequest) modifyForm.get("post");
            long postId = (long) modifyForm.get("postId");

            postRequest.setTitle("수정된 제목");
            postRequest.setBodyText("수정된 본문");
            postRequest.setPostType(PostType.INFORMATION);
            postRequest.setHealthCategory(HealthCategory.MENTAL_HEALTH);

            Post post2 = postRepository.findById(postId).orElseThrow(); // 한 번만 호출
            post2.setTitle(postRequest.getTitle());
            post2.setBodyText(postRequest.getBodyText());
            post2.setPostType(postRequest.getPostType());
            post2.setHealthCategory(postRequest.getHealthCategory());
            post2.setUpdatedAt(LocalDateTime.now()); // 현재 시간 설정

            postRepository.save(post2); // 저장

            System.out.println(" ");
            System.out.println(post2.getId());
            System.out.println(post2.getTitle());
            System.out.println(post2.getBodyText());
            System.out.println(post2.getPostType());
            System.out.println(post2.getHealthCategory());
            System.out.println(post2.getMember());
            System.out.println(post2.getImageUrl());
            System.out.println(post2.getCreatedAt());
            System.out.println(post2.getUpdatedAt());


            assertThat(postResponse.getPostId()).isEqualTo(postId);


        }






        @Test // 5. 게시글 삭제
        void deletePost () {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);


            postRepository.deleteById(postResponse.getPostId());

            Optional<Post> post = postRepository.findById(postResponse.getPostId());
            assertThat(post.isPresent()).isFalse();
        }


        @Test // 6. 카테코리별 글 조회 (조짐?)
        void getCategoryPost () {
            for (int i = 0; i < 3; i++) {
                validPostRequest = new PostRequest();
                validPostRequest.setTitle("테스트 게시글 제목");
                validPostRequest.setBodyText("테스트 게시글 내용입니다.");
                validPostRequest.setPostType(PostType.QUESTION);
                validPostRequest.setHealthCategory(HealthCategory.NUTRITION);
            }


            for (int i = 0; i < 3; i++) {
                validPostRequest = new PostRequest();
                validPostRequest.setTitle("테스트 게시글 제목");
                validPostRequest.setBodyText("테스트 게시글 내용입니다.");
                validPostRequest.setPostType(PostType.QUESTION);
                validPostRequest.setHealthCategory(HealthCategory.MENTAL_HEALTH);
            }

            for (int i = 0; i < 3; i++) {
                validPostRequest = new PostRequest();
                validPostRequest.setTitle("테스트 게시글 제목");
                validPostRequest.setBodyText("테스트 게시글 내용입니다.");
                validPostRequest.setPostType(PostType.QUESTION);
                validPostRequest.setHealthCategory(HealthCategory.GENERAL);
            }

            List<Post> nutritionCategory = postService.getPostsByHealthCategory(HealthCategory.NUTRITION);
            List<Post> mentalHealthCategory = postService.getPostsByHealthCategory(HealthCategory.MENTAL_HEALTH);
            List<Post> generalCategory = postService.getPostsByHealthCategory(HealthCategory.GENERAL);

            System.out.println(nutritionCategory);
            System.out.println(mentalHealthCategory);
            System.out.println(generalCategory);
        }



        @Test // 7. 댓글 작성
        void createComment() {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);
            CommentRequest commentRequest = new CommentRequest();
            commentRequest.setComment("댓글이용");
            Comment comment2 = commentService.registerComment(postResponse.getPostId(), testMember, commentRequest);



            PostResponse post = postService.getPostByIdAndMember(postResponse.getPostId(), testMember);
            System.out.println(post.getTitle());
            System.out.println(post.getBodyText());
            System.out.println(post.getPostType());
            System.out.println(post.getHealthCategory());
            System.out.println(post.getMember());
            System.out.println(post.getComments());
            System.out.println(post.getLikes());
            System.out.println(post.getImageUrl());

            assertThat(post.getComments()).isNotEmpty();
        }

        @Test
        void getUpdateCommentForm() {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);
            CommentRequest commentRequest = new CommentRequest();
            commentRequest.setComment("댓글이용");
            Comment comment = commentService.registerComment(postResponse.getPostId(), testMember, commentRequest);

            Map<String, Object> commentUpdateForm = commentService.getModifyForm(comment.getId());

            assertThat(((CommentRequest) commentUpdateForm.get("comment")).getComment()).isEqualTo("댓글이용");
            assertThat(commentUpdateForm.get("commentId")).isEqualTo(comment.getId());

        }

        @Test
        void realUpdateComment() {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);
            CommentRequest commentRequest = new CommentRequest();
            commentRequest.setComment("댓글이용");
            Comment comment = commentService.registerComment(postResponse.getPostId(), testMember, commentRequest);

            Map<String, Object> commentUpdateForm = commentService.getModifyForm(comment.getId());

            CommentRequest modifyCommentRequest = new CommentRequest();
            modifyCommentRequest.setComment("댓글수정");

            Comment modifiedComment = commentService.modifyComment((Long) commentUpdateForm.get("commentId"), modifyCommentRequest);
            assertThat(comment.getId()).isEqualTo(modifiedComment.getId());
            assertThat(comment.getCreatedAt()).isEqualTo(modifiedComment.getCreatedAt());
//            assertThat(comment.getUpdatedAt()).isNotEqualTo(modifiedComment.getUpdatedAt());
            assertThat(comment.getContent()).isNotEqualTo(commentUpdateForm.get("comment"));
        }





        @Test
        void deleteComment() {
            MultipartFile mockFile = new MockMultipartFile(
                    "file",                         // name
                    "test-image.jpg",               // original filename
                    "image/jpeg",                   // content type
                    "test-image-content".getBytes() // content
            );
            PostResponse postResponse = postService.createPost(testMember, validPostRequest, mockFile);
            CommentRequest commentRequest = new CommentRequest();
            commentRequest.setComment("댓글이용");
            Comment comment = commentService.registerComment(postResponse.getPostId(), testMember, commentRequest);
            System.out.println(comment);

            PostResponse post = postService.getPostByIdAndMember(postResponse.getPostId(), testMember);
            System.out.println(post.getTitle());
            System.out.println(post.getBodyText());
            System.out.println(post.getPostType());
            System.out.println(post.getHealthCategory());
            System.out.println(post.getMember());
            System.out.println(post.getComments());
            System.out.println(post.getLikes());
            System.out.println(post.getImageUrl());
            System.out.println(" ");


            String deleteComment = commentService.deleteComment(postResponse.getPostId());
            System.out.println(deleteComment);

            PostResponse post2 = postService.getPostByIdAndMember(postResponse.getPostId(), testMember);
            System.out.println(post2.getTitle());
            System.out.println(post2.getBodyText());
            System.out.println(post2.getPostType());
            System.out.println(post2.getHealthCategory());
            System.out.println(post2.getMember());
            System.out.println(post2.getComments());
            System.out.println(post2.getLikes());
            System.out.println(post2.getImageUrl());

        }

    }
