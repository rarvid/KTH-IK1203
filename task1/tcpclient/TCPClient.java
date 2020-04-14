/*Rudolfs Arvids Kalnins
* TCOMK2 2019/2020
* KTH
*/
package tcpclient;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TCPClient
{
    private static int BUFFERSIZE = 64000;
    private static int TIMEOUT = 3000;

    public static String askServer(String hostname, int port, String ToServer) throws  IOException
    {
        if(ToServer == null)
        {
            return askServer(hostname, port);
        } else {
            byte[] fromServer = new byte[BUFFERSIZE];
            Socket clientSocket = new Socket(hostname, port);
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            clientSocket.setSoTimeout(TIMEOUT);
            try {
                ToServer += "\n";
                byte[] encoded = ToServer.getBytes(StandardCharsets.UTF_8);
                out.write(encoded, 0, encoded.length);
                int b = 0;
                while (true) {
                    b = in.read(fromServer, 0, BUFFERSIZE);
                    if (b == -1) {
                        break;
                    }
                }
            } catch (SocketTimeoutException e) {
                return new String(fromServer, StandardCharsets.UTF_8).trim();
            } finally {
                in.close();
                out.close();
                clientSocket.close();
            }
            return new String(fromServer, StandardCharsets.UTF_8).trim();
        }
    }

    public static String askServer(String hostname, int port) throws  IOException
    {
        byte[] fromServer = new byte[BUFFERSIZE];
        Socket clientSocket = new Socket(hostname, port);
        InputStream in = clientSocket.getInputStream();
        clientSocket.setSoTimeout(TIMEOUT);
        try
        {
            int b = 0;
            while (true) {
                b = in.read(fromServer, 0, fromServer.length);
                if (b == -1)
                {
                    break;
                }
            }
        }
        catch(SocketTimeoutException e)
        {
            return new String(fromServer, StandardCharsets.UTF_8).trim();
        }
        finally {
            in.close();
            clientSocket.close();
        }
        return new String(fromServer, StandardCharsets.UTF_8).trim();
    }
}