package com.diarymate.dame.member.service;

import com.diarymate.dame.member.config.OAuth2Attribute;
import com.diarymate.dame.member.entity.Member;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final MemberService memberService;

  public CustomOAuth2UserService(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    OAuth2Attribute attributes = OAuth2Attribute
        .of(registrationId, oAuth2User.getAttributes());

    Member requestMember = attributes.toMember();
    memberService.registerMember(requestMember); // id로 멤버가 있는지 확인후 없으면 생성
    log.info(attributes.toString());
    log.info(requestMember.toString());
    log.info(oAuth2User.getAttributes().toString());
    Optional<Member> findMember = memberService.findMemberById(requestMember.getId());

    return new DefaultOAuth2User(
        Collections.singleton(new
            SimpleGrantedAuthority("ROLE_USER")),
        attributes.toMap(findMember.get().getId()), "id");
  }


}