package com.chen.core.util;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;

import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.*;
public class HttpUtils {

    private static final String ENCODING = "UTF-8";

    public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }

    public static String get(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            String getUrl = url+"?";
            if (paramsMap != null) {
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    getUrl += param.getKey() + "=" + URLEncoder.encode(param.getValue(), ENCODING)+"&";
                }
            }
            HttpGet method = new HttpGet(getUrl);
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }
    //post请求参数为json格式
    public static String HttpPostWithJson(String url, String json) {
        String returnValue = "这是默认返回值，接口调用失败";
        CloseableHttpClient httpClient = HttpClients.createDefault();

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try{
            //第一步：创建HttpClient对象
            httpClient = HttpClients.createDefault();

            //第二步：创建httpPost对象
            HttpPost httpPost = new HttpPost(url);

            //第三步：给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);

            //第四步：发送HttpPost请求，获取返回值
            returnValue = httpClient.execute(httpPost,responseHandler); //调接口获取返回值时，必须用此方法

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //第五步：处理返回值
        return returnValue;
    }
    public static String HttpsPostWithJson(String url, String json) throws Exception {


        String returnValue = "这是默认返回值，接口调用失败";
        CloseableHttpClient httpClient = createHttpsClient();

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try{
            //第一步：创建HttpClient对象
            httpClient = HttpClients.createDefault();

            //第二步：创建httpPost对象
            HttpPost httpPost = new HttpPost(url);
            String headers = "{\"Accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\"}";
            //设置头信息
            JSONObject jsonObject = JSONObject.parseObject(headers);
            Set<String> keys = jsonObject.keySet();
            for (String key : keys){
                httpPost.setHeader(key,jsonObject.getString(key));
            }
            //第三步：给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);

            //第四步：发送HttpPost请求，获取返回值
            returnValue = httpClient.execute(httpPost,responseHandler); //调接口获取返回值时，必须用此方法

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //第五步：处理返回值
        return returnValue;


    }

    public static CloseableHttpClient createHttpsClient() throws Exception {

        //证书信任管理者
        X509TrustManager x509mgr = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        //获取一个SSLContext实例
        SSLContext sslContext = SSLContext.getInstance("TLS");
        //初始化SSLContext实例
        sslContext.init(null, new TrustManager[]{x509mgr}, new java.security.SecureRandom());
        //构建一个SSLConnectionSocket工厂，设置为信任所有证书
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        //返回CloseableHttpClient对象
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }

    /**
     * 发送Post请求
     * @param url url
     * @param params json,发送的内容
     * @return true or false
     */
    public static JSONObject post(String url, String params){
        String headers = "{\"Accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\"}";
        CloseableHttpClient closeableHttpClient = null;
        JSONObject result = new JSONObject();
        //建立一个连接
        try {
            closeableHttpClient = createHttpsClient();
            //创建一个HttpPost
            HttpPost httpPost = new HttpPost(url);

            //设置发送的内容
//            httpPost.setEntity(new StringEntity(params));

            //第三步：给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(params,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);
            //设置头信息
            JSONObject jsonObject = JSONObject.parseObject(headers);
            Set<String> keys = jsonObject.keySet();
            for (String key : keys){
                httpPost.setHeader(key,jsonObject.getString(key));
            }
            //发送
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            if (returnCode == HttpStatus.SC_OK) {
                InputStream is = httpResponse.getEntity().getContent();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
                result.put("code","200");
                result.put("result",JSONObject.parseObject(buffer.toString()));
                return result;
            } else {
                result.put("code","404");
                result.put("result","no result");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code","404");
            result.put("result","no result");
            return result;
        }
    }


    /**
     *
     *
     * @param hsUrl  支持https格式 get请求
     * @return  返回json格式
     */

    public static String getHttps(String hsUrl) {
        try {
            URL url;

            url = new URL(hsUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }
            };

            TrustManager[] tm = {xtm};

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);

            con.setSSLSocketFactory(ctx.getSocketFactory());
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });


            InputStream inStream = con.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            String rtn = new String(b, "utf-8");
            if (rtn!=null) {
//                JSONObject object = JSONObject.fromObject(rtn);
                return rtn;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
