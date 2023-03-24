package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1", 10, new Team("teamA"));
        Member member2 = new Member("member2", 10, new Team("teamB"));
        memberRepository.save(member1);
        memberRepository.save(member2);
        //단건 조회 검증
        Member findMember1 =
                memberRepository.findById(member1.getId()).get();
        Member findMember2 =
                memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 20, new Team("teamA"));
        Member m2 = new Member("BBB", 10, new Team("teamA"));
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByUsernameAndAgeLessThen() {
        Member m1 = new Member("AAA", 20, new Team("teamA"));
        Member m2 = new Member("BBB", 10, new Team("teamA"));
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeLessThan("BBB", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("BBB");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 20, new Team("teamA"));
        Member m2 = new Member("BBB", 10, new Team("teamA"));
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 20);
        List<String> nameList = memberRepository.findUsernameList();
        for (String s : nameList) {
            System.out.println("s = " + s);
        }

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }


    @Test
    public void findMemberDto() {
        Member m1 = new Member("AAA", 20, new Team("teamA"));
        Member m2 = new Member("BBB", 10, new Team("teamA"));
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> result = memberRepository.findMemberDto();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 20, new Team("teamA"));
        Member m2 = new Member("BBB", 10, new Team("teamA"));
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void testReturnType() {
        Member m1 = new Member("AAA", 20, new Team("teamA"));
        Member m2 = new Member("BBB", 10, new Team("teamA"));
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findListByUsername("BBB"); // 없는 경우 비어있는 리스트 반환
        Member aaa = memberRepository.findMemberByUsername("BBB"); // 없는 경우 null로 반환. 따라서, 단건인 경우 Optional로 묶어주는게 좋음
        Optional<Member> optional = memberRepository.findOptionalByUsername("BBB");

        System.out.println("list = " + list);
        System.out.println("aaa = " + aaa);
        System.out.println("optional = " + optional);

    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10, new Team("teamA")));
        memberRepository.save(new Member("member1", 10, new Team("teamA")));
        memberRepository.save(new Member("member1", 10, new Team("teamA")));
        memberRepository.save(new Member("member1", 10, new Team("teamA")));
        memberRepository.save(new Member("member1", 10, new Team("teamA")));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName())); // dto로 변환(front-end로 보낼 시)

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
}