package com.chen.service;

import com.chen.entity.Message;

import java.util.List;

public interface IMessageService {

    boolean add(Message m);

    boolean delete(String messageNo);

    List<Message> findMessageAll();

    List<Message> findMessageList(Message m);

    Message findMessage(Message m);

}
