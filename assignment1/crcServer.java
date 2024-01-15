import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.*;
import java.net.Socket;
import java.net.ServerSocket;

public class crcServer {
    public static String XOR(String a, String b) {
        String result = "";
        int n = b.length();
        for (int i = 1; i < n; i++) {
            if (a.charAt(i) == b.charAt(i)) {
                result = result + "0";
            } else {
                result = result + "1";
            }
        }
        return result;
    }

    public static String getReminder(String data, String polynomial) {
        int pick = polynomial.length();
        String temp = data.substring(0, pick);
        int n = data.length();
        while (pick < n) {
            if (temp.charAt(0) == '1') {
                temp = XOR(polynomial, temp) + data.charAt(pick);
            } else {
                temp = XOR(new String(new char[pick]).replace("\0", "0"), temp) + data.charAt(pick);
            }
            pick = pick + 1;

        }
        if (temp.charAt(0) == '1') {
            temp = XOR(polynomial, temp);
        } else {
            temp = XOR(new String(new char[pick]).replace("\0", "0"), temp);
        }
        return temp;

    }

    public static void main(String args[]) {
        try {
            System.out.println("Waiting for Clients....");

            // creating server socket
            ServerSocket ss = new ServerSocket(1000);

            // binding with client
            Socket soc = ss.accept();
            System.out.println("connection established ");
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String s = in.readLine();
            System.out.println("Data : " + s);

            FileInputStream fis = new FileInputStream("Polynomial.txt");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder data = new StringBuilder();
            int x;
            while ((x = bis.read()) != -1) {
                data.append((char) x);
            }
            String str = data.toString();
            System.out.println("Polynomial : " + str);
            String result = getReminder(s, str);
            System.out.println("Result : " + result);
            int ans = Integer.parseInt(result);
            if (ans == 0) {
                System.out.println("As division = 0 ,so, Errorless Message ! ");
            } else {
                System.out.println("Erroneous Message !!!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
