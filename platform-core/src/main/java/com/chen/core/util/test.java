package com.chen.core.util;

import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.nio.charset.Charset;

public class test {
    public static void main(String[] args) throws Exception {
        // 创建系统进程
        ProcessBuilder pb = new ProcessBuilder("tasklist");
        Process p = pb.start();
        BufferedReader out = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()), Charset.forName("GB2312")));
        BufferedReader err = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getErrorStream())));
        System.out.println("Window 系统进程列表");
        String ostr;

        while ((ostr = out.readLine()) != null){
            System.out.println(ostr);
            if (ostr.contains("esssignal.exe")){
                String command = "taskkill /f /im esssignal.exe";
                Runtime.getRuntime().exec(command);
            }

        }
    }
}