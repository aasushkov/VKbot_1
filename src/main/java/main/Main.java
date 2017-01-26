package main;

import vkbot.VKautorize;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
                (new InputStreamReader(new FileInputStream("comment.txt")));
        StringBuilder commentBuffer = new StringBuilder();
        while ((s = brComment.readLine()) != null) {
            commentBuffer.append(s);
        }
        COMMENT = commentBuffer.toString();
        brComment.close();


        BufferedReader brGroupList = new BufferedReader
                (new InputStreamReader(new FileInputStream("groupList.txt")));
        while ((GROUP = brGroupList.readLine()) != null) {
            vKautorize = new VKautorize(LOGIN, PASS, GROUP, COMMENT);
            vKautorize.sendComment();
        }

        brGroupList.close();
    }
}
