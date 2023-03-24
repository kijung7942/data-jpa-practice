package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member memberA = new Member("memberA");
        Member savedMember = memberRepository.save(memberA);

        Member member = memberRepository.findById(savedMember.getId()).get();

        assertThat(memberA.getId()).isEqualTo(member.getId());
        assertThat(memberA.getUsername()).isEqualTo(member.getUsername());
        assertThat(memberA).isEqualTo(member);

    }
}