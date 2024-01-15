import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class server {
    static String add_Binary(String x, String y) {
        int num1 = Integer.parseInt(x, 2);
        int num2 = Integer.parseInt(y, 2);
        int sum = num1 + num2;
        String result = Integer.toBinaryString(sum);
        if (result.length() > 16) {
            result = result.substring(1);
            int num3 = Integer.parseInt(result, 2);
            num3 = num3 + 1;
            result = Integer.toBinaryString(num3);
        }
        if (result.length() < 16) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16 - result.length(); i++) {
                sb.append("0");
            }
            sb.append(result);
            result = sb.toString();
        }
        return result;
    }

    public static void main(String args[]) {
        try {
            System.out.println("Waiting for Clients....");

            // creating server socket
            ServerSocket ss = new ServerSocket(1138);

            // binding with client
            Socket soc = ss.accept();
            System.out.println("connection established ");

            // code to get data from client
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String s = in.readLine();
            System.out.println(s);

            Vector<String> v = new Vector<>();
            int substringCount = (int) Math.ceil(s.length() / 16.0);
            for (int i = 0; i < substringCount; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = (16 * i); j < Math.min(16 * (i + 1), s.length()); j++) {
                    sb.append(s.charAt(j));
                }
                String s1 = sb.toString();
                v.add(s1);
            }
            for (int i = 0; i < v.size(); i++) {
                System.out.println(v.elementAt(i));
            }
            String checksum = v.elementAt(0);
            for (int i = 1; i < v.size(); i++) {
                checksum = add_Binary(v.elementAt(i), checksum);
            }
            // complementing checksum digit
            StringBuilder sb6 = new StringBuilder();
            sb6.append(checksum);
            for (int i = 0; i < sb6.length(); i++) {
                if ((sb6.charAt(i)) == '1') {
                    sb6.setCharAt(i, '0');
                } else {
                    sb6.setCharAt(i, '1');
                }
            }
            checksum = sb6.toString();
            System.out.println("Check Sum : " + checksum);

            Long l = Long.parseLong(checksum);
            if (l == 0) {
                System.out.println("Errorless Message");
            } else {
                System.out.println("Erroneous Message");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
