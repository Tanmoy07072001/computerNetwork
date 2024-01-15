import java.util.*;

import javax.print.DocFlavor.STRING;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

class c1 extends Thread {

    static String add_Binary(String x, String y, int size) {
        x = x.replaceFirst("^0+(?!$)", ""); // Remove leading zeros
        y = y.replaceFirst("^0+(?!$)", "");

        if (x.isEmpty())
            x = "0"; // Ensure non-empty binary string
        if (y.isEmpty())
            y = "0";

        int num1 = Integer.parseInt(x, 2);
        int num2 = Integer.parseInt(y, 2);

        int sum = num1 + num2;
        String result = Integer.toBinaryString(sum);
        if (result.length() > size) {
            result = result.substring(1);
            int num3 = Integer.parseInt(result, 2);
            num3 = num3 + 1;
            result = Integer.toBinaryString(num3);
        }
        if (result.length() < size) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size - result.length(); i++) {
                sb.append("0");
            }
            sb.append(result);
            result = sb.toString();
        }
        return result;
    }

    public String extract(String s) {
        int i = 0;
        while (i < s.length()) {
            i++;
            if (s.charAt(i) == '.') {
                break;
            }

        }
        i = i + 1;
        StringBuilder sb = new StringBuilder();
        for (int j = i; j < s.length(); j++) {
            sb.append(s.charAt(j));
        }
        String s1 = sb.toString();
        return s1;
    }

    public int getLength(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 48; i < (48 + 16); i++) {
            sb.append(s.charAt(i));
        }
        String s1 = sb.toString();

        int decimal = Integer.parseInt(s1, 2);

        return decimal;
    }

    public long checkSum(String s, int size) {
        Vector<String> v = new Vector<>();
        for (int i = 0; i < s.length() / size; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = size * i; j < size * (i + 1); j++) {
                sb.append(s.charAt(j));
            }
            String s1 = sb.toString();
            System.out.println(s1);
            v.add(s1);
        }

        String result = v.get(0);
        for (int i = 1; i < v.size(); i++) {
            result = add_Binary(v.get(i), result, size);
        }
        StringBuilder sb6 = new StringBuilder();
        sb6.append(result);
        for (int i = 0; i < sb6.length(); i++) {
            if ((sb6.charAt(i)) == '1') {
                sb6.setCharAt(i, '0');
            } else {
                sb6.setCharAt(i, '1');
            }
        }
        String str1 = sb6.toString();
        long ans = Long.parseLong(str1);

        return ans;
    }

    public void run() {
        System.out.println("Inside thread");
        try {
            ServerSocket serverSocket = new ServerSocket(12345);

            System.out.println("Server is waiting for connections...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            String message;

            while ((message = reader.readLine()) != null) {
                System.out.println("NEW FRAME ARRIVED : ");
                System.out.println("frame size : " + getLength(message));
                System.out.println("Received: " + message);
                System.out.println("Message Bytes : " + extract(message));
                String s1 = (extract(message));
                int frame_size = getLength(message);
                long result = checkSum(s1, frame_size);
                System.out.println("Check sum : " + result);
                if (result == 0) {
                    out.println("Successfully send");
                } else {
                    out.println("Errorful Message");
                }
                out.flush();
                System.out.println(" ");
            }

            reader.close();
            clientSocket.close();
            serverSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class server {
    public static void main(String args[]) {
        c1 c = new c1();
        c.start();
    }
}
