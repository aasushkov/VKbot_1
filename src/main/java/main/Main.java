package main;

import org.apache.http.HeaderElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import vkbot.VKautorize;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {


        VKautorize.sendComment("test");
//        VKCookie.getCookie(new URL("https://login.vk.com"));
    }
}
