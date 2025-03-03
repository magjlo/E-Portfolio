import java.net.*;
import java.nio.charset.StandardCharsets;

import javax.imageio.IIOException;

import java.io.*;

public class HTTPAsk {

    static boolean shutdown = false;
    static Integer timeout = null;
    static Integer limit = null;
    static String hostname = null;
    static int port = 0;
    static byte[] usinbytes = new byte[0];
    static int buffersize = 1024;
    static int fkuduplicate = 0;

    public static void main(String[] args) {
        
        //Create the server socket.
        ServerSocket welSocket = null;

        try{
            int serverport = Integer.parseInt(args[0]);
            welSocket = new ServerSocket(serverport);
        }catch(IOException | NumberFormatException e){
            System.err.println("Invalid port for HTTPAsk. Possible failure to open socket." + e);
            System.exit(1);
        };
    

        //Server loop
        while(true){
            
            //initialize variables.
            int i = 0;
            ByteArrayOutputStream dynarray = new ByteArrayOutputStream();
            byte[] clientbytes = new byte[buffersize];
            byte[] temp = new byte[0];
            Socket conSocket = null;
            InputStream input = null;

            //Waits for connection and reads the input
            try{
                conSocket = welSocket.accept();
                input = conSocket.getInputStream();
                
                //Reads the bytes from the inputstream into an array until emptyline
                
                while((i = input.read(clientbytes)) != -1){
                    dynarray.write(clientbytes, 0, i);
                    temp = dynarray.toByteArray();
                    String ss = new String(temp, StandardCharsets.UTF_8);
                    
                    //Check if GET has ended.
                    if(ss.contains("\r\n\r\n")){
                        conSocket.shutdownInput();
                        break;
                    }
                    else{
                        continue;
                    }
                }
            }catch(IOException e){
                try{
                    conSocket.getOutputStream().write("Failure to connect to client and read client data (IOException).".getBytes(StandardCharsets.UTF_8));
                    conSocket.close();
                    continue;
                }catch(IOException a){
                    System.err.println(e);
                    System.exit(1);
                }
            }
            
            
            /////////////////////////////////////////////////////////////////////////////////////
            
            //Extracts GET request from Input bytes. Creates stringbuilder for TCPClient.
            String s1 = new String(temp, StandardCharsets.UTF_8);

            //Prevent duplicate response, remove??
            if(s1.contains("favicon") == false){
                
                try{
                    s1 = s1.substring(0, s1.indexOf("\r\n"));
                }catch(StringIndexOutOfBoundsException e){
                    try{
                        conSocket.getOutputStream().write("Error 400, Bad request.".getBytes(StandardCharsets.UTF_8));
                        conSocket.close();
                        continue;
                    }catch(IOException a){
                        System.err.println(a);
                        System.exit(1);
                    }
                }

                if((s1.substring(0, 8)).contains("GET /ask") == false){
                    try{
                        conSocket.getOutputStream().write("Error 400, Bad request. URL does not contain valid /ask.".getBytes(StandardCharsets.UTF_8));
                        conSocket.close();
                        continue;
                    }catch(IOException e){
                        System.err.println(e);
                        System.exit(1);
                    }
                }
                System.out.println(s1);
                s1 = s1.substring(s1.indexOf(" ")+1, s1.lastIndexOf(" "));

                StringBuilder builder = new StringBuilder();
                String[] querylines = s1.split("&");
                String[] spair = new String[0];
                int f = 0;
                boolean hashostname = false;
                boolean hasport = false;
                
                try{
                    //parse arguments
                    for(String qline : querylines){
                        if(qline.contains("hostname=")){
                            spair = qline.split("=");
                            hostname = spair[1];
                            hashostname = true;
                        }
                        else if(qline.contains("port=")){
                            spair = qline.split("=");
                            port = Integer.parseInt(spair[1]);
                            hasport = true;
                        }
                        else if(qline.contains("limit=")){
                            spair = qline.split("=");
                            limit = Integer.parseInt(spair[1]);
                        }
                        else if(qline.contains("shutdown=")){
                            spair = qline.split("=");
                            if(spair[1].toLowerCase().contains("true")) shutdown = true;
                        }
                        else if(qline.contains("timeout=")){
                            spair = qline.split("=");
                            timeout = Integer.parseInt(spair[1]);
                        }
                        else if(qline.contains("=")){
                            if(f == 0) f = 1;
                            else{
                                builder.append(" ");
                            }
                            spair = qline.split("=");
                            builder.append(spair[1]);
                        }
                    }
                }catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
                    try{
                        conSocket.getOutputStream().write("Error 400. Bad request, check query validity (NumberFormatException).".getBytes(StandardCharsets.UTF_8));
                        conSocket.close();
                        continue;
                    }catch(IOException a){
                        System.err.println(e);
                        System.exit(1);
                    }
                }
            
                builder.append("\n");
                usinbytes = builder.toString().getBytes(StandardCharsets.UTF_8);
                //
            
                if(hasport == false || hashostname == false || port == 0 || hostname == null){
                    try{
                        conSocket.getOutputStream().write("Error 400, Bad request. URL does not contain valid port or hostname.".getBytes(StandardCharsets.UTF_8));
                        conSocket.close();
                        continue;
                    }catch(IOException e){
                        System.err.println(e);
                        System.exit(1);
                    }
                }
        
            
            //Try
                
                try{
                    byte[] outputbytes = askServer(hostname, port, usinbytes);
                    conSocket.getOutputStream().write(outputbytes);
                    conSocket.close();
                }catch(IOException e){
                    try{
                        conSocket.getOutputStream().write("Error 404. Host not found. Verify hostname and port validity (IOException).".getBytes(StandardCharsets.UTF_8));
                        conSocket.close();
                        continue;
                    }catch(IOException a){
                        System.err.println(e);
                        System.exit(1);
                    }
                }

            }
    
        }       
        
    }



























    public static byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        String s = new String(toServerBytes, StandardCharsets.UTF_8);
        System.out.println(hostname + " " + port+ " "+ timeout +" "+limit+" "+s);
        Socket socket = new Socket(hostname, port);
        if(timeout == null) timeout = 0;
        byte[] returnarray = new byte[0];
        ByteArrayOutputStream dynarray = new ByteArrayOutputStream(); 

        socket.setSoTimeout(timeout);
        try{
            byte[] buffer = new byte[1024];
            
            int i = 0;
            int j = 0;

            OutputStream OS = socket.getOutputStream();
            if(toServerBytes != null) OS.write(toServerBytes);
            InputStream IS = socket.getInputStream();
            
            if(shutdown){
                
                int cap = 0;
                
                OS.flush();
                while((i = IS.read(buffer)) != -1 && cap < 100000){
                    dynarray.write(buffer, 0, i);
                    cap++;
                }
                socket.shutdownOutput();
                returnarray = dynarray.toByteArray();
                IS.close();
                OS.close();
                socket.close();
                return returnarray;
            }
            
            if(limit != null){
                
                while((i = IS.read(buffer)) != -1){
                    dynarray.write(buffer, 0, i);
                    j = j+1024;
                    if(j >= limit) shutdown = true;    
                }
                IS.close();
                OS.close();
                socket.close();
                byte[] temparray = dynarray.toByteArray();

                if(temparray.length > limit) returnarray = new byte[limit];
                else returnarray = new byte[temparray.length];

                for(i = 0; i < returnarray.length; i++){
                    returnarray[i] = temparray[i];
                }

                return returnarray;
                
            }
            else{
                
                while((i = IS.read(buffer)) != -1 && j < 1000000){
                    dynarray.write(buffer, 0, i);
                    j = j+1024;    
                }
                returnarray = dynarray.toByteArray();
                IS.close();
                OS.close();
                socket.close();
                return returnarray;
            }
        } catch(SocketTimeoutException e){
            System.out.println("Socket Timed out. " + e);
            returnarray = dynarray.toByteArray();
            socket.close();
            return returnarray;
        }
    }
}

