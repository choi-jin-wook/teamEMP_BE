package emp.emp.community.service;

import emp.emp.community.dto.response.PostResponse;
import emp.emp.community.entity.Comment;
import emp.emp.community.entity.Like;
import emp.emp.community.entity.Post;
import emp.emp.community.repository.CommentRepository;
import emp.emp.community.repository.LikeRepository;
import emp.emp.community.repository.PostRepository;
import emp.emp.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //    3. 좋아요 누르기
    public PostResponse createOrDeleteLike(Member member, Long postId) {
        Post post = postRepository.findById(postId).get();
        Optional<Like> like= likeRepository.findByMemberAndPost(member, post);

        if (like.isPresent()) { // 좋아요 테이블에 눌렀다는게 존재한다면?
            likeRepository.delete(like.get());// 좋아요 테이블에서 삭제
        } else {
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setMember(member);
            likeRepository.save(newLike); // 안눌렀다면 좋아요 누르기
        }

        PostResponse postResponse = new PostResponse();
        Optional<Post> post2 = postRepository.findById(postId);

        if (post2.isPresent()) {
            long likes = likeRepository.countByPost(post2.get());
            postResponse.setPostId(postId);
            postResponse.setTitle(post2.get().getTitle()); // 제목 반환
            postResponse.setBodyText(post2.get().getBodyText()); // 내용
            postResponse.setPostType(post2.get().getPostType()); // 글 타입
            postResponse.setHealthCategory(post2.get().getHealthCategory()); // 카테고리 타입
            postResponse.setMemberName(post2.get().getMember().getUsername()); // 멤버
            postResponse.setLikes(likes); // 좋아요 수
            postResponse.setImageUrl(post2.get().getImageUrl()); // 이미지
            Optional<Like> isLiked = likeRepository.findByMemberAndPost(member, post2.get());
            boolean liked;
            if (isLiked.isPresent()) {
                liked = true;
            } else {
                liked = false;
            }
            postResponse.setIsLiked(liked);
            List<Comment> comments = commentRepository.findByPostId(post2.get().getId());
            postResponse.setComments(comments); // 댓글


        }
        return postResponse;

    }
}
