import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.*;
import java.net.Socket;
import java.util.*;

class c1 extends Thread {
    public static String GenerateNoise(String message) {
        StringBuilder sb = new StringBuilder(message);

        // Generating Random no:
        Random rand = new Random();
        int a = rand.nextInt(5);
        System.out.println(a);
        System.out.println("No of Error bit generated :" + a);
        for (int i = 0; i < a; i++) {
            Random rand1 = new Random();
            int b = rand1.nextInt(5);
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

    static String add_Binary(String x, String y, int size) {
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

    public String toBCD(int a) {
        String s = Integer.toBinaryString(a);
        StringBuilder sb = new StringBuilder();
        if (s.length() < 16) {
            for (int i = 0; i < 16 - s.length(); i++) {
                sb.append("0");
            }
        }
        sb.append(s);
        String s1 = sb.toString();
        return s1;
    }

    public Vector<String> packeting(String s, int packet_size) {
        Vector<String> v = new Vector<>();
        int size = s.length();
        if (size % packet_size == 0) {

            for (int i = 0; i < size / packet_size; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = packet_size * i; j < (packet_size * i) + packet_size; j++) {
                    sb.append(s.charAt(j));
                }
                String str = sb.toString();
                v.add(str);
            }
        } else {
            for (int i = 0; i < size / packet_size; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = packet_size * i; j < (packet_size * i) + packet_size; j++) {
                    sb.append(s.charAt(j));
                }
                String str = sb.toString();
                v.add(str);
            }
            StringBuilder sb1 = new StringBuilder();
            for (int i = (size / packet_size) * packet_size; i < size; i++) {
                sb1.append(s.charAt(i));
            }
            for (int i = 0; i < packet_size - (size % packet_size); i++) {
                sb1.append("0");
            }
            String str1 = sb1.toString();
            v.add(str1);
        }
        return v;
    }

    public String framing(String s, int size, String head) {

        // initialize the frame size;
        int n = s.length();
        Vector<String> v = new Vector<>();
        if (s.length() % size == 0) {
            for (int i = 0; i < s.length() / size; i++) {
                StringBuilder sb1 = new StringBuilder();
                for (int j = size * i; j < (size * i) + size; j++) {
                    sb1.append(s.charAt(j));
                }
                String str = sb1.toString();
                v.add(str);
            }
        } else {
            for (int i = 0; i < (s.length() / size); i++) {
                StringBuilder sb2 = new StringBuilder();
                for (int j = size * i; j < (size * i) + size; j++) {
                    sb2.append(s.charAt(j));
                }
                String str = sb2.toString();
                Scanner sc = new Scanner(System.in);
                v.add(str);
            }
            StringBuilder sb3 = new StringBuilder();
            for (int i = (s.length() / size) * size; i < s.length(); i++) {
                sb3.append(s.charAt(i));
            }
            for (int i = 0; i < (size - s.length() % size); i++) {
                sb3.append("0");
            }
            String str = sb3.toString();
            v.add(str);

        }
        // Checksum
        String checksum = v.elementAt(0);
        for (int i = 1; i < v.size(); i++) {
            checksum = add_Binary(v.elementAt(i), checksum, size);
        }
        System.out.println("Check Sum is : " + checksum);
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
        System.out.println("Complement CheckSum: " + str1);

        v.add(str1);

        StringBuilder sb1 = new StringBuilder();
        sb1.append(head);
        int a = size;
        sb1.append(toBCD(a));

        sb1.append(".");
        for (int i = 0; i < v.size(); i++) {
            sb1.append(v.get(i));
        }

        String s1 = sb1.toString();

        return s1;
    }

    public void channel() {
        try {
            Random rand = new Random();
            int a = rand.nextInt(1000);
            Thread.sleep(a);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void timer() {
        try {
            int time = 1000;
            Thread.sleep(time);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String generate(String s) {
        StringBuilder sb = new StringBuilder();
        int a = 0;
        while (s.charAt(a) != '.') {
            sb.append(s.charAt(a));
            a++;
        }
        a++;
        StringBuilder sb1 = new StringBuilder();
        for (int i = a; i < s.length(); i++) {
            sb1.append(s.charAt(i));
        }
        String s2 = sb1.toString();
        s2 = GenerateNoise(s2);
        sb.append('.');
        sb.append(s2);
        String s1 = sb.toString();
        return s1;
    }

    public void send(Vector<String> result) throws Exception {
        Socket socket = new Socket("localhost", 12345);

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        for (int i = 0; i < result.size(); i++) {
            System.out.println("Message " + i + " Started sending :");
            String str = result.get(i);
            System.out.println("Do you want to pass through noise : 1)YES 2)NO");
            Scanner sc = new Scanner(System.in);
            int a = sc.nextInt();
            if (a == 1) {
                str = generate(str);
            }
            System.out.println("Message : " + str);
            writer.println(str);
            writer.flush();
            String str1 = " ";
            str1 = bf.readLine();
            // while (str1 != "Successfully send") {
            // System.out.println("Retransmission of frame " + i);
            // str1 = bf.readLine();
            // writer.println(str);
            // }
            System.out.println(str1);
        }

        // Close the resources
        writer.close();
        outputStream.close();
        socket.close();
    }

    public void run() {
        System.out.println("Client Started....");

        try (FileInputStream fis = new FileInputStream("txt1.txt")) {
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuilder sb = new StringBuilder();
            System.out.println("Inside thread");
            int x;

            while ((x = bis.read()) != -1) {
                sb.append((char) x);
            }
            String s = sb.toString();
            System.out.println("message : " + s);
            System.out.println(s);
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter packet Size : ");
            int packet_size = sc.nextInt();
            Vector<String> v = packeting(s, packet_size);
            for (int i = 0; i < v.size(); i++) {
                System.out.println(v.get(i));
            }
            Vector<String> result = new Vector<>();
            System.out.println("Enter the frame size : ");
            int frame_size = sc.nextInt();
            System.out.println("Enter the header  : ");
            String head = sc.next();
            // if header is not 6 bytes or 48 bits
            StringBuilder sb1 = new StringBuilder();
            if (head.length() < 48) {
                for (int i = 0; i < 48 - head.length(); i++) {
                    sb1.append("0");
                }
                for (int i = 0; i < head.length(); i++) {
                    sb1.append(head.charAt(i));
                }
                head = sb1.toString();

            }
            for (int i = 0; i < v.size(); i++) {
                String s1 = framing(v.get(i), frame_size, head);
                channel();
                result.add(s1);
            }
            System.out.println("packets : ");
            send(result);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class client1 {
    public static void main(String args[]) {
        c1 c = new c1();
        c.start();
    }
}
