package com.diarymate.dame.common.response;

import static com.diarymate.dame.common.response.BaseResponseStatus.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
  @JsonProperty("isSuccess")
  private final Boolean isSuccess;
  private final String message;
  private final int status;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T result;

  // 요청에 성공한 경우
  public BaseResponse(T result) {
    this.isSuccess = SUCCESS.isSuccess();
    this.status = SUCCESS.getStatus();
    this.message = SUCCESS.getMessage();
    this.result = result;
  }

  // 요청에 실패한 경우
  public BaseResponse(BaseResponseStatus status) {
    this.isSuccess = status.isSuccess();
    this.status = status.getStatus();
    this.message = status.getMessage();
  }

  public static BaseResponse<BaseResponseStatus> from(BaseResponseStatus status) {
    return new BaseResponse<>(
        status.isSuccess(), status.getMessage(), status.getStatus(), status
    );
  }

  public static BaseResponse<String> from(BaseResponseStatus status, String fieldName) {
    return new BaseResponse<>(
        status.isSuccess(), status.getMessage(), status.getStatus(), fieldName
    );
  }
}