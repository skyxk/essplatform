package com.chen.dao;

import com.chen.entity.Apply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyMapper {
    List<Apply> findApplyList(Apply apply);
    int addApply(Apply apply);

    Apply findApplyById(String applyId);

    boolean updateApply(Apply apply);

    int delApplyById(String applyId);

    List<Apply> findApplyListByUKId(String ukid);

    List<Apply> findApplyListByKeyword(@Param("apply") Apply apply, @Param("keyword") String keyword);
}
