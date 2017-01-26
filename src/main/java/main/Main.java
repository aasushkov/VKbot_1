package main;

import org.apache.http.HeaderElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import vkbot.VKautorize;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner in = new Scanner(System.in);
        System.out.print("Enter VK Login: ");
        String LOGIN = in.nextLine();
        System.out.print("Enter VK Password: ");
        String PASS = in.nextLine();
        System.out.print("Enter VK Group: ");
        String GROUP = in.nextLine();
        System.out.print("Enter Text Comment: ");
        String COMMENT = in.nextLine();

        VKautorize vKautorize = new VKautorize(LOGIN,PASS,GROUP,COMMENT);

        vKautorize.sendComment();

           }
}
