package com.chen.dao;

import com.chen.entity.Message;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageMapper {
    int add(Message m);

//    int delete(Message m);

    int update(Message m);

//    List<Message> findMessageAll(Message m);

    List<Message> findMessageList(Message m);

    Message findMessage(Message m);

    boolean delete(String messageNo);
}
