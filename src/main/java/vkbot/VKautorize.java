package vkbot;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import parser.ReaderJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VKautorize {

    private static Logger logger = Logger.getLogger(VKautorize.class);
    private static  String EMAIL = "";
    private static  String PASS = "";
    private static  String GROUP = "";
    private static  String COMMENT = "";

    public VKautorize(String EMAIL, String  PASS, String GROUP, String COMMENT) throws IOException {


        VKautorize.EMAIL = EMAIL;
        VKautorize.PASS = PASS;
        VKautorize.GROUP = GROUP;
        VKautorize.COMMENT = COMMENT;
    }

    private static final String USER_AGENT = "Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

    private static final String GET_URL = "https://vk.com/";
    private static final String POST_URL = "https://login.vk.com/?act=login";
    private static final String LOGIN_URL = "https://vk.com/login.php?act=slogin&to=&s=1&__q_hash=";

    private static String IP_H = "";
    private static String LG_H = "";
    private static String Q_HASH = "";
    private static String POST_HASH = "";
    private static String REMIXSID = "";
  //  private static final HttpHost proxyHost = new HttpHost("127.0.0.1", 8888);
    private static final String vkApi = "https://api.vk.com/method/wall.get?domain=";

    private static final String patternLG_H = "<input type=\"hidden\" name=\"lg_h\" value=\"((\\w)*)\"";
    private static final String patternIP_H = "<input type=\"hidden\" name=\"ip_h\" value=\"((\\w)*)\"";
    private static final String patternRemixlhk = "remixlhk=((\\w)*)";
    private static final String patternQ_Hash = "__q_hash=((\\w)*)";
    private static final String patternRemixsid = "remixsid=((\\w)*);";
    private static final String patternHash = "post_hash\\\\\":\\\\\"((\\w)*)";



    private static final Pattern lg_H = Pattern.compile(patternLG_H);
    private static final Pattern ip_H = Pattern.compile(patternIP_H);
    private static final Pattern remixlhk = Pattern.compile(patternRemixlhk);
    private static final Pattern q_hash = Pattern.compile(patternQ_Hash);
    private static final Pattern remixsid = Pattern.compile(patternRemixsid);
    private static final Pattern hash = Pattern.compile(patternHash);


    private static final RequestConfig globalConfig = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.DEFAULT)
            .setRedirectsEnabled(false)
       //     .setProxy(proxyHost)
            .build();

       CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(globalConfig)
            .build();

    public CloseableHttpResponse sendGET() throws IOException {
        logger.info("Connect to VK" + "\n");
        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpGet httpGet = new HttpGet(GET_URL);
        httpGet.setConfig(localConfig);
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        Matcher m1 = lg_H.matcher(response.toString());
        Matcher m2 = ip_H.matcher(response.toString());
        if (m1.find()) {
            LG_H = m1.group(1);
        }
        if (m2.find()) {
            IP_H = m2.group(1);
        }
        reader.close();
        logger.info("Connect success. ResponseStatus: "  + httpResponse.getStatusLine().getStatusCode() + "\n");
        return httpResponse;
    }

    public String sendPOST(CloseableHttpResponse closeableHttpResponse) throws IOException {

        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setRedirectsEnabled(false)
                .build();
        HttpPost httpPost = new HttpPost
                (POST_URL);
        httpPost.setConfig(localConfig);
        httpPost.addHeader("User-Agent", USER_AGENT);
        String stringRemixlhk = "";
        for (Header header : closeableHttpResponse.getHeaders("Set-Cookie")) {
            String setCookie = ((BufferedHeader) header).getBuffer().toString();
            if (setCookie.startsWith("Set-Cookie: remixlhk=")) {
                Matcher m = remixlhk.matcher(setCookie);
                if (m.find()) {	stringRemixlhk = m.group(1);
                }
            }
        }
        httpPost.addHeader("Cookie", "remixlhk="+stringRemixlhk);
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("ip_h", IP_H));
        urlParameters.add(new BasicNameValuePair("lg_h", LG_H));
        urlParameters.add(new BasicNameValuePair("email", EMAIL));
        urlParameters.add(new BasicNameValuePair("pass", PASS));
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(postParams);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        Header headers = httpResponse.getLastHeader("Location");
        Matcher m3 = q_hash.matcher(headers.getValue());
        if (m3.find()) {
            Q_HASH = m3.group(1);
        }
        logger.info("Get Q_HASH success. ResponseStatus: "  + httpResponse.getStatusLine().getStatusCode() + "\n");
        return Q_HASH;
    }

    public String getAutorization() throws IOException {
        String q_HASH = sendPOST(sendGET());
        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpGet httpGet = new HttpGet(LOGIN_URL+ q_HASH);
        httpGet.setConfig(localConfig);
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        Header[] headers = httpResponse.getAllHeaders();
        Matcher m = remixsid.matcher(headers[9].getValue());
        if (m.find()) {
            REMIXSID = m.group(1);
        }
        logger.info("Authorization success. ResponseStatus: "  + httpResponse.getStatusLine().getStatusCode() + "\n");
        return REMIXSID;
    }

    public String getHash() throws IOException {

        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpGet httpGet = new HttpGet
                (GET_URL + "al_profile.php?__query=" + GROUP + "&_ref=groups&_tstat=236%2C1%2C38%2C295%2Cgroups_list&al=-1&al_id=133485980&_rndVer=22215");
        httpGet.setConfig(localConfig);
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        Matcher mHash = hash.matcher(responseString);
        if (mHash.find()) {
            POST_HASH = mHash.group(1);
            }
        return POST_HASH;
    }

    public  void sendComment() throws IOException {

        JSONObject json = ReaderJSON.readJsonFromUrl(vkApi+ GROUP +"&count=2&v=5.62");
        Object numberPost = json.getJSONObject("response")
                .getJSONArray("items").getJSONObject(1).get("id");
        Object numberGroup = json.getJSONObject("response")
                .getJSONArray("items").getJSONObject(0).get("owner_id");
        logger.info(numberPost.toString());
        String headerElement = getAutorization();
        String postHash = getHash();
        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpPost httpPost = new HttpPost(GET_URL + "al_wall.php");
        httpPost.setConfig(localConfig);
        httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Cookie", "remixsid=" + headerElement);
        httpPost.addHeader("Origin", "https://vk.com");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("Message", COMMENT));
        urlParameters.add(new BasicNameValuePair("act", "post"));
        urlParameters.add(new BasicNameValuePair("al", "1"));
        urlParameters.add(new BasicNameValuePair("form", ""));
        urlParameters.add(new BasicNameValuePair("hash",postHash));
        urlParameters.add(new BasicNameValuePair("ref", "wall_page"));
        urlParameters.add(new BasicNameValuePair("reply_to", numberGroup.toString() + "_" + numberPost.toString()));
        urlParameters.add(new BasicNameValuePair("reply_to_user", "0"));
        urlParameters.add(new BasicNameValuePair("type", "own"));

        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters,"utf-8");
        httpPost.setEntity(postParams);
        httpPost.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        logger.info("Comments posted. ResponseStatus: "  + httpResponse.getStatusLine().getStatusCode()+ "\n");
        logger.info("Post a comment.txt: " + GET_URL + GROUP + "?w=wall" + numberGroup + "_" + numberPost + "\n" );
        System.out.println(postHash);
        System.out.println("Comments to post in groups. For see details please open log file");
        httpClient.close();
    }


}
