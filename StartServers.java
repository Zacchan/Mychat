package main;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.DBManager;

import view.MainWindow;
import view.MainWindow.OnStartServersListener;


public class StartServers extends Thread {
	private Socket client;
	private String message = null;
	private BufferedReader is;
	private BufferedWriter os;
	
	
	public StartServers(Socket c) {
		this.client = c;
		
//		try {
//			 is = new BufferedReader(new InputStreamReader(this.client.getInputStream(),"UTF-8"));
//			 os = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream(),"UTF-8"));
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	public void run() {
		try {
			//PrintWriter os = new PrintWriter(client.getOutputStream());
			is = new BufferedReader(new InputStreamReader(this.client.getInputStream(),"UTF-8"));
		os = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream(),"UTF-8"));
			BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
	while((line = is.readLine())!=null) {
		if(!line.equals("end")) {
			message+=line;
		}else {
			delMessage(message);
			System.out.println("receive:"+message);
			line = null;
			message = null;
		}

		}	
	}catch(Exception e) {
		e.printStackTrace();
	}finally {
		try {
			is.close();
			os.close();
			client.close();
			
		}catch(Exception e) {
			e.printStackTrace();
			}
		}
	}
	
	
	
	public void delMessage(String msg) {
		if(msg!=null) {
			System.out.println(msg);
			String action = getAction(msg);
			System.out.print("test");
			switch(action) {
			case "LOGIN":{ dealLogin(msg);break;} //this is used for stored the user's information into a chatManager
			case "CHATMSG":{dealChatMsg(msg);break;}
			
			}
		}
	}
	
	
	private void dealLogin(String msg) {
		StartServers userserver = this;
		String username;
		String p = "(null)\\[LOGIN\\]:\\[(.*)\\]";
		Pattern pattern = Pattern.compile(p);
		Matcher matcher = pattern.matcher(msg);
		System.out.println(msg);
		if(matcher.find()) {
			username = matcher.group(2);
			System.out.println(username);
			SocketMsg socketmsg = new SocketMsg(userserver,username);
			ChatManager.getChatManager().socketList.add(socketmsg);
		}
		else {
			System.out.println("login cannot match up");
		}
		
		
	}
	
	private void dealChatMsg(String msg) {
		String senduser=null;
		//String senduserid=null;
		String chatuser = null;// usernamename
		//String userid = null;//real useridname
		String content = null;
		String avatarID = null;
		//String msgType
		String msgType = null;
		String out = null;
		String p = "(null)\\[CHATMSG\\]:\\[(.*),(.*),(.*),(.*),(.*)\\]";
		Pattern pattern = Pattern.compile(p);
		Matcher matcher = pattern.matcher(msg);
		System.out.println(msg);
		
		if(matcher.find()) {
			senduser = matcher.group(2);
			//senduserid = matcher.group(2);
			
			chatuser = matcher.group(3);
			//userid = matcher.group(4);
    		content = matcher.group(4);
    		avatarID = matcher.group(5);
    		msgType = matcher.group(6);
			
		}else {
			System.out.println("mistakes in chatmsg");
		}
		for(SocketMsg SocketMsg:ChatManager.getChatManager().socketList) {
			if(SocketMsg.getUsername().equals(chatuser)) {
				out = "[GETCHATMSG]:["+senduser+","+content+","+avatarID+"]";
				SocketMsg.getChatSocket().sendMsg(out);
			}
		}
	
		
		
		
	}
	public void sendMsg(String msg)
	{
		try {
			while(client ==null);
			System.out.println("send:"+msg);
			os.write(msg+"\n");
			os.flush();
			os.write("end\n");
			os.flush();	
			}catch(IOException e) {
				e.printStackTrace();
		}
	}
	
	public String getAction(String msg) {
	    String p = "\\[(.*)\\]:";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "error";
        }
	}
	
	
	
	public static void main(String[] args) throws Exception{
		ServerSocket server = new ServerSocket(12000);
		System.out.println("hello");
		while(true) {
			StartServers s = new StartServers(server.accept());
			s.start();	
		}
	}
}
