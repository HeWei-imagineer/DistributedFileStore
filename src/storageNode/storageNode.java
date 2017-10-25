package storageNode;

import java.io.*;
import java.util.*;

import server.IOStrategy;

import java.net.*;

public class storageNode  implements Comparable<storageNode>,IOStrategy,Serializable{
	public String NodeName;
	public String NodeIP;
	public int NodePort;
	public int Volume;
	public int trueVolume;
	public int leftVolume;
	public int fileSum;
	public File RootFolder;//store file information
	public String FileServerIP;
	public int FileServerPort;
	public boolean IsAlive;
	public storageNode(String fileName) {
		// TODO Auto-generated constructor stub
		System.out.println("new node");
		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(fileName));
			prop.load(in);
			
			NodeName = prop.getProperty("NodeName");
			NodeIP=prop.getProperty("NodeIP");
			
			NodePort  = Integer.parseInt(prop.getProperty("NodePort"));
			
			fileSum=0;
			Volume = Integer.parseInt(prop.getProperty("Volume"));
			
			trueVolume =new Integer(Volume);
			leftVolume =new Integer(Volume);
			
			
			RootFolder=new File(NodeName);
			System.out.println(RootFolder);
			RootFolder.mkdirs();
			FileServerIP = prop.getProperty("FileServerIP");
			FileServerPort  = Integer.parseInt(prop.getProperty("FileServerPort"));
			
			IsAlive = true;
		    in.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public int compareTo(storageNode s) {
		
		if(this.NodePort==s.NodePort){
			   return 0;
		    }
		else{
			
			if((s.leftVolume - this.leftVolume)==0){
			return -1;//don't care return -i or 1 
		   }
			else{
				return s.leftVolume - this.leftVolume;
			}
		}
	 }
	
	
	
	
	public void service(Socket socket){
		//开始服务
		
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		    
			int command = dis.readInt();
			System.out.println(NodePort+"  start receive"  );
			switch(command){
			  case 1:
				  String UUID = dis.readUTF();
				  int fileLength  = dis.readInt(); 
				
				  
				  File f = new File(RootFolder+"//"+UUID);
				  f.createNewFile();
				  FileOutputStream fos = new FileOutputStream(f);
				  System.out.println("----Node star receive <" + UUID +">----");
				  byte [] buffer = new byte[1024*4];
				  int read = 0;
				  try { 
					    while((read=dis.read(buffer))>0){
			            fos.write(buffer,0,read);	
			            System.out.println("read "+read);
				  }
						
					} catch (java.net.SocketException e) {
						// TODO: handle exception
						System.out.println("client shutdownOutputstream resolve");
					}
				 
				  fileSum++;
				  leftVolume -= fileLength; 
				  System.out.println(NodeName+" leftvulom"+leftVolume);
				  //
				  try {
					    dos.writeInt(1);
				  } catch (java.net.SocketException e) {
					// TODO: handle exception
					System.out.println(NodeName+"  write resolve");
				  }
				     dos.flush();
				     socket.shutdownOutput();
				  break;
			  case 2:
				  System.out.println(NodePort+"  start send download information");
				  UUID = dis.readUTF();
				  System.out.println(UUID);
				  File[] list = RootFolder.listFiles();
				  boolean find = false;
				  System.out.println( RootFolder.getName());
				  for(int i=0;i<list.length;i++){
					  System.out.println( list[i].getName());
					 if(list[i].getName().equals(UUID)){
						    dos.writeInt(1);//成功标志
						    System.out.println("take file send to client");
							FileInputStream in = new FileInputStream(list[i]);
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							
						    byte[] buf = new byte[1024 * 4];
						    read = 0;
						    
						    while ((read = in.read(buf)) != -1) {
						        out.write(buf, 0, read);
						    }
						    in.close();
						    out.close();
						    byte[] data = out.toByteArray();
						    System.out.println(data[0]);
						    System.out.println("file length"+data.length);
						    dos.writeInt(data.length);
						    dos.write(data);
						    find = true;
						    dos.writeInt(0);
						    System.out.print("node send download success");
						    
					}
				  }
				  if(!find){
					  dos.writeInt(0);
					  System.out.print("file not exit");
				  }
				  dos.flush();
				 
				 
				  break;
			  case 3:
				  System.out.println(NodePort+"  start send delete information");
				  UUID = dis.readUTF();
				  String viceIP = dis.readUTF();
				  int vicePort = dis.readInt();
				  String viceFolder = dis.readUTF();
				  
				  System.out.println(RootFolder.getName());
				  list = RootFolder.listFiles();
				  find = false;
				  System.out.println("list.length"+list.length);
				  for(int i=0;i<list.length;i++){
					 if(list[i].getName().equals(UUID)){
						  System.out.println("find file from mainNode then to delete"); 
						  leftVolume += (int)list[i].length();
						  fileSum--;
						  list[i].delete();
						    find = true;
					   break;
					}
				  }
				  
				
				  File folder = new File(viceFolder);
				  list = folder.listFiles();
				  find = false;
				  System.out.println("list.length"+list.length);
				  for(int i=0;i<list.length;i++){
					 if(list[i].getName().equals(UUID)){
						  System.out.println("find file from viceNode then to delete");
						  leftVolume += (int)list[i].length();
						  fileSum--;
						  list[i].delete();
						    find = true;
						    break;
					}
				  }
				  
				  if(!find){
					  System.out.print("file not exit");
					  }
					  dos.flush();
					  break;
					  
			 case 11://receive upload as a main node
				  System.out.print("main node receive upload");
				  UUID = dis.readUTF();
				  fileLength  = dis.readInt(); 
				  
				  viceIP = dis.readUTF();
				  vicePort = dis.readInt();
				  String viceNodeFolder = dis.readUTF();
				  
				  f = new File(RootFolder+"//"+UUID);
				  f.createNewFile();
				  fos = new FileOutputStream(f);
				  
				  ByteArrayOutputStream out = new ByteArrayOutputStream();
				  System.out.println("----Node star receive<" + UUID +">----");
				  buffer = new byte[1024*4];
				  read = 0;
				  try { 
					    int n;
					    while(read<fileLength){
					    n = dis.read(buffer);
				        out.write(buffer,0,n);
				        read += n;
				        System.out.println("read "+n);
				       
				   }
				
					    
				  } catch (Exception e) {
						// TODO: handle exception
				   System.out.println("client shutdownOutputstream resolve");
					}
				  byte [] data = out.toByteArray();
				  fos.write(data);
				  out.close();
				  
				 
				 
				  fileSum++;
				  leftVolume -= fileLength; 
				  System.out.println(NodeName+" leftvulom"+leftVolume);
				  //
				  try {
					    dos.writeInt(1);
				  } catch (java.net.SocketException e) {
					// TODO: handle exception
				System.out.println(NodeName+"  write resolve");
				  }
			    dos.flush();
				socket.shutdownOutput();
				
				//send to vice node
				try {
					 f = new File(viceNodeFolder+"//"+UUID);
					 f.createNewFile();
					 fos = new FileOutputStream(f);
					 fos.write(data);
					 fos.close();
					 System.out.println("tackup success");
				} catch (Exception e) {
					// TODO: handle exception
				}
					
				 break;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
