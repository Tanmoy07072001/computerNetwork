import java.util.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
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

    // method that will convert every 1bit decimal to 4 bit decimal
    public static String toBCD(int num) {
        if (num == 0) {
            return "0000";
        } else {
            String BCD = "";
            while (num != 0) {
                int t = num % 10;
                String tBCD = Integer.toBinaryString(t);
                while (tBCD.length() < 4) {
                    tBCD = "0" + tBCD;
                }
                BCD = tBCD + BCD;
                num /= 10;
            }
            return BCD;
        }
    }

    // method for add Binary acc to checksum rule
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
        return result;
    }

    public static void main(String args[]) {
        try {

            System.out.println("Client Started....");

            FileInputStream fis = new FileInputStream("Text.txt");
            BufferedInputStream bis = new BufferedInputStream(fis);

            StringBuilder sb = new StringBuilder();

            // The data that I will send :
            int x;
            System.out.println("Date that I send from client : ");
            while ((x = bis.read()) != -1) {
                sb.append(x);
            }
            String s = sb.toString();
            System.out.println(s);

            // creating socket
            Socket soc = new Socket("Localhost", 1138);

            // converting length of 4 to further convert it 16 bit
            Vector<String> v = new Vector<>();
            if (s.length() % 4 == 0) {
                for (int i = 0; i < s.length() / 4; i++) {
                    StringBuilder sb1 = new StringBuilder();
                    for (int j = 4 * i; j < (4 * i) + 4; j++) {
                        sb1.append(s.charAt(j));
                    }
                    String str = sb1.toString();
                    v.add(str);
                }
            } else {
                for (int i = 0; i < (s.length() / 4); i++) {
                    StringBuilder sb2 = new StringBuilder();
                    for (int j = 4 * i; j < (4 * i) + 4; j++) {
                        sb2.append(s.charAt(j));
                    }
                    String str = sb2.toString();
                    v.add(str);
                }
                StringBuilder sb3 = new StringBuilder();
                for (int i = (s.length() / 4) * 4; i < s.length(); i++) {
                    sb3.append(s.charAt(i));
                }
                for (int i = 0; i < (4 - s.length() % 4); i++) {
                    sb3.append("0");
                }
                String str = sb3.toString();
                v.add(str);

            }
            for (int i = 0; i < v.size(); i++) {
                System.out.println(v.get(i));
            }

            // Converting 16 digit BCD & creating a single string of 16bit BCDs
            Vector<String> v1 = new Vector<>();
            StringBuilder sb1 = new StringBuilder();
            for (int i = 0; i < v.size(); i++) {
                StringBuilder sb2 = new StringBuilder();
                String s1 = v.get(i);
                for (int j = 0; j < s1.length(); j++) {
                    int a = Character.getNumericValue(s1.charAt(j));
                    System.out.println(a);
                    String s2 = toBCD(a);
                    System.out.println(s2);
                    sb1.append(s2);
                    sb2.append(s2);
                }
                String s2 = sb2.toString();
                v1.add(s2);
            }
            System.out.println("Group of 16 Bit data : ");
            for (int i = 0; i < v1.size(); i++) {
                System.out.println(v1.elementAt(i));
            }

            // Checksum
            String checksum = v1.elementAt(0);
            for (int i = 1; i < v1.size(); i++) {
                checksum = add_Binary(v1.elementAt(i), checksum);
            }

            // if checksum is not a 16 bit no then we need to convert it to a 16 bit no
            if (checksum.length() < 16) {
                StringBuilder sb4 = new StringBuilder();
                for (int i = 0; i < 16 - checksum.length(); i++) {
                    sb4.append("0");
                }
                sb4.append(checksum);
                checksum = sb4.toString();
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
            String str1 = sb6.toString();
            System.out.println("CheckSum: " + str1);

            // creating string to sent to server
            StringBuilder sb8 = new StringBuilder();
            for (int i = 0; i < v1.size(); i++) {
                sb8.append(v1.elementAt(i));
            }
            sb8.append(str1);
            String string = sb8.toString();

            // adding noise
            System.out.println("Do you want to add noise : 1)YES 2)NO");
            Scanner sin = new Scanner(System.in);
            int n = sin.nextInt();
            if (n == 1) {
                string = GenerateNoise(string);
            }

            System.out.println("Message that I'm sending from Client : " + string);

            // code to send data to server
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

            // // sending it to the server
            out.println(string);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
