package main;

import vkbot.VKautorize;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        VKautorize vKautorize;

        Scanner in = new Scanner(System.in);
        System.out.print("Enter VK Login: ");
        String LOGIN = in.nextLine();
        System.out.print("Enter VK Password: ");
        String PASS = in.nextLine();
        String GROUP;
        String COMMENT;
        String s;
        BufferedReader brComment = new BufferedReader
                (new InputStreamReader(new FileInputStream("comment.txt"), "CP1251"));
        StringBuffer commentBuffer = new StringBuffer();
        while ((s = brComment.readLine()) != null) {
            commentBuffer.append(s);
        }
        COMMENT = commentBuffer.toString();


        BufferedReader brGroupList = new BufferedReader
                (new InputStreamReader(new FileInputStream("groupList.txt"), "CP1251"));
        while ((GROUP = brGroupList.readLine()) != null) {
            vKautorize = new VKautorize(LOGIN, PASS, GROUP, COMMENT);
            vKautorize.sendComment();
        }

        brGroupList.close();
    }

}
