package emp.emp.community.dto.response;

import emp.emp.community.entity.Comment;
import emp.emp.community.enums.HealthCategory;
import emp.emp.community.enums.PostType;
import emp.emp.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PostResponse {
    private long postId;
    private String title;
    private String bodyText;
    private PostType postType;
    private Member member;
    private HealthCategory healthCategory;
    private List<Comment> comments;
    private long likes;
    private Boolean isLiked;
    private String imageUrl;
}
