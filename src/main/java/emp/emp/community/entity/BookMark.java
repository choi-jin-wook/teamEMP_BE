//package emp.emp.community.entity;
//
//
//import emp.emp.member.entity.Member;
//import jakarta.persistence.*;
//import org.springframework.security.core.parameters.P;
//
//
//@Entity
//public class BookMark {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "bookmark_id")
//    private int bookmarkId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id", nullable = false)
//    private Post post;
//
//}
