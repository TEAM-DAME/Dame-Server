package com.diarymate.dame.member.service;

import com.diarymate.dame.member.dto.request.MemberCreateDto;
import com.diarymate.dame.member.dto.response.MemberResponseDto;
import com.diarymate.dame.member.entity.Member;
import com.diarymate.dame.member.entity.Member.Role;
import com.diarymate.dame.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final ModelMapper mapper;

  public MemberResponseDto createMember(MemberCreateDto dto) {
    Member newMember = Member.builder()
        .id(null)
        .username(dto.getUsername())
        .role(Role.valueOf("ROLE_USER"))
        .build();
    return mapper.map(memberRepository.save(newMember), MemberResponseDto.class);
  }

  public List<Member> getMemberList(Pageable pageable) {
    return memberRepository.findAll(pageable)
        .stream()
        .collect(Collectors.toList());
  }

  public Member findMemberByEmail(String email) {
    return memberRepository.findByEmail(email);
  }

  public Optional<Member> findMemberById(Long id) {
    return memberRepository.findById(id);
  }

  public void registerMember(Member requestMember) {
    if(memberRepository.existsById(requestMember.getId())){
      return;
    }
    Member newMember = Member.builder()
        .id(requestMember.getId())
        .username(requestMember.getUsername())
        .email(requestMember.getEmail())
        .role(Role.ROLE_USER)
        .build();
    memberRepository.save(newMember);
  }
}