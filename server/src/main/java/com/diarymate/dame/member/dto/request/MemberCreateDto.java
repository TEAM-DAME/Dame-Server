package com.diarymate.dame.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberCreateDto {
  @NotNull
  private String username;
  @NotNull
  private String password;
}
