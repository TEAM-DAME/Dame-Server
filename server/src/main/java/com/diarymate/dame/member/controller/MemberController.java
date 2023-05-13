package com.diarymate.dame.member.controller;

import com.diarymate.dame.member.dto.request.MemberCreateDto;
import com.diarymate.dame.member.dto.response.MemberResponseDto;
import com.diarymate.dame.member.entity.Member;
import com.diarymate.dame.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("")
  public MemberResponseDto createMember(MemberCreateDto member) {
    return memberService.createMember(member);
  }

  @GetMapping("")
  public List<Member> getMemberList(Pageable pageable) {
    return memberService.getMemberList(pageable);
  }
}