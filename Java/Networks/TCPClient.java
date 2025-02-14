
//Example implementation of a TCP Client that can send data to a server and read incoming data.
//The client itself does not connect to a server. It used in conjuction with an ASK program.
//This code was created as a project assignment.
public class TCPClient {
    
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        
    
            
            Socket sendSocket = new Socket(hostname, port);
            byte[] recvbuffer = new byte[1024];
            ByteArrayOutputStream dynarray = new ByteArrayOutputStream();

            OutputStream OS = sendSocket.getOutputStream();
            OS.write(toServerBytes);

            InputStream IS = sendSocket.getInputStream();
            int i;
            int j = 0;
            while((i = IS.read(recvbuffer)) != -1 && j != 1000000){
                dynarray.write(recvbuffer, 0, i);
            }
        
            byte[] returnarray = dynarray.toByteArray();
            sendSocket.close();
            return returnarray;

           
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        
        
        
            Socket recvSocket = new Socket(hostname, port);
            byte[] recvbuffer = new byte[1024];
            ByteArrayOutputStream dynarray = new ByteArrayOutputStream();
        
            InputStream IS = recvSocket.getInputStream();
            int i;
            int j = 0;
            while((i = IS.read(recvbuffer)) != -1 && j != 1000000){
                dynarray.write(recvbuffer, 0, i);
                j++;
            }

            byte[] returnarray = dynarray.toByteArray();
            recvSocket.close();
            return returnarray;

            
    }
}
