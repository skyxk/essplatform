package com.chen.platformweb;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getStudentList() throws Exception {

        //准备请求url  不用带ip、端口、项目名称等 直接写接口的映射地址就可以了
        String url = "/queryUnit.html";
        /* 构建request 发送请求GET请求
         * MockMvcRequestBuilders 中有很多 请求方式。像get、post、put、delete等等
         */
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON)) // 断言返回结果是json
                .andReturn();// 得到返回结果
        MockHttpServletResponse response = mvcResult.getResponse();
        //拿到请求返回码
        int status = response.getStatus();
        //拿到结果
        String contentAsString = response.getContentAsString();

        System.err.println(status);
        System.err.println(contentAsString);
    }


    /**
     * @throws Exception
     * @创建时间 2018年7月13日
     * @功能描述  传递post请求和 bean类型对象 ，接受 返回值
     */
    @Test
    public void postTest() throws Exception {
        // uri
        String uri = "/app/get3";
//
//        UserModel userModel = new UserModel("张三", 11, new Date(), "abc123");
//
//        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JSON.toJSONString(userModel))
//                .accept(MediaType.APPLICATION_JSON)) // 断言返回结果是json
//                .andReturn();// 得到返回结果
//
//        MockHttpServletResponse response = mvcResult.getResponse();
//        //拿到请求返回码
//        int status = response.getStatus();
//        //拿到结果
//        String contentAsString = response.getContentAsString();
//
//        System.err.println(status);
//        System.err.println(contentAsString);

    }


}
