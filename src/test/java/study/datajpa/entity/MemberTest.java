package study.datajpa.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;


@SpringBootTest
@Transactional
@Rollback
class MemberTest {

    @PersistenceContext
    EntityManager em;
    
    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        
        // 초기화
        em.flush();
        em.clear();
        
        // 확인
        List<Member> findMembers = em.createQuery("select m from Member m", Member.class).getResultList();

        assertThat(findMembers.size()).isEqualTo(4);
        assertThat(findMembers.stream().filter(m-> m.getTeam().getName().equals("teamA")).count()).isEqualTo(2);
        assertThat(findMembers.stream().filter(m-> m.getTeam().getName().equals("teamB")).count()).isEqualTo(2);

    }
}
