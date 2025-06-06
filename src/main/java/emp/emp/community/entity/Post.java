package emp.emp.community.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import emp.emp.community.enums.HealthCategory;
import emp.emp.community.enums.PostType;
import emp.emp.member.entity.Member;
import emp.emp.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body_text", nullable = false)
    private String bodyText;

    @Column(name = "post_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PostType postType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "health_category", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private HealthCategory healthCategory;

    @Column(name = "image_url")
    private String imageUrl;
    // 좋아요는 수치 저장은 따로 안함
}
