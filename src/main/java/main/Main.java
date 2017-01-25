package main;

import vkbot.VKautorize;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        VKautorize vKautorize = null;

        Scanner in = new Scanner(System.in);
        System.out.print("Enter VK Login: ");
        String LOGIN = in.nextLine();
        System.out.print("Enter VK Password: ");
        String PASS = in.nextLine();
        String GROUP;
        System.out.print("Enter Comment: ");
        String COMMENT = in.nextLine();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("groups.txt"),"CP1251"));
        while((GROUP = br.readLine()) != null) {
            vKautorize = new VKautorize(LOGIN,PASS,GROUP,COMMENT);
            vKautorize.sendComment();
        }

        br.close();

    }
}
