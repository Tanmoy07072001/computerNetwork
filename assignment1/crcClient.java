import java.util.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class crcClient {
    // method to generate noise
    public static String GenerateNoise(String message) {
        StringBuilder sb = new StringBuilder(message);

        // Generating Random no:
        Random rand = new Random();
        int a = rand.nextInt(10);
        System.out.println(a);
        System.out.println("No of Error bit generated :" + a);
        for (int i = 0; i < a; i++) {
            Random rand1 = new Random();
            int b = rand1.nextInt(10);
            if (sb.charAt(b) == 0) {
                sb.setCharAt(b, '1');
            } else {
                sb.setCharAt(b, '0');
            }
            System.out.println("Error generated at " + b + "th position");
        }

        String s = sb.toString();
        return s;
    }

    public static int toInt(String str) {
        int sum = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            double a = Integer.parseInt(String.valueOf(c));
            sum = sum + (int) ((sum + Math.pow(2, (str.length() - i - 1))) * a);
        }

        return sum;
    }

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
            // Reading Data
            FileInputStream fis1 = new FileInputStream("Text1.txt");
            BufferedInputStream bis1 = new BufferedInputStream(fis1);
            StringBuilder data = new StringBuilder();
            // creating socket
            Socket soc = new Socket("Localhost", 1000);

            int x;
            while ((x = bis1.read()) != -1) {
                data.append((char) x);
            }
            String str1 = data.toString();
            System.out.println("Data Bits : " + str1);
            // Reading Polynomial
            FileInputStream fis2 = new FileInputStream("Polynomial.txt");
            BufferedInputStream bis2 = new BufferedInputStream(fis2);
            StringBuilder polynomial = new StringBuilder();

            while ((x = bis2.read()) != -1) {
                polynomial.append((char) x);
            }
            String str2 = polynomial.toString();
            System.out.println("Polynomial Bits : " + str2);

            // adding 0 acc to polynimial
            for (int i = 0; i < polynomial.length() - 1; i++) {
                data.append('0');
            }
            String d = data.toString();
            System.out.println("after adding 0 with data : " + d);

            String reminder = getReminder(d, str2);
            String message = str1 + reminder;

            // Adding noise
            System.out.println("Do you want to add noise : 1)YES 2)NO");
            Scanner sin = new Scanner(System.in);
            int n = sin.nextInt();
            if (n == 1) {
                message = GenerateNoise(message);
            }

            System.out.println("Message that I'm sending from Client : " + message);

            // code to send data to server
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

            // // sending it to the server
            out.println(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
