package com.chen.service.impl;

import com.chen.dao.MessageMapper;
import com.chen.entity.Apply;
import com.chen.entity.Message;
import com.chen.entity.Person;
import com.chen.service.IMessageService;
import com.chen.service.ISystemService;
import com.chen.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.chen.core.util.DateUtils.getDateTime;
import static com.chen.core.util.StringUtils.getUUID;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    public IUserService userService;
    @Autowired
    public ISystemService systemService;
    @Override
    public boolean add(Message m) {
        m.setSendTime(getDateTime());
        m.setMessageNo(getUUID());
        m.setReadState(2);
        m.setState(1);
        int i =  messageMapper.add(m);
        return i == 1;
    }

    @Override
    public boolean delete(String messageNo) {
        return messageMapper.delete(messageNo);
    }

    @Override
    public List<Message> findMessageAll() {
        return null;
    }

    @Override
    public List<Message> findMessageList(Message m) {
        List<Message> messageList = messageMapper.findMessageList(m);
        for (Message message :messageList){
            if (message.getSenderUser()!=null){
                message.setSenderUser(userService.findUserByPersonId(message.getSenderUser().getPerson_id()));
            }
            if (message.getReceiverUser()!=null){
                message.setReceiverUser(userService.findUserByPersonId(message.getReceiverUser().getPerson_id()));
            }
        }
        return messageList;
    }

    @Override
    public Message findMessage(Message m) {

        Message message = messageMapper.findMessage(m);
        if (message.getSenderUser()!=null){
            message.setSenderUser(userService.findUserByPersonId(message.getSenderUser().getPerson_id()));
        }
        if (message.getReceiverUser()!=null){
            message.setReceiverUser(userService.findUserByPersonId(message.getReceiverUser().getPerson_id()));
        }
        return message;
    }
}
