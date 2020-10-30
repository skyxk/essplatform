package com.chen.service;

import com.chen.entity.Apply;

import java.util.List;

public interface IApplyService {
    List<Apply> findApplyList(Apply apply);


    boolean addApply(Apply apply);

    Apply findApplyById(String applyId);

    boolean updateApply(Apply apply);

    boolean delApplyById(String applyId);

    List<Apply> findApplyListByUKId(String ukid);
    List<Apply> findApplyListByKeyword(Apply apply,String keyword);
}
