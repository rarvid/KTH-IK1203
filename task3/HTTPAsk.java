import tcpclient.TCPClient;

import java.net.*;
import java.io.*;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {
    public static void main( String[] args) throws IOException {
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

                String input = new String(fromClient, StandardCharsets.UTF_8).trim();
                String code200OK = "HTTP/1.1 200 OK\r\n\r\n";
                String code404NotFound = "HTTP/1.1 404 Not Found\r\n\r\n";
                String code400BadRequest = "HTTP/1.1 400 Bad Request\r\n\r\n";
                String[] request = input.split(" ");
                if(request.length > 1  && request[1].contains("ask?"))
                {
                    String[] query = request[1].split("\\?");
                    String askHostname = "";
                    int askPort = 0;
                    String askString = "";
                    if (query[1].contains("hostname") && query[1].contains("port") && query[1].contains("string"))
                    {
                        String[] arguments = query[1].split("&");
                        for(int i = 0; i < arguments.length; i++)
                        {
                            if(arguments[i % arguments.length].contains("hostname"))
                                askHostname = arguments[i % arguments.length].split("=")[1];

                            if (arguments[(i + 1) % arguments.length].contains("port"))
                                askPort = Integer.parseInt(arguments[(i + 1) % arguments.length].split("=")[1]);

                            if(arguments[(i + 2) % arguments.length].contains("string"))
                                askString = arguments[(i + 2) % arguments.length].split("=")[1];

                            askString = askString.replaceAll("%20" , " ");
                        }
                        try
                        {
                            String output = TCPClient.askServer(askHostname, askPort, askString);
                            byte[] toClient = (code200OK + output).getBytes(StandardCharsets.UTF_8);
                            out.write(toClient);
                        }catch(UnknownHostException e)
                        {
                            byte[] toClient = code404NotFound.getBytes(StandardCharsets.UTF_8);
                            out.write(toClient);
                        }
                    }
                    else if(query[1].contains("hostname") && query[1].contains("port"))
                            {
                                String[] arguments = query[1].split("&");
                                for(int i = 0; i < arguments.length; i++)
                                {
                                    if(arguments[i % arguments.length].contains("hostname"))
                                        askHostname = arguments[i % arguments.length].split("=")[1];

                                    if (arguments[(i + 1) % arguments.length].contains("port"))
                                        askPort = Integer.parseInt(arguments[(i + 1) % arguments.length].split("=")[1]);
                                }
                                try
                                {
                                    String output = TCPClient.askServer(askHostname, askPort, askString);
                                    byte[] toClient = (code200OK + output).getBytes(StandardCharsets.UTF_8);
                                    out.write(toClient);
                                }catch(UnknownHostException e)
                                {
                                    byte[] toClient = code404NotFound.getBytes(StandardCharsets.UTF_8);
                                    out.write(toClient);
                                }
                            }
                             else
                                 {
                                     byte[] toClient =code400BadRequest.getBytes(StandardCharsets.UTF_8);
                                     out.write(toClient);
                                 }
                }
                else
                {
                    byte[] toClient =code400BadRequest.getBytes(StandardCharsets.UTF_8);
                    out.write(toClient);
                }
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

