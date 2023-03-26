package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString(of = {"id", "username", "age"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQuery(name = "Member.findByUsername",
        query = "select m from Member m where m.username=:username")
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        changeTeam(team);
    }

    public void changeTeam(Team team) {
        if (Objects.nonNull(this.team)) {
            this.team.setMembers(this.team.getMembers().stream().filter(m -> !m.getId().equals(this.id)).collect(Collectors.toList()));
        }
        if (team != null) {
            this.team = team;
            team.getMembers().add(this);
        }
    }

}
