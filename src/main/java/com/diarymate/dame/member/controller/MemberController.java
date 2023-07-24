package com.diarymate.dame.member.controller;

import com.diarymate.dame.common.response.BaseResponse;
import com.diarymate.dame.member.entity.Member;
import com.diarymate.dame.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;


  @GetMapping("")
  public BaseResponse<List<Member>> getMemberList(Pageable pageable) {
    List<Member> members = memberService.getMemberList(pageable);
    return new BaseResponse<>(members);
  }
}
