package main;

public class SocketMsg {
	private StartServers chatSocket;
	private String username;
	
	public SocketMsg(StartServers chatSocket,String username) {
		this.chatSocket = chatSocket;
		this.username = username;
	}
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public StartServers getChatSocket() {
		return chatSocket;
	}
	public void setChatSocket(StartServers chatSocket) {
		this.chatSocket = chatSocket;
	}
	
	

}
