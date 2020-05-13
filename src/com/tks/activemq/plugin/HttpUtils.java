package com.tks.activemq.plugin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Titel:一卡通云平台tks
 * Description:
 * Author: zhujing
 * Date: 2016/4/2
 * Time: 15:53
 * QQ:283883031
 */
public class HttpUtils {

    private static final Logger log = LoggerFactory
            .getLogger(HttpUtils.class);

    public static CloseableHttpClient getHttpClient() {
        return getHttpClient(-1);
    }

    public static CloseableHttpClient getHttpClient(long keepAlive) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
//指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //信任任何链接
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };
            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);

        ConnectionKeepAliveStrategy kaStrategy = null ;

        if (keepAlive != -1) {
            kaStrategy = new DefaultConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {

                    return keepAlive;
                }

            };
        }
//      connManager.setDefaultConnectionConfig(connConfig);
//      connManager.setDefaultSocketConfig(socketConfig);
        //构建客户端
        return kaStrategy == null ? HttpClientBuilder.create().setConnectionManager(connManager).build() : HttpClientBuilder.create().setConnectionManager(connManager).setKeepAliveStrategy(kaStrategy).build();
    }


    /**
     * @param url        请求地址
     * @param jsonParams 请求的json 参数
     * @return 返回json
     */
    public static String execute(String url, String jsonParams) {

        return execute(url, jsonParams, "application/json");
    }

    /**
     * @param url        请求地址
     * @param jsonParams 请求的json 参数
     * @return 返回json
     */
    public static String execute(String url, String jsonParams, String contentType) {
        return execute(url, jsonParams, contentType, null);

    }


    public static String execute(String url, String jsonParams, String contentType, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = null;

        if (url != null) {
            if (url.startsWith("https")) {
                httpClient = getHttpClient();
            } else {
                httpClient = HttpClients.createDefault();
            }
        }
        HttpPost method = new HttpPost(url);

        StringEntity entity = new StringEntity(jsonParams, "utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType(contentType);
        method.setEntity(entity);
        //add headers
        if (headerMap != null) {
            for (Iterator<Map.Entry<String, String>> it = headerMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                method.addHeader(entry.getKey(), entry.getValue());
            }
        }


        String resData = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(method);

            HttpEntity r = response.getEntity();

            resData = EntityUtils.toString(r);

            EntityUtils.consume(r);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("关闭reponse出错");
                }
            }
        }

        log.info("调用url:{}" + "参数:{}"+" 返回数据：{}" , url,jsonParams,resData);

        return resData;

    }


    public static String doPost(String url, Map<String, String> map) {

        CloseableHttpClient httpClient = null ;

        HttpPost httpPost = null;
        String result = null;
        CloseableHttpResponse response = null ;
        try {
            httpClient = getHttpClient(60000l);
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
                log.info(elem.getKey() + ":" + elem.getValue());
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("关闭reponse出错");
                }
            }
        }
        return result;
    }


}
