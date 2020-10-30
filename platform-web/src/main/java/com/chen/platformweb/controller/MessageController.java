package com.chen.platformweb.controller;

import com.chen.entity.Message;
import com.chen.entity.User;
import com.chen.service.IMessageService;
import com.chen.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.chen.platformweb.utils.SessionUtil.getSessionAttribute;

@Controller
@RequestMapping("/message/")
public class MessageController {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;

    @Autowired
    private IMessageService messageService;

    /**
     * 每次拦截到请求会先访问此函数
     * @param request http请求
     * @param response http回应
     */
    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }
    /**
     *访问消息列表
     * @param
     */
    @RequestMapping(value="list")
    public String seal_list(Model model) {
        Message message = new Message();
        message.setState(1);
        User user = (User)getSessionAttribute("loginUser");
        message.setReceiver(user.getUser_id());
        List<Message> messageList = messageService.findMessageList(message);
        model.addAttribute("messageList",  messageList);
        return "message/list";
    }
    /**
     *访问消息内容
     * @param
     */
    @RequestMapping(value="detail")
    public ModelAndView detail(String messageNo) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("message/detail");
        Message message = new Message();
        message.setMessageNo(messageNo);
        message.setState(1);
        message = messageService.findMessage(message);
        mv.addObject("message",  message);
        return mv;
    }
    /**
     *删除信息
     * @param
     */
    @RequestMapping(value="delete")
    @ResponseBody
    public String delete(String messageNo) {
        Message message = new Message();
        message.setMessageNo(messageNo);
        message.setState(1);
        boolean a = messageService.delete(messageNo);
        if (a){
            return "success";
        }
      return "error";
    }

}
