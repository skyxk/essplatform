package com.chen.service;

import com.chen.entity.Person;
import com.chen.entity.User;

import java.util.List;

public interface IUserService {
    /**
     * 根据指定条件查询用户
     * @param user 用户查询限定条件
     * @return 用户list对象
     */
    List<User> findUserList(User user);

    /**
     * 根据指定条件查询制作系统管理员
     * @param user 用户查询限定条件
     * @return 用户list对象
     */
    List<User> findAdminUserList(User user);

    /**
     * 根据personID查找管理员User
     * @param personId 人员ID
     * @return 管理员User对象
     */
    User findUserByPersonId(String personId);

    List<Person> findPersonListByKeyword(String keyword);
}
