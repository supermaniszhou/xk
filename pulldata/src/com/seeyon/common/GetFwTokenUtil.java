package com.seeyon.common;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.common.collect.HppcMaps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetFwTokenUtil {
    public static void main(String[] args) {
        String address = "http://10.11.100.64:8888";
//        String token = testGetoken(address);
//        System.out.println(token);
    }

    public static String requestHttp(String reqUrl, Map<String, String> param,String sendUserId) {
        ProptiesUtil pUtil = new ProptiesUtil();
        String address = pUtil.getServerUrl();
        Map<String, Object> objectMap = GetFwTokenUtil.testRegist(address);
        String token = GetFwTokenUtil.testGetoken(objectMap);
        String appId = pUtil.getAppId();
        String spk = StrUtil.nullToEmpty((String) objectMap.get("spk"));
        RSA rsa = new RSA(null, spk);
        //注意使用getSendUserId参数进行删除操作
        String userId = rsa.encryptBase64(sendUserId, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        Map<String, String> headers = new HashMap<>();
        headers.put("appid", appId);
        headers.put("token", token);
        headers.put("userid", userId);
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        String url = address + reqUrl;
        String back1 = HttpClient.httpPostForm(url, param, headers, "utf-8");
        return back1;
    }

    public static String getOaToken() {
        ProptiesUtil prop = new ProptiesUtil();
        String token = "";
        String url = prop.getOaUrl() + "/seeyon/rest/token";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = null;
        HttpResponse response = null;
        httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        String requestParams = "{\"userName\":\"" + prop.getRestUsername() + "\",\"password\":\"" + prop.getRestPassword() + "\",\"loginName\":\"" + prop.getSendLoginName() + "\"}";
        StringEntity postingString = new StringEntity(requestParams, "utf-8");
        httpPost.setEntity(postingString);
        try {
            response = client.execute(httpPost);
            response.setHeader("Cache-Control", "no-cache");
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String resultString = EntityUtils.toString(response.getEntity(), "utf-8").replaceAll(" ", "");
                JSONObject jsonObject = JSON.parseObject(resultString);
                token = (String) jsonObject.get("id");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }


    /**
     * 第一步：
     * <p>
     * 调用ecology注册接口,根据appid进行注册,将返回服务端公钥和Secret信息
     */
    public static Map<String, Object> testRegist(String address) {

        //获取当前系统RSA加密的公钥
        RSA rsa = new RSA();
        String publicKey = rsa.getPublicKeyBase64();
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address + "/api/ec/dev/auth/regist")
                .header("appid", new ProptiesUtil().getProperties().getProperty("fw.appid"))
                .header("cpk", publicKey)
                .timeout(2000)
                .execute().body();

        // 打印ECOLOGY响应信息
        Map<String, Object> datas = JSONUtil.parseObj(data);

        return datas;
    }


    /**
     * 第二步：
     * <p>
     * 通过第一步中注册系统返回信息进行获取token信息
     */
    public static String testGetoken(Map<String, Object> mp) {
        String spk = StrUtil.nullToEmpty((String) mp.get("spk"));
        String secret = StrUtil.nullToEmpty((String) mp.get("secrit"));
        RSA rsa = new RSA(null, spk);
        String encryptSecret = rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        ProptiesUtil proptiesUtil = new ProptiesUtil();
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(proptiesUtil.getServerUrl() + "/api/ec/dev/auth/applytoken")
                .header("appid", proptiesUtil.getAppId())
                .header("secret", encryptSecret)
                .header("time", "3600")
                .execute().body();

        Map<String, Object> datas = JSONUtil.parseObj(data);
        return StrUtil.nullToEmpty((String) datas.get("token"));
    }

}
