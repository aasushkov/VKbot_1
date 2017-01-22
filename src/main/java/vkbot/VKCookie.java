package vkbot;

import java.io.IOException;
import java.net.*;
import java.net.CookieStore;
import java.util.List;

public class VKCookie {


    public static List<HttpCookie> getCookie(URL url) throws IOException {

        List<HttpCookie> cookies;
        CookieStore cookieJar = null;

        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            URLConnection connection = url.openConnection();
            connection.getContent();

            cookieJar = manager.getCookieStore();
            cookies =
                    cookieJar.getCookies();
            for (HttpCookie cookie : cookies) {
                System.out.println("CookieHandler retrieved cookie: " + cookie);
            }
        } catch (Exception e) {
            System.out.println("Unable to get cookie using CookieHandler");
            e.printStackTrace();
        }


        return cookieJar.getCookies();
    }


}
