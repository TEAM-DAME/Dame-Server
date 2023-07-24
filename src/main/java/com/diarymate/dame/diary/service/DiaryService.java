package com.diarymate.dame.diary.service;

import com.diarymate.dame.common.response.BaseResponse;
import com.diarymate.dame.diary.entity.Diary;
import java.util.List;

public interface DiaryService {

    //Diary Create
    BaseResponse<Diary> createDiary();

    //Diary Read
    BaseResponse<Diary> readDiary(Long diaryId);

    //Diary Delete
    BaseResponse<Diary> deleteDiary(Long diaryId);

    //Diary List
    List<Diary> getDiaryList();
    BaseResponse<List<Diary>> getDiaryListByMemberId(Long memberId);

}
