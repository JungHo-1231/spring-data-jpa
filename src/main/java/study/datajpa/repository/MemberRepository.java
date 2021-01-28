package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username =:username and m.age =:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional


    /**
     * 스프링 데이터 JPA 사용한 페이징 처리
     * 반환타입을 Page로 받고
     * 파라미터로 Pageable 넘기면 됨 (Pageable 인터페이스 구현체인 PageRequest를 많이 사용)
     * 반환타입에 따라서 totalCount쿼리를 날릴지 안날릴지 결정이 됨
     * totalCount쿼리는 어찌됐든 DB데이터 전체를 카운트 하는 것이기 때문에
     * 데이터 많아질 수록 최적화가 필요함,
     * 특히 left outer join한 경우 데이터 수는 조인하기 전과 같으므로 count쿼리까지 조인할 필요없음
     * 따라서 count쿼리 따로 분리하는 방법 제공함
     */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 벌크성 수정쿼리
     * 벌크성 수정쿼리 수행 후에는 반드시 영속성 컨텍스트 clear해줘야함
     */
    @Modifying(clearAutomatically = true) //이게 있어야 executeUpdate실행 (아니면 getResultList 이런거 실행해버림)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

}
