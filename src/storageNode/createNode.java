package storageNode;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.text.*;

import java.util.*;

import server.*;

public class createNode {

	public createNode(String nodeFile) {
		// TODO Auto-generated method stub
		storageNode s = new storageNode(nodeFile);
		
		//设置定时器发心跳包
		
				Timer timer = new Timer();
				TimerTask myTask = new TimerTask() {
					
				@Override
				public void run() {
					try {
						System.out.println("send heart");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);
						System.out.println(s.NodeName);
						oos.writeObject(s);
						System.out.println(s.NodeName);
						byte[] data=baos.toByteArray();
						System.out.println("heart packet data ready");
						
						DatagramSocket socket = new DatagramSocket();
				        DatagramPacket packet = new DatagramPacket(data, data.length);
				        
						packet.setSocketAddress(new InetSocketAddress(s.FileServerIP,s.FileServerPort));
						System.out.println("send to " + s.FileServerPort);
						socket.send(packet);
						 String viceFolder = client.Tool.FindFolder(s.NodePort, server.Server.NodeInformation);
						 System.out.println(viceFolder);
						
						
					} catch (Exception e) {
						// TODO: handle exception
					}
					 
					
				}
				};
				timer.schedule(myTask,1000,1000*3);
			
		
				new Thread(){
			
					public void run() {
						super.run();
						new NwNodeServer(s.NodePort, s);
					}	
				}.start();

	}
}
	
		
		