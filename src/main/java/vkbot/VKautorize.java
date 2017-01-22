package vkbot;

import org.apache.http.*;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VKautorize {

	private static final String USER_AGENT = "Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

	private static final String GET_URL = "https://vk.com/";
	private static final String POST_URL = "https://login.vk.com/?act=login";
	private static final String LOGIN_URL = "https://vk.com/login.php?act=slogin&to=&s=1&__q_hash=";
	private static final String EMAIL = "***";
	private static final String PASS = "***";
	private static String IP_H = "";
	private static String LG_H = "";
	private static String Q_HASH = "";
	private static String REMIXSID = "";
	private static final HttpHost proxyHost = new HttpHost("127.0.0.1", 8889);

	private static final String patternLG_H = "<input type=\"hidden\" name=\"lg_h\" value=\"((\\w)*)\"";
	private static final String patternIP_H = "<input type=\"hidden\" name=\"ip_h\" value=\"((\\w)*)\"";
	private static final String patternRemixlhk = "remixlhk=((\\w)*)";
	private static final String patternQ_Hash = "__q_hash=((\\w)*)";
	private static final String patternRemixsid = "remixsid=((\\w)*);";


	private static final Pattern lg_H = Pattern.compile(patternLG_H);
	private static final Pattern ip_H = Pattern.compile(patternIP_H);
	private static final Pattern remixlhk = Pattern.compile(patternRemixlhk);
	private static final Pattern q_hash = Pattern.compile(patternQ_Hash);
	private static final Pattern remixsid = Pattern.compile(patternRemixsid);

	private  static final RequestConfig globalConfig = RequestConfig.custom()
			.setCookieSpec(CookieSpecs.DEFAULT)
			.setRedirectsEnabled(false)
			.setProxy(proxyHost)
			.build();

	public static CloseableHttpResponse sendGET() throws IOException {
			CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(globalConfig)
				.build();
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
				.setCookieSpec(CookieSpecs.STANDARD)
				.build();
		HttpGet httpGet = new HttpGet(GET_URL);
		httpGet.setConfig(localConfig);
		httpGet.addHeader("User-Agent", USER_AGENT);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		System.out.println("GET Response Status:: "
				+ httpResponse.getStatusLine().getStatusCode());
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
		httpClient.close();
		reader.close();
		System.out.println("IP_H  " + IP_H);
		System.out.println("LG_H  " + LG_H);
		return httpResponse;
	}

	public static void sendPOST(CloseableHttpResponse closeableHttpResponse) throws IOException {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(globalConfig)
				.build();
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
		System.out.println("POST Response Status:: "
				+ httpResponse.getStatusLine().getStatusCode());
		Header headers = httpResponse.getLastHeader("Location");
		Matcher m3 = q_hash.matcher(headers.getValue());
		if (m3.find()) {
			Q_HASH = m3.group(1);
		}
		httpClient.close();
		System.out.println("Q_HASH: " + Q_HASH);
	}

	public static void getAutorization() throws IOException {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(globalConfig)
				.build();
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
				.setCookieSpec(CookieSpecs.STANDARD)
				.build();
		HttpGet httpGet = new HttpGet(LOGIN_URL+ Q_HASH);
		httpGet.setConfig(localConfig);
		httpGet.addHeader("User-Agent", USER_AGENT);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		System.out.println("GET Response Status:: "
				+ httpResponse.getStatusLine().getStatusCode());
		Header[] headers = httpResponse.getAllHeaders();
		Matcher m = remixsid.matcher(headers[9].getValue());
		if (m.find()) {
			REMIXSID = m.group(1);
		}
		httpClient.close();
		System.out.println("REMIXSID: " + REMIXSID);
	}
}
