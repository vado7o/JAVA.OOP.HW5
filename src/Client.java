import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String request;

        try (Socket socket = new Socket("localhost", 1234)) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                System.out.println("Введите математическое действие для вычисления, например '-6 * (22 + 88) / 11 + 4 - 10': ");
                request = scanner.nextLine();
                dataOutputStream.writeUTF(request);
                if("end".equals(request)) break;
                System.out.println("Результат вычисления на сервере: " + dataInputStream.readUTF());

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
