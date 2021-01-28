package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete (Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        //  jpql
        //  객체를 대상으로 하는 쿼리
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        return result;
    }


    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count(){
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age){
        return em.createQuery("select m from Member m where m.username =:username and m.age >= :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username){
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    /**
     * 순수 JPA 페이징과 정렬
     * offset : 몇 번째부터 시작해서
     * limit : 몇 개를 가져와라
     */
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset) // 몇 번째 페이지부터 가져올거야
                .setMaxResults(limit) // 개수를 몇 개 가져올거야 (한페이지 크기)
                .getResultList();
    }

    /**
     * 보통 페이징 쿼리를 짜면
     * 현재 페이지가 전체 페이지중 몇번째 페이지인지 표시하니까
     * totalCount 필요함
     */
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    /**
     * 벌크성 수정쿼리
     */
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age+1 where m.age >= : age")
                .setParameter("age",age)
                .executeUpdate(); //이거 실행하면, 응답값으로 개수가 나옴
    }
}
