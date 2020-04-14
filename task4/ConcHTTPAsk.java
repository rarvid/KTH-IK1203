
import java.io.*;
import java.net.*;

public class ConcHTTPAsk {

    public static void main(String [] args) throws IOException
    {
        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = new ServerSocket(port);

        System.err.println("Server started on localhost port :" + port);

        while(true)
        {
            try
            {
                Socket clientSocket = serverSocket.accept();
                new Thread(new HTTPAsk(clientSocket)).start();
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }
}