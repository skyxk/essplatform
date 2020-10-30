package com.chen.core.util;

import com.chen.core.bean.HttpResult;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http輔助工具類</br>
 * 依賴HttpClient 4.5.x版本
 *
 * @author comven
 */
public class HttpHelper {
//    private static Logger logger = LoggerFactory.getLogger(HttpHelper.class);
    private static final String DEFAULT_CHARSET = "UTF-8";// 默認請求編碼
    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;// 默認等待響應時間(毫秒)
    private static final int DEFAULT_RETRY_TIMES = 0;// 默認執行重試的次數

    /**
     * 創建一個默認的可關閉的HttpClient
     *
     * @return
     */
    public static CloseableHttpClient createHttpClient() {
        return createHttpClient(DEFAULT_RETRY_TIMES, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * 創建一個可關閉的HttpClient
     *
     * @param socketTimeout 請求獲取數據的超時時間
     * @return
     */
    public static CloseableHttpClient createHttpClient(int socketTimeout) {
        return createHttpClient(DEFAULT_RETRY_TIMES, socketTimeout);
    }

    /**
     * 創建一個可關閉的HttpClient
     *
     * @param socketTimeout 請求獲取數據的超時時間
     * @param retryTimes    重試次數，小於等於0表示不重試
     * @return
     */
    public static CloseableHttpClient createHttpClient(int retryTimes, int socketTimeout) {
        Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(5000);// 設置連接超時時間，單位毫秒
        builder.setConnectionRequestTimeout(1000);// 設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共享連接池的。
        if (socketTimeout >= 0) {
            builder.setSocketTimeout(socketTimeout);// 請求獲取數據的超時時間，單位毫秒。 如果訪問一個接口，多少時間內無法返回數據，就直接放棄此次調用。
        }
        RequestConfig defaultRequestConfig = builder.setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
        // 開啟HTTPS支持
        enableSSL();
        // 創建可用Scheme
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
        // 創建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (retryTimes > 0) {
            setRetryHandler(httpClientBuilder, retryTimes);
        }
        CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
        return httpClient;
    }

    /**
     * 執行GET請求
     *
     * @param url           遠程URL地址
     * @param charset       請求的編碼，默認UTF-8
     * @param socketTimeout 超時時間（毫秒）
     * @return HttpResult
     * @throws IOException
     */
    public static HttpResult executeGet(String url, String charset, int socketTimeout) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executeGet(httpClient, url, null, null, charset, true);
    }

    /**
     * 執行GET請求
     *
     * @param url           遠程URL地址
     * @param charset       請求的編碼，默認UTF-8
     * @param socketTimeout 超時時間（毫秒）
     * @return String
     * @throws IOException
     */
    public static String executeGetString(String url, String charset, int socketTimeout) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executeGetString(httpClient, url, null, null, charset, true);
    }

    /**
     * 執行HttpGet請求
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param url             請求的遠程地址
     * @param referer         referer信息，可傳null
     * @param cookie          cookies信息，可傳null
     * @param charset         請求編碼，默認UTF8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return HttpResult
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeGet(CloseableHttpClient httpClient, String url, String referer, String cookie, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executeGetResponse(httpClient, url, referer, cookie);
            //Http請求狀態碼
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param httpClient httpclient對象
     * @param url        執行GET的URL地址
     * @param referer    referer地址
     * @param cookie     cookie信息
     * @return CloseableHttpResponse
     * @throws IOException
     */
    public static CloseableHttpResponse executeGetResponse(CloseableHttpClient httpClient, String url, String referer, String cookie) throws IOException {
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        HttpGet get = new HttpGet(url);
        if (cookie != null && !"".equals(cookie)) {
            get.setHeader("Cookie", cookie);
        }
        if (referer != null && !"".equals(referer)) {
            get.setHeader("referer", referer);
        }
        return httpClient.execute(get);
    }

    /**
     * 執行HttpGet請求
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param url             請求的遠程地址
     * @param referer         referer信息，可傳null
     * @param cookie          cookies信息，可傳null
     * @param charset         請求編碼，默認UTF8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return String
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String executeGetString(CloseableHttpClient httpClient, String url, String referer, String cookie, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executeGetResponse(httpClient, url, referer, cookie);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 簡單方式執行POST請求
     *
     * @param url           遠程URL地址
     * @param paramsObj     post的參數，支持map<String,String>,JSON,XML
     * @param charset       請求的編碼，默認UTF-8
     * @param socketTimeout 超時時間(毫秒)
     * @return HttpResult
     * @throws IOException
     */
    public static HttpResult executePost(String url, Object paramsObj, String charset, int socketTimeout) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executePost(httpClient, url, paramsObj, null, null, charset, true);
    }

    /**
     * 簡單方式執行POST請求
     *
     * @param url           遠程URL地址
     * @param paramsObj     post的參數，支持map<String,String>,JSON,XML
     * @param charset       請求的編碼，默認UTF-8
     * @param socketTimeout 超時時間(毫秒)
     * @return HttpResult
     * @throws IOException
     */
    public static String executePostString(String url, Object paramsObj, String charset, int socketTimeout) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executePostString(httpClient, url, paramsObj, null, null, charset, true);
    }

    /**
     * 執行HttpPost請求
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param url             請求的遠程地址
     * @param paramsObj       提交的參數信息，目前支持Map,和String(JSON\xml)
     * @param referer         referer信息，可傳null
     * @param cookie          cookies信息，可傳null
     * @param charset         請求編碼，默認UTF8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static HttpResult executePost(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset);
            //Http請求狀態碼
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 執行HttpPost請求
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param url             請求的遠程地址
     * @param paramsObj       提交的參數信息，目前支持Map,和String(JSON\xml)
     * @param referer         referer信息，可傳null
     * @param cookie          cookies信息，可傳null
     * @param charset         請求編碼，默認UTF8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return String
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String executePostString(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * @param httpClient HttpClient對象
     * @param url        請求的網絡地址
     * @param paramsObj  參數信息
     * @param referer    來源地址
     * @param cookie     cookie信息
     * @param charset    通信編碼
     * @return CloseableHttpResponse
     * @throws IOException
     */
    private static CloseableHttpResponse executePostResponse(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset) throws IOException {
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        HttpPost post = new HttpPost(url);
        if (cookie != null && !"".equals(cookie)) {
            post.setHeader("Cookie", cookie);
        }
        if (referer != null && !"".equals(referer)) {
            post.setHeader("referer", referer);
        }
        // 設置參數
        HttpEntity httpEntity = getEntity(paramsObj, charset);
        if (httpEntity != null) {
            post.setEntity(httpEntity);
        }
        return httpClient.execute(post);
    }

    /**
     * 執行文件上傳
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param remoteFileUrl   遠程接收文件的地址
     * @param localFilePath   本地文件地址
     * @param charset         請求編碼，默認UTF-8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeUploadFile(CloseableHttpClient httpClient, String remoteFileUrl,
                                               String localFilePath, String paramName,String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            // 把文件轉換成流對象FileBody
            File localFile = new File(localFilePath);

            FileBody fileBody = new FileBody(localFile, ContentType.DEFAULT_BINARY);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart(paramName, fileBody);
            // 相当于 <input type="file" class="file" name="file">,匹配@RequestParam("file")
            // .addPart()可以设置模拟浏览器<input/>的表单提交
            HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build();

            // uploadFile對應服務端類的同名屬性<File類型>
            // .addPart("uploadFileName", uploadFileName)
            // uploadFileName對應服務端類的同名屬性<String類型>
            HttpPost httpPost = new HttpPost(remoteFileUrl);
            httpPost.setEntity(reqEntity);
            httpResponse = httpClient.execute(httpPost);
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 執行文件上傳(以二進制流方式)
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param remoteFileUrl   遠程接收文件的地址
     * @param localFilePath   本地文件地址
     * @param charset         請求編碼，默認UTF-8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeUploadFileStream(CloseableHttpClient httpClient, String remoteFileUrl,
                                                     String localFilePath, String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
        CloseableHttpResponse httpResponse = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            // 把文件轉換成流對象FileBody
            File localFile = new File(localFilePath);
            fis = new FileInputStream(localFile);
            byte[] tmpBytes = new byte[1024];
            byte[] resultBytes = null;
            baos = new ByteArrayOutputStream();
            int len;
            while ((len = fis.read(tmpBytes, 0, 1024)) != -1) {
                baos.write(tmpBytes, 0, len);
            }
            resultBytes = baos.toByteArray();
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(resultBytes, ContentType.APPLICATION_OCTET_STREAM);
            HttpPost httpPost = new HttpPost(remoteFileUrl);
            httpPost.setEntity(byteArrayEntity);
            httpResponse = httpClient.execute(httpPost);
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 執行文件下載
     *
     * @param httpClient      HttpClient客戶端實例，傳入null會自動創建一個
     * @param remoteFileUrl   遠程下載文件地址
     * @param localFilePath   本地存儲文件地址
     * @param charset         請求編碼，默認UTF-8
     * @param closeHttpClient 執行請求結束後是否關閉HttpClient客戶端實例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static boolean executeDownloadFile(CloseableHttpClient httpClient, String remoteFileUrl,Object obj, String localFilePath
            ,String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }

            HttpPost httpPost = new HttpPost(remoteFileUrl);
            HttpEntity aa = getEntity(obj,charset);
            httpPost.setEntity(aa);

            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return false;
            }
            in = entity.getContent();
            File file = new File(localFilePath);
            fout = new FileOutputStream(file);
            int l;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
                // 註意這裡如果用OutputStream.write(buff)的話，圖片會失真
            }
            // 將文件輸出到本地
            fout.flush();
            EntityUtils.consume(entity);
            return true;
        } finally {
            // 關閉低層流。
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 根據參數獲取請求的Entity
     *
     * @param paramsObj
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpEntity getEntity(Object paramsObj, String charset) throws UnsupportedEncodingException {
        if (paramsObj == null) {
//            logger.info("當前未傳入參數信息，無法生成HttpEntity");
            return null;
        }
        if (Map.class.isInstance(paramsObj)) {// 當前是map數據
            @SuppressWarnings("unchecked")
            Map<String, String> paramsMap = (Map<String, String>) paramsObj;
            List<NameValuePair> list = getNameValuePairs(paramsMap);
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
            httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            return httpEntity;
        } else if (String.class.isInstance(paramsObj)) {// 當前是string對象，可能是
            String paramsStr = (String) paramsObj;
            StringEntity httpEntity = new StringEntity(paramsStr, charset);
            if (paramsStr.startsWith("{")) {
                httpEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            } else if (paramsStr.startsWith("<")) {
                httpEntity.setContentType(ContentType.APPLICATION_XML.getMimeType());
            } else {
                httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            }
            return httpEntity;
        } else {
//            logger.info("當前傳入參數不能識別類型，無法生成HttpEntity");
        }
        return null;
    }

    /**
     * 從結果中獲取出String數據
     *
     * @param httpResponse http結果對象
     * @param charset      編碼信息
     * @return String
     * @throws ParseException
     * @throws IOException
     */
    private static String getResult(CloseableHttpResponse httpResponse, String charset) throws ParseException, IOException {
        String result = null;
        if (httpResponse == null) {
            return result;
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return result;
        }
        result = EntityUtils.toString(entity, charset);
        EntityUtils.consume(entity);// 關閉應該關閉的資源，適當的釋放資源 ;也可以把底層的流給關閉瞭
        return result;
    }

    /**
     * 轉化請求編碼
     *
     * @param charset 編碼信息
     * @return String
     */
    private static String getCharset(String charset) {
        return charset == null ? DEFAULT_CHARSET : charset;
    }

    /**
     * 將map類型參數轉化為NameValuePair集合方式
     *
     * @param paramsMap
     * @return
     */
    private static List<NameValuePair> getNameValuePairs(Map<String, String> paramsMap) {
        List<NameValuePair> list = new ArrayList<>();
        if (paramsMap == null || paramsMap.isEmpty()) {
            return list;
        }
        for (Entry<String, String> entry : paramsMap.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    /**
     * 開啟SSL支持
     */
    private static void enableSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{manager}, null);
            socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SSLConnectionSocketFactory socketFactory;

    // HTTPS網站一般情況下使用瞭安全系數較低的SHA-1簽名，因此首先我們在調用SSL之前需要重寫驗證方法，取消檢測SSL。
    private static TrustManager manager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            //

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            //

        }
    };

    /**
     * 為httpclient設置重試信息
     *
     * @param httpClientBuilder
     * @param retryTimes
     */
    private static void setRetryHandler(HttpClientBuilder httpClientBuilder, final int retryTimes) {
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= retryTimes) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果請求被認為是冪等的，那麼就重試
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        httpClientBuilder.setRetryHandler(myRetryHandler);
    }


    public static String decaptcha(File file,String url,String paramName) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        RequestConfig config = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(20000).build();
        post.setConfig(config);

        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart(paramName, fileBody);
        // 相当于 <input type="file" class="file" name="file">,匹配@RequestParam("file")
        // .addPart()可以设置模拟浏览器<input/>的表单提交
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        String result = "";

        try {
            CloseableHttpResponse e = client.execute(post);
            HttpEntity resEntity = e.getEntity();
            if(entity != null) {
                result = EntityUtils.toString(resEntity);
                System.out.println("response content:" + result);
            }
        } catch (IOException var10) {
//            logger.error("请求解析验证码io异常",var10);
            var10.printStackTrace();
        }

        return result;
    }
}