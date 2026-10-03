package Server;

import java.io.IOException;
import java.net.*;
import java.net.ServerSocket;

public class Server {
 
	private ServerSocket server;
	private boolean Running;
	
	public Server (int Port)
	{
		try
		{
			server=new ServerSocket();
			
			server.setReuseAddress(true);
			
			server.bind(new InetSocketAddress(Port));
			
			Running=true ;
			
			
		}catch(IOException e)
		{
			System.out.println("Exception  Server Construstor"+e.getMessage());
		}
	}
	
	public void ServerEngine()
	{
		System.out.println("The server is running");
	try {
			while(Running)
			{
				Socket ClientConnection=server.accept();
				
				ServerHandler s= new ServerHandler(ClientConnection);
				
				Thread t= new Thread(s);
						
				t.start();
				;
				
			}
		}catch(IOException e) {
			
			System.out.println("Message"+e.getMessage());
			
		}
	
	
	}
	
	
	
	public static void main(String[] args)
	{
	
		Server s= new Server(3030);
		
		s.ServerEngine();

	}

}
