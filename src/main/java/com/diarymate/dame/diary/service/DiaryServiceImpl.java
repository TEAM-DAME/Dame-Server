package com.diarymate.dame.diary.service;

import com.diarymate.dame.common.response.BaseResponse;
import com.diarymate.dame.diary.entity.Diary;
import com.diarymate.dame.diary.repository.DiaryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;

    @Override
    public BaseResponse<Diary> createDiary() {
        return null;
    }

    @Override
    public BaseResponse<Diary> readDiary(Long diaryId) {
        return null;
    }

    @Override
    public BaseResponse<Diary> deleteDiary(Long diaryId) {
        return null;
    }

    @Override
    public List<Diary> getDiaryList() {
        return new ArrayList<>(diaryRepository.findAll());
    }

    @Override
    public BaseResponse<List<Diary>> getDiaryListByMemberId(Long memberId) {
        return null;
    }
}
