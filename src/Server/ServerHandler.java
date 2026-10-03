package Server;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ServerHandler implements Runnable {

	private Socket ClientConnection;
	

	private InputStream is;
	private OutputStream os;
	private PrintWriter pw;
	private BufferedReader br;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private boolean isAuthenticated;
	String User;
	
	public ServerHandler(Socket s)
	{
		try {
			ClientConnection=s;
			
			is=ClientConnection.getInputStream();
			os=ClientConnection.getOutputStream();
			pw=new PrintWriter(os,true);
			br= new BufferedReader(new InputStreamReader(is));
			dos= new DataOutputStream(os);
			dis= new DataInputStream(is);
			
		}catch(IOException e)
		{
			System.out.println("Error in SeverHandler"+e.getMessage());
		}
		
	}
	

	@Override
	public void run() {
		System.out.println("Handling Client Requests");
		try
		{
			String CommandLine;
			
			while(((CommandLine=br.readLine()) != null))
			{
			  StringTokenizer Tokens= new StringTokenizer(CommandLine," ");
			  
			  String Command = Tokens.nextToken();
			  
			  switch(Command)
			  {
			  case "WEENIN" :
			  {
				  
				  if(Tokens.countTokens() >= 2)
				  {
					  String name= Tokens.nextToken();
					  String Password= Tokens.nextToken();
					  
					  if(name.trim().equals("Weenie") && Password.trim().equals("123"))
					  {
						  isAuthenticated=true;
						  User=name;
					  }
					  pw.println("TREAT SuccessFully Loged In");
				  }else
				  {
					pw.println("TRICK Missing Tokens");
					break;
				  }
				
			   break;
			  }
			  case "WEENLIST" :
			  {
				  if(!isAuthenticated)
				  {
					  pw.println("TRICK UnAuthenitcaed Action");
					  break;
				  }
				  
				  String List=handleList();
				  pw.println(List);
				  break;
			  }
			  case "WEENDOWN" :
			  {
				  
				  if(!isAuthenticated)
				  {
					  pw.println("TRICK UnAuthenitcaed Action");
					  break;
				  }
				  
				  String ID= Tokens.nextToken();
				  
				  if(ID.isEmpty())
				  {
					  pw.println("TRICK ID is Required To Make A download");
					  break;
				  }
				  
				  handleDownloads(ID);
				  pw.println("TREAT DOWNLOAD SUCCESSFUL");
				  break;
			  } 
			  case "WEENUP":
			  {
				  if(!isAuthenticated)
				  {
					  pw.println("TRICK UnAuthenitcaed Action");
					  break;
				  }
				  if(Tokens.countTokens()>=3)
				  {
					  String ID=Tokens.nextToken();
					  String Filename=Tokens.nextToken();
					  long FileSize= Long.parseLong(Tokens.nextToken());
					 
					  handleFileUploads(ID,Filename,FileSize);
					  
				  }else
				  {
					  pw.println("TRICK Missing Token ID/FILENAME/FILESIZE");
					  return;
				  }
				  
			  }
			  case "WEENOUT" :
			  {
				  pw.println("TREAT GOODBYE ");
				return;  
			  }
			  default :
			  {
				  pw.println("Unkown Command");
			  }
			  
			  }
				
				
			
			}
			
			
		}catch(IOException e)
		{
			System.out.println(e.getMessage());
		}finally {
			Clean();
		}
	}
	
	public String handleList()
	{
		File ListFile= new File("data/Server/Plans.txt");
		
		if(!ListFile.exists())
		{
			pw.print("TRICK THE LIST FILE IS NOT FOUND");
		
		}
		StringBuilder sb= new StringBuilder();
		
		try(Scanner sc= new Scanner(ListFile))
		{
			while(sc.hasNext())
			{
				String Line=sc.nextLine();
				sb.append(Line).append("#");
				
			}
			
		}catch(IOException e)
		{
			pw.println("TRICK"+e.getMessage());
		}
		return sb.toString();
		
	}

	
	public void  handleDownloads(String SearchID) throws IOException 
	{
	   String Filename = handleFilename(SearchID);
	    
	   File FileToDownload= new File("data/Server/"+Filename);
	   
	   if(FileToDownload.exists() && !Filename.isEmpty())
	   {
		   //System.out.println("UP TO000");
		   long Filesize=FileToDownload.length();
		   
		   dos.writeLong(Filesize);
		   dos.flush();
		   
		   try(FileInputStream fis= new FileInputStream(FileToDownload))
		   {
			   byte[] Buffere= new byte [2048];
			   int Read;
			   
			   while((Read = fis.read(Buffere)) !=-1)
			   {
				   dos.write(Buffere,0,Read);
			   }
			   dos.flush();
			   
		   }catch(IOException e)
		   {
			   pw.println("TRICK"+e.getMessage());
		   }
		   pw.println("TREAT DOWNLOAD SUCCESSFUL "+Filename);
		  //System.out.println("UP TOOO01");
		
	   }else
	   {
		   pw.println("TRICK_ Server Error: Filename or file could not be found during Download");
		   
	   }
	}
	
	public String handleFilename(String SearchID) 
	{
		File Filelists= new File("data/Server/Plans.txt");
		
		if(!Filelists.exists())
		{
			pw.println("The File To Download Does Not Exist");
		}
		
		try(Scanner sc= new Scanner(Filelists))
		{
			while(sc.hasNext())
			{
				String Line= sc.nextLine();
				
				StringTokenizer st= new StringTokenizer(Line," ");
				 String ID= st.nextToken();
				 
				 if(ID.trim().equals(SearchID) )
				 {
					 return st.nextToken();
				 }
				
			}
		}catch(FileNotFoundException e)
		{
			System.out.println("Error"+e.getMessage());
		}
		return " ";
	}
	public void Clean()
	{
		try {
			if(ClientConnection!= null || !ClientConnection.isClosed())
			{
				ClientConnection.close();
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void handleFileUploads(String ID, String Filename,long FileSize) throws IOException
	{
		File Directory= new File("data/Server");
		
		if(!Directory.exists())
		{
			Directory.mkdir();
		}
		
		File FileToDownload= new File(Directory,Filename);
		
		try(FileOutputStream fos= new FileOutputStream(FileToDownload))
		{
			byte[] Buffere= new byte[2048];
			long TotalRead=0;
			int Read;
			
			while(TotalRead < FileSize && (Read = dis.read(Buffere,0,(int)Math.min(Buffere.length, FileSize-TotalRead)))!=-1)
			{
				fos.write(Buffere,0,Read);
				TotalRead += Read;
			}
			fos.flush();
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
}
