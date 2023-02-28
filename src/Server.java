import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Сервер запущен, ожидает соединения...");
            Socket socket = serverSocket.accept();
            System.out.println("Клиент подключился!");
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                String clientRequest = dataInputStream.readUTF();
                System.out.println("Клиент прислал: " + clientRequest);
                // Начинаем процесс логирования:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Для вычисления получено выражение: " + clientRequest);
                String result = calculate(clientRequest).toString();
                stringBuilder.append("\nВычисление от сервера: " + result + "\n\n");
                try (FileWriter fileWriter = new FileWriter("log.txt", true)) {
                    fileWriter.write(stringBuilder.toString());
                }
                //Процесс логирования окончен
                dataOutputStream.writeUTF(""+ result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Integer calculate(String example) {
        //String example = "- 100 + 27/(28-19) -6 + 12 * (15 - 3) + 6 *7 + (15 /3)";
        String first;
        String second;

        example = example.replaceAll(" ", "");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(example);
        while (m.find()) {
            if (m.group(1).contains("+")) example = example.replace(m.group(1), (operate(m.group(1).split("\\+")[0].trim(), m.group(1).split("\\+")[1].trim(), "+")).toString());
            else if (m.group(1).contains("-")) example = example.replace(m.group(1), (operate(m.group(1).split("\\-")[0].trim(), m.group(1).split("\\-")[1].trim(), "-").toString()));
            else if (m.group(1).contains("/")) example = example.replace(m.group(1), (operate(m.group(1).split("\\/")[0].trim(), m.group(1).split("\\/")[1].trim(), "/").toString()));
            else if (m.group(1).contains("*")) example = example.replace(m.group(1), (operate(m.group(1).split("\\*")[0].trim(), m.group(1).split("\\*")[1].trim(), "*").toString()));
        }
        example = example.replaceAll("\\(", "").replaceAll("\\)", "");
        while (example.contains("*")) {
            first = example.split("\\*")[0].trim();
            second = example.split("\\*")[1].trim();
            int sum = Integer.parseInt(extractNum(first, 1)) * Integer.parseInt(extractNum(second, 2));
            StringBuilder sb_new = new StringBuilder().append(sum);
            StringBuilder sb_old = new StringBuilder().append(extractNum(first, 1)).append("\\*").append(extractNum(second, 2));
            example = example.replaceAll(sb_old.toString(), sb_new.toString());
        }
        while (example.contains("/")) {
            first = example.split("/")[0].trim();
            second = example.split("/")[1].trim();
            int sum = Integer.parseInt(extractNum(first, 1)) / Integer.parseInt(extractNum(second, 2));
            StringBuilder sb_new = new StringBuilder().append(sum);
            StringBuilder sb_old = new StringBuilder().append(extractNum(first, 1)).append("/").append(extractNum(second, 2));
            example = example.replaceAll(sb_old.toString(), sb_new.toString());
        }
        int i = 0;
        List<Integer> list = new ArrayList<>();
        String temp = "";
        String op = "";
        List<Character> digits = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        while (i < example.length()) {
            if (i == 0 && !digits.contains(example.charAt(i))) {
                op = String.valueOf(example.charAt(i));
                i++;
                continue;
            }
            if (i == example.length() - 1) {
                temp += example.charAt(i);
                if (Objects.equals(op, "-")) {
                    list.add(-Integer.parseInt(temp));
                } else list.add(Integer.parseInt(temp));
                break;
            }
            if (digits.contains(example.charAt(i))) {
                temp += example.charAt(i);
            }
            else {
                if (op.equals("-")) {
                    list.add(-Integer.parseInt(temp));
                } else list.add(Integer.parseInt(temp));
                op = String.valueOf(example.charAt(i));
                temp = "";
            }
            i++;
        }
        Integer result = 0;
        for (Integer num: list) {
            result += num;
        }
        System.out.println(result);
        return result;
    }

    public static String extractNum(String num, int order) {
        List<Character> digits = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '~');
        int i;
        if (order == 1) i = num.length() - 1;
        else i = 0;
        String temp = "";
        if (order == 1) {
            while (i > -1 && digits.contains(num.charAt(i))) {
                temp += num.charAt(i);
                i--;
            }
            num = new StringBuilder(temp).reverse().toString();
        } else {
            while (i < num.length() && digits.contains(num.charAt(i))) {
                temp += num.charAt(i);
                i++;
            }
            num = temp;
        }
        return num;
    }

    public static Integer operate(String first, String second, String op) {
        try {
            return switch (op) {
                case "+" -> Integer.parseInt(first) + Integer.parseInt(second);
                case "-" -> Integer.parseInt(first) - Integer.parseInt(second);
                case "/" -> Integer.parseInt(first) / Integer.parseInt(second);
                case "*" -> Integer.parseInt(first) * Integer.parseInt(second);
                default -> 0;
            };
        } catch (Exception e) {
            System.out.println("Ошибка!!! Возможно введён неверный формат выражения!!!");
            e.printStackTrace();
        }
        return 0;
    }
}
