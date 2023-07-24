package com.diarymate.dame.diary.controller;


import com.diarymate.dame.common.response.BaseResponse;
import com.diarymate.dame.diary.entity.Diary;
import com.diarymate.dame.diary.service.DiaryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("")
    public BaseResponse<List<Diary>> getDiaryList() {
        List<Diary> diaryList = diaryService.getDiaryList();
        return new BaseResponse<>(diaryList);
    }
}
