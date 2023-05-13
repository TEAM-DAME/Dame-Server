package com.diarymate.dame.member.config;

import com.diarymate.dame.member.entity.Member;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class OAuth2Attribute {

  private Map<String, Object> attributes;
  private String attributeKey;
  private String nickname;
  private String email;
  private Long id;


  public static OAuth2Attribute of(String provider,
      Map<String, Object> attributes) {
    switch (provider) {
      case "kakao":
        return ofKakao(attributes);
      default:
        throw new RuntimeException();
    }
  }

  public static OAuth2Attribute ofKakao(Map<String, Object> attributes) {

    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
    return OAuth2Attribute.builder()
        .id((Long) attributes.get("id"))
        .nickname((String) kakaoProfile.get("nickname"))
        .email((String) kakaoAccount.get("email"))
        .attributes(attributes)
        .attributeKey("id")
        .build();
  }

  public Member toMember() {
    return Member.builder()
        .id(id)
        .email(email)
        .username(nickname)
        .build();
  }

  public Map<String, Object> toMap(long id) {
    Map<String, Object> map = new HashMap<>();
    map.put("id", id);
    map.put("key", attributeKey);
    map.put("nickname", nickname);
    map.put("email", email);

    return map;
  }

}