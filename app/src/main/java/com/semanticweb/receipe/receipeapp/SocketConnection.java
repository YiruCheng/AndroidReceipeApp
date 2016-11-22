package com.semanticweb.receipe.receipeapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

/**
 * Use socket to connect with python server.
 * @author Yi-Ru
 *
 */
public class SocketConnection {
	
	private static String ip = "192.168.0.24";
//	private static String ip = "134.155.209.85";
	private static int port = 2222;
	private Socket socket;
	
	private PrintWriter pr;
	private BufferedReader br;
	
	private List<String> ingredientList;
	
	public SocketConnection() {
		try {
			socket = new Socket(ip, port);
			pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SocketConnection(List<String> ingredientList) {
		try {
			this.ingredientList = ingredientList;
			socket = new Socket(ip, port);
			pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send() throws IOException{
//		String sendMsg = "{\"Avocado\":1,\"Bananas\":1,\"Vegetable Oil\":0,\"Apples\":0}";
		String sendMsg = "{";
        for(int i = 0;i < ingredientList.size();i++){
//        	String after_capitalized = ingredientList.get(i).replace(ingredientList.get(i).substring(0, 1), ingredientList.get(i).substring(0, 1).toUpperCase(Locale.ENGLISH));
//        	sendMsg += after_capitalized;
        	String ingredient_temp = ingredientList.get(i).substring(0, ingredientList.get(i).indexOf(":"));
        	String priority_temp = ingredientList.get(i).substring(ingredientList.get(i).indexOf(":")+1);
        	sendMsg += "\""+ingredient_temp+"\":"+priority_temp;
        	if(i != ingredientList.size()-1)
        		sendMsg += ",";
        }
        sendMsg += "}";
		
        System.out.println("sendMsg: "+sendMsg);
		pr.print(sendMsg);
		pr.flush();
	}
	
	public void sendRecipeID(String key) throws IOException{
		System.out.println("in queryByRecipeID --------");
		System.out.println("key: "+key);
		pr.print(key);
		pr.flush();
	}
	
	public String receiver() throws IOException{
		System.out.println("in receiver --------");
		String msg = br.readLine();
		br.close();
		System.out.println("msg: "+msg);
		System.out.println("end readUTF");
		return msg;
	}
}
