package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.omg.PortableInterceptor.DISCARDING;

import client.Tool;
import storageNode.storageNode;

public class ClientServerProtocol implements IOStrategy{
    
	public void service(Socket socket) {
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			String command;

			command = dis.readUTF(); // 实际上，协议命令的数值
			switch (command) { 
			case "upload": 
				System.out.println("server receive upload");
				
				int length = dis.readInt();
				String FileName = dis.readUTF();
				System.out.println("server get FileName "+FileName);
				
				
				//寻找两个空闲节点返回给client
				Iterator<storageNode> iterator= Server.NodeInformation.iterator();
				Map<String, String> f = new HashMap<String, String>();
				storageNode node;
				if(Server.NodeInformation.isEmpty()){
					dos.writeInt(0);
					System.out.print("No storageNode offer sevice");
					break;
				}
				else if(Server.NodeInformation.size()==1){
					dos.writeInt(1);
					node = iterator.next();
					String mainIP=node.NodeIP;
					dos.writeUTF(mainIP);
					int mainPort=node.NodePort;
					dos.writeInt(mainPort);
					
					System.out.print("one storageNode offer sevice");
					String UUID = Tool.getUUID();
					dos.writeUTF(UUID);
					dos.flush();
					
					System.out.println("server record fileInformation");
					f.put("FileName", FileName);
					System.out.println(FileName);
					f.put("UUID",UUID);
					f.put("fileLength",String.valueOf(length));
					System.out.println(String.valueOf(mainPort));
					f.put("mainIP",mainIP);
					f.put("mainPort",String.valueOf(mainPort));
				
				}
				else {
					dos.writeInt(2);
					node = iterator.next();
					String mainIP=node.NodeIP;
					dos.writeUTF(mainIP);
					int mainPort=node.NodePort;
					dos.writeInt(mainPort);
					
					node = iterator.next();
					String viceIP=node.NodeIP;
					dos.writeUTF(viceIP);
					int vicePort=node.NodePort;
					dos.writeInt(vicePort);
					String viceNodeFolder = node.RootFolder.getName();// 时间来不及了
					dos.writeUTF(viceNodeFolder);
					
					System.out.print("more than one storageNode offer sevice");
					String UUID = Tool.getUUID();
					dos.writeUTF(UUID);
					dos.flush();
					
					System.out.println("server record fileInformation");
					f.put("FileName", FileName);
					System.out.println(FileName);
					f.put("UUID",UUID);
					f.put("fileLength",String.valueOf(length));
					System.out.println(String.valueOf(mainPort));
					f.put("mainIP",mainIP);
					f.put("mainPort",String.valueOf(mainPort));
					f.put("viceIP",viceIP);
					f.put("vicePort",String.valueOf(vicePort));
					System.out.println("vicePort"+String.valueOf(mainPort));
				}
		
				Server.FileInformation.put(FileName,f);
				System.out.println("success store file "+Server.FileInformation.get(FileName).get("FileName"));
				
				socket.shutdownOutput();
				break;
				
			case "download": //
				
				System.out.println("server start send download information to client");
				String fileName = dis.readUTF();
				System.out.println(fileName);
				if(!Server.FileInformation.containsKey(fileName)){
					System.out.println("file not exit");
					socket.close();
					break;
					
				}
				else{
					dos.writeUTF(Server.FileInformation.get(fileName).get("UUID"));
					dos.writeInt(Integer.parseInt(Server.FileInformation.get(fileName).get("mainPort")));
					dos.writeUTF(Server.FileInformation.get(fileName).get("mainIP"));
					int vicePort = Integer.parseInt(Server.FileInformation.get(fileName).get("vicePort"));
					dos.writeInt(vicePort);
					dos.writeUTF(Server.FileInformation.get(fileName).get("viceIP"));
					/*String viceFolder=client.Tool.FindFolder(vicePort, Server.NodeInformation);
					dos.writeUTF(viceFolder);
					System.out.println(viceFolder);*/
					System.out.println(Server.FileInformation.get(fileName).get("mainPort"));
					
					
					dos.flush();
					
					System.out.println("server send download information to client success");
					
					socket.shutdownOutput();
				}
				break;
				
			case "delete": // 命令3映射到delete方法
				
				System.out.println("server start send delete information to client");
				String filename = dis.readUTF();
				System.out.println(filename);
				System.out.println(Server.FileInformation.get(filename).get("mainPort"));
				dos.writeUTF(Server.FileInformation.get(filename).get("UUID"));
				dos.writeInt(Integer.parseInt(Server.FileInformation.get(filename).get("mainPort")));
				dos.writeUTF(Server.FileInformation.get(filename).get("mainIP"));
				int vicePort = Integer.parseInt(Server.FileInformation.get(filename).get("vicePort"));
				dos.writeInt(vicePort);
				dos.writeUTF(Server.FileInformation.get(filename).get("viceIP"));
				String viceFolder = client.Tool.FindFolder(vicePort, server.Server.NodeInformation);
				dos.writeUTF(viceFolder);
				System.out.println(viceFolder);
				Server.FileInformation.remove(filename);
				
				dos.flush();
				break;
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("client disconnected.");
			e.printStackTrace();
		}
		
	} 
}
