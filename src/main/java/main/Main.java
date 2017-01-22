package main;

import org.apache.http.client.methods.CloseableHttpResponse;
import vkbot.VKCookie;
import vkbot.VKautorize;

import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {

       CloseableHttpResponse httpResponse = VKautorize.sendGET();
//       VKCookie.getCookie(new URL("https://vk.com"));
        VKautorize.sendPOST(httpResponse);
        VKautorize.getAutorization();
//        VKCookie.getCookie(new URL("https://login.vk.com"));
    }
}
