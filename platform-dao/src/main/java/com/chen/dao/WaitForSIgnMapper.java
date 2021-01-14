package com.chen.dao;

import com.chen.entity.pdf.WaitForSign;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface WaitForSIgnMapper {

    WaitForSign queryByUkid(@Param("UKID") String ukid);

    Integer updateSignvalByUUID(@Param("UUID")String uuid, @Param("SIGNVAL")String signval);

    Integer delByUUID(@Param("UUID")String uuid);

    List<WaitForSign> getAllOldTime(@Param("SUBTIME")String formatoldTime);

    int addWaitForSign(WaitForSign waitForSign);

    WaitForSign findWaitForSign(String wid);

    Integer updateSignvalState(@Param("uuid")String uuid, @Param("state")String state);
}
