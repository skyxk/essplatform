package com.chen.service.impl;

import com.chen.dao.ApplyMapper;
import com.chen.entity.Apply;
import com.chen.entity.Seal;
import com.chen.service.IApplyService;
import com.chen.service.ISealImgService;
import com.chen.service.ISystemService;
import com.chen.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplyServiceImpl implements IApplyService {

    @Autowired
    private ApplyMapper applyMapper;
    @Autowired
    public ISystemService systemService;
    @Autowired
    public ISealImgService sealImgService;
    @Autowired
    public IUserService userService;

    @Override
    public List<Apply> findApplyList(Apply apply) {
        apply = encryptApply(apply);
        List<Apply> applyList = applyMapper.findApplyList(apply);
        for (Apply apply1 :applyList){
            apply1 = decryptApply(apply1);
            if (apply1.getApply_user()!=null){
                apply1.setApply_user(userService.findUserByPersonId(apply1.getApply_user().getPerson_id()));
            }
            if (apply1.getReview_user()!=null){
                apply1.setReview_user(userService.findUserByPersonId(apply1.getReview_user().getPerson_id()));
            }
            if (apply1.getMake_user()!=null){
                apply1.setMake_user(userService.findUserByPersonId(apply1.getMake_user().getPerson_id()));
            }
        }
        return applyList;
    }
    @Override
    public boolean addApply(Apply apply) {
        apply = encryptApply(apply);
        int result =applyMapper.addApply(apply);
        apply = decryptApply(apply);
        return result == 1;
    }
    @Override
    public Apply findApplyById(String applyId) {
        Apply apply = decryptApply(applyMapper.findApplyById(applyId));
        if (apply.getApply_user()!=null){
            apply.setApply_user(userService.findUserByPersonId(apply.getApply_user().getPerson_id()));
        }
        if (apply.getReview_user()!=null){
            apply.setReview_user(userService.findUserByPersonId(apply.getReview_user().getPerson_id()));
        }
        if (apply.getMake_user()!=null){
            apply.setMake_user(userService.findUserByPersonId(apply.getMake_user().getPerson_id()));
        }
        return apply;
    }

    @Override
    public boolean updateApply(Apply apply) {
        apply = encryptApply(apply);
        boolean result = applyMapper.updateApply(apply);
        apply = decryptApply(apply);
        return result;
    }

    @Override
    public boolean delApplyById(String applyId) {
        int result = applyMapper.delApplyById(applyId);
        if(result==1){
            return true;
        }
        return false;
    }
    @Override
    public List<Apply> findApplyListByUKId(String ukid) {
        List<Apply> applyList = applyMapper.findApplyListByUKId(ukid);
        for (Apply apply :applyList){
            apply = decryptApply(apply);
            if (apply.getApply_user()!=null){
                apply.setApply_user(userService.findUserByPersonId(apply.getApply_user().getPerson_id()));
            }
            if (apply.getReview_user()!=null){
                apply.setReview_user(userService.findUserByPersonId(apply.getReview_user().getPerson_id()));
            }
            if (apply.getMake_user()!=null){
                apply.setMake_user(userService.findUserByPersonId(apply.getMake_user().getPerson_id()));
            }
        }
        return applyList;
    }

    @Override
    public List<Apply> findApplyListByKeyword(Apply apply, String keyword) {
        keyword = systemService.encryptString(keyword);
        List<Apply> applyList = applyMapper.findApplyListByKeyword(apply,keyword);
        for (Apply apply1 :applyList){
            apply1 = decryptApply(apply1);
            if (apply1.getApply_user()!=null){
                apply1.setApply_user(userService.findUserByPersonId(apply1.getApply_user().getPerson_id()));
            }
            if (apply1.getReview_user()!=null){
                apply1.setReview_user(userService.findUserByPersonId(apply1.getReview_user().getPerson_id()));
            }
            if (apply1.getMake_user()!=null){
                apply1.setMake_user(userService.findUserByPersonId(apply1.getMake_user().getPerson_id()));
            }
        }
        return applyList;
    }
    public Apply encryptApply(Apply apply){
        if (apply==null){
            return null;
        }
        //印章名称
        if (apply.getSeal_name()!=null){
            apply.setSeal_name(systemService.encryptStringJMJ(apply.getSeal_name()));
        }
        if (apply.getJbr_card_data()!=null){
            apply.setJbr_card_data(systemService.encryptString(apply.getJbr_card_data()));
        }

        if (apply.getJbr_card_name()!=null){
            apply.setJbr_card_name(systemService.encryptString(apply.getJbr_card_name()));
        }
        return apply;
    }
    public Apply decryptApply(Apply apply){
        if (apply==null){
            return null;
        }
        //印章名称
        if (apply.getSeal_name()!=null){
            apply.setSeal_name(systemService.decryptStringJMJ(apply.getSeal_name()));
        }
        if (apply.getJbr_card_data()!=null){
            apply.setJbr_card_data(systemService.decryptString(apply.getJbr_card_data()));
        }

        if (apply.getJbr_card_name()!=null){
            apply.setJbr_card_name(systemService.decryptString(apply.getJbr_card_name()));
        }
        return apply;
    }
}
