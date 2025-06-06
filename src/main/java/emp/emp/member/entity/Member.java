package emp.emp.member.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import emp.emp.family.entity.Family;
import emp.emp.health.entity.Health;
import emp.emp.health.entity.HealthComment;
import emp.emp.member.enums.Role;
import emp.emp.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String provider;

	private String verifyId;

	@Setter
	private String username;

	private String email;

	private String password;

	@Enumerated(value = EnumType.STRING)
	@Setter
	private Role role;

	@Setter
	private String gender;

	@Setter

	private LocalDate birthDay;

	@Setter
	private String address;

	@Setter
	private boolean bpPublic = false;
	@Setter
	private boolean bsPublic = false;
	@Setter
	private boolean slPublic = false;
	@Setter
	private boolean wePublic = false;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "family_id")
	private Family family;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Health> healthRecords = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HealthComment> healthComments = new ArrayList<>();

	@Builder
	public Member(String provider, String verifyId, String username, String email, String password, Role role,
		String gender, LocalDate birthDay, String address) {
		this.provider = provider;
		this.verifyId = verifyId;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.gender = gender;
		this.birthDay = birthDay;
		this.address = address;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	/**
	 * 생년월일 기준으로 만나이 계산
	 *
	 * @return 만나이
	 */

	public int getAge() {
		return Period.between(this.birthDay, LocalDate.now()).getYears();
	}

}