package com.chen.dao;

import com.chen.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mrche
 */
@Repository
public interface UserMapper {

//    @Select("SELECT	*   FROM  USER	WHERE	NAME	=	#{name}")
//    User findByName(@Param("name")	String	name);
//
//    @Insert("INSERT	INTO	USER(NAME,	AGE)	VALUES(#{name},	#{age})")
//    int	insert(@Param("name")	String	name, @Param("age")	Integer	age);

    List<User> findAdminUserList(User user);

    User findUserById(String userId);

    User findUserByPersonId(String personId);
}
