package Client;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClientHandler extends GridPane {
	
	private Socket ClientSocket;
	
	private InputStream is;
	private OutputStream os;
	private PrintWriter pw;
	private BufferedReader br;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private Button btnConnect;// The WEEIN BUTTON
	private Button btnList; // The WEELIST BUTTON
	private Button btnDownload;
	private Button btnUpload;// The WEEDOWN BUTTON
	private Button btnLogOut; // The WEEOUT NBUTTON
	 
	private TextField Name_Area;
	private TextField Password_Area;
	
	private Label Name_Label;
	private Label Password_Label;
	private Label List_Label;
	private Label Response_Label;
	private Label ID_Label;
	
	private TextArea Response_Area;
	private TextArea List_Area;
	private TextField ID_Area;
	
	private File DownloadedFile;
	private File FileToUplaod;
	public  ClientHandler(Stage stage)
	{
		
	SetUpGui();
	
	btnConnect.setOnAction((e)-> {
		new Thread(()->
		{
			try {
				
				ClientSocket = new Socket("localhost",3030);
				
				is= ClientSocket.getInputStream();
				os=ClientSocket.getOutputStream();
				pw= new PrintWriter(os,true);
				br= new BufferedReader(new InputStreamReader(is));
				dis= new DataInputStream(is);
				dos= new DataOutputStream(os);
				
				String name=Name_Area.getText();
				String Password=Password_Area.getText();
				sendCommand(pw,"WEENIN "+name+" "+Password);
				String ServerR= ReadResponse(br);
				
				UpdateResponse(ServerR);
		
			}catch(IOException e1)
			{
				
			}
			
			
			
		}).start();

	});
	btnList.setOnAction((e)-> {
		new Thread(()->
		{
			
		sendCommand(pw,"WEENLIST");
		String List=ReadResponse(br);
		List_Area.setText(List);
			
		}).start();
		
		
	});
	btnDownload.setOnAction((e)-> {
		
		String ID= ID_Area.getText();
		if(ID.isEmpty())
		{
			UpdateResponse("TRICK THE ID AREA IS Empty");
			return;
		}
		new Thread(()->{
			try {
			sendCommand(pw,"WEENDOWN "+ID);
			
			long FileSize=dis.readLong();
			
			if(FileSize <=0 )
			{
				UpdateResponse("Download Cancelled File Not Found");
			}
			
			ByteArrayOutputStream BAOT = new ByteArrayOutputStream();
	        byte[] Buffere= new byte[2048];
	        long TotalRead=0;
	        int Read;
	        
	        while(TotalRead < FileSize && (Read = dis.read(Buffere,0,(int)Math.min(Buffere.length, FileSize - TotalRead)))!=-1)
	        {
	        	BAOT.write(Buffere,0,Read);
	        	TotalRead+=Read;
	        }
	        
	        String ServerR= ReadResponse(br);
	        UpdateResponse(ServerR);
	        StringTokenizer st= new StringTokenizer(ServerR," ");
	        String r1=st.nextToken();
	        String r2=st.nextToken();
	        String r3=st.nextToken();
	        String r4=st.nextToken();
	        	
	        DownloadedFile = new File("data/Client/"+r4);
	        
	        
	        try (FileOutputStream fos= new FileOutputStream(DownloadedFile))
	        {
	        	BAOT.writeTo(fos);
	        	fos.flush();
	        }
			
	      
			}catch(IOException e2)
			{
				UpdateResponse("TRICK Failed to download file");
			}
			
			
		}).start();
		
		
	});
	
	btnLogOut.setOnAction((e)-> {
		new Thread(()->
		{
			sendCommand(pw,"WEENOUT");
			String ServerR=ReadResponse(br);
			
			UpdateResponse(ServerR);
		
		}).start();
		
	});
	btnUpload.setOnAction((e)-> {
		
		FileChooser fc= new FileChooser();
		fc.setTitle("Halloween App");
		FileToUplaod=fc.showOpenDialog(stage);
		
		if(FileToUplaod==null)
		{
			pw.println("TRICK Download Cancelled the file does not exist");
			return;
			
		}
		
		String ID= ID_Area.getText();
		if(ID.isEmpty())
		{
			UpdateResponse("TRICK you need to provvode and ID FOR THAT Action");
			return;
		}
		new Thread(()-> {
		try {
			String Filename= FileToUplaod.getName() ;
			long FileSize=FileToUplaod.length();
			

			sendCommand(pw,"WEENUP "+ID+" "+Filename+" "+FileSize);
			
			try(FileInputStream fis= new FileInputStream(FileToUplaod))
			{
				byte[] Buffere= new byte[2048];
				int Read;
				while((Read = fis.read(Buffere))!=-1)
				{
					dos.write(Buffere,0,Read);
				}
				dos.flush();
				
				String R= ReadResponse(br);
				UpdateResponse(R);
				
			}
		}catch(IOException e4)
		{
			e4.printStackTrace();
		}
		
			
		
			
	
		}).start();
		
		
	});
	}
	
	
	
		
	public void SetUpGui()
	{
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(15));
		
		//Button Initialisation
		btnConnect = new Button("WEENIN");
		btnList=new Button("WEENLIST");
		btnDownload= new Button("WEENDOWN");
		btnUpload= new Button("WEENUP");
		btnLogOut= new Button("WEENOUT");
		
		//Label Initialisation
		Name_Label = new Label("USER_NAME");
		Password_Label = new Label("PASSWORD");
		ID_Label = new Label("ID");
		Response_Label = new Label("RESPONSE");
		List_Label = new Label("LIST");
		
		//Text_Area Initialisation
		Name_Area= new TextField();
		Password_Area= new PasswordField();
		List_Area= new TextArea();
		Response_Area= new TextArea();
		ID_Area= new TextField();
		
		this.add(btnConnect,0,0);
		this.add(btnList, 1, 0);
		
		this.add(Name_Label,0, 1);
		this.add(Name_Area, 1, 1);
		
		this.add(Password_Label,0 , 2);
		this.add(Password_Area, 1, 2);
		
		this.add(List_Label, 0, 3);
		this.add(List_Area, 1, 3,2,1);
		
		this.add(ID_Label,0, 4);
		this.add(ID_Area, 1, 4);
		
		this.add(btnDownload, 0, 5);
		this.add(btnUpload, 0, 6);
		
		this.add(Response_Label, 0, 7);
		this.add(Response_Area, 1,7);
		
		this.add(btnLogOut, 1, 8);
		
		
	}
	public void sendCommand(PrintWriter pw,String msg)
	{
		pw.println(msg);
		pw.flush();
	}
	public String ReadResponse(BufferedReader br)
	{
		String Response=" ";
		try {
			Response=br.readLine();
		}catch(IOException e2)
		{
			UpdateResponse(e2.getMessage());
		}
		return Response;
	}
	public void UpdateResponse(String msg)
	{
		Platform.runLater(()-> Response_Area.setText(msg));
		
	}
	
	


}
