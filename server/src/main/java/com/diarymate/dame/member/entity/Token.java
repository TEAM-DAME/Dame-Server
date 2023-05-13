package com.diarymate.dame.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {

  private final String accessToken;
  private final String refreshToken;

}
