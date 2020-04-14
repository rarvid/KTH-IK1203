/*Rudolfs Arvids Kalnins
 * TCOMK2 2019/2020
 * KTH
 */
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HTTPEcho {
    public static void main(String[] args) throws IOException {
        int BUFFERSIZE = 64000;
        int TIMEOUT = 1000;
        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Server started on localhost port :" + port);

        try
        {

            while(true)
            {

                byte[] fromClient = new byte[BUFFERSIZE];
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(TIMEOUT);
                System.err.println("Connection accepted from client");

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                try {

                    int b;
                    int count = 0;
                    int endingCounter = 0;
                    while (true)
                    {

                        b = in.read();
                        fromClient[count] = (byte) b;
                        System.out.println("Read byte:" + b);
                        count++;

                        if(b == 10 || b == 13)
                        {
                            endingCounter++;
                        }else
                            {
                                endingCounter = 0;
                            }
                        if(endingCounter == 4 || b == -1)
                            break;
                    }
                }catch (SocketTimeoutException e)
                {
                    System.err.println("Socket timed out");
                }

                String input1 = new String(fromClient, StandardCharsets.UTF_8).trim();
                String input2 = "HTTP/1.1 200 OK\r\n\r\n";
                String output = input2 + input1;

                byte[] toClient = output.getBytes(StandardCharsets.UTF_8);

                out.write(toClient);
                in.close();
                out.close();
                clientSocket.close();

                System.err.println("Client Socket closed");
            }
        }

        catch(UnknownHostException e)
        {
            System.out.println("Could not resolve localhost address");
        }
    }
}