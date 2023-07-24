package com.diarymate.dame.member.repository;

import com.diarymate.dame.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Member findByUsername(String username);

  Member findByEmail(String email);

  boolean existsByEmail(String email);
}