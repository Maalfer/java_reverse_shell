import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is ready and waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private String currentDirectory;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.currentDirectory = System.getProperty("user.home");
        }

        @Override
        public void run() {
            try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                out.println("Server is ready");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equals("exit")) {
                        break;
                    } else if (inputLine.startsWith("cd")) {
                        handleCdCommand(inputLine, out);
                    } else {
                        runCommand(inputLine, out);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleCdCommand(String cdCommand, PrintWriter out) {
            String[] commandParts = cdCommand.split(" ", 2);
            if (commandParts.length == 2) {
                String newDirectory = commandParts[1].trim();
                File newDir = new File(currentDirectory, newDirectory);
                if (newDir.isDirectory()) {
                    currentDirectory = newDir.getAbsolutePath();
                    out.println("Changed directory to: " + currentDirectory);
                    out.println("EndOfCdCommand");
                } else {
                    out.println("Directory not found: " + newDir.getAbsolutePath());
                }
            } else {
                out.println("Invalid cd command format");
            }
        }

        private void runCommand(String command, PrintWriter out) {
            try {
                Process process = Runtime.getRuntime().exec(command, null, new File(currentDirectory));
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        out.println(line);
                    }
                    out.println("EndOfOutput");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
