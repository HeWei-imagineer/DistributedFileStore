package client;
import java.io.*;
import java.net.*;
import java.util.*;

import server.Server;


public class ClientModle1{
//uoload
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
    private Socket s;
	public ClientModle1(String host, int port) throws Exception {
		 s = new Socket(host, port); // 这个Socket对象创建完毕后何时销毁？
		
		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());
	}
	
	//压缩加密后的数组内容
	static byte[] encryptFile;
	public void upload(String fileName) {
		int FileLength=0;
		
		try {
			System.out.println("处理文件，准备向节点发送");
			FileInputStream in = new FileInputStream(fileName);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
		    byte[] buffer = new byte[1024 * 4];
		    int n = 0;
		    
		    while ((n = in.read(buffer)) != -1) {
		        out.write(buffer, 0, n);
		    }
		    byte[] data = out.toByteArray();
			
			byte[] Zipfile = client.Tool.compress(data);
			encryptFile = client.Tool.encrypt(Zipfile);
			
			FileLength = data.length;
			in.close();
			out.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("文件处理出错");
		}
		try {
			System.out.println("send upload to server");
			dos.writeUTF("upload");
			dos.writeInt(FileLength);
			dos.writeUTF(fileName);
		
			dos.flush();
			
			//receive the service Node sum
			int storNodeSum = dis.readInt();
			System.out.println("storNodeSum  "+storNodeSum);
			//mostly same code in case 
			switch (storNodeSum) {
			case 0:
				s.close();
				System.out.println("upload fial, no nede service");
				break;
				
			case 1:
				System.out.println("one node service");
				String viceIP=dis.readUTF();
			    int vicePort=dis.readInt();
			    System.out.println(vicePort);
			    String UUID=dis.readUTF();
				System.out.println(UUID);
				s.close();
				
				try {
				System.out.println("send upload to one Node"+vicePort);
				Socket s1 = new Socket(viceIP,vicePort);
				DataInputStream dis1 = new DataInputStream(s1.getInputStream());
				DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
				
				dos1.writeInt(1);
				dos1.writeUTF(UUID);
				dos1.writeInt(FileLength);
				System.out.print(FileLength);
				
				dos1.write(encryptFile);
				s1.shutdownOutput();
				int result = dis1.readInt();
				System.out.print(result);
				if(result==1){
					System.out.print("upload success");
				}
			    s1.close();
			    
			    
				return;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				break;
			   
				
			case 2:
				System.out.println("two node service");
				String mainIP=dis.readUTF();
			    int mainPort=dis.readInt();
			    System.out.println(mainIP);
			    viceIP=dis.readUTF();
			    vicePort=dis.readInt();
			    String viceNodeFolder = dis.readUTF();
			    System.out.println(vicePort);
			    System.out.println("viceNodeFolder "+viceNodeFolder);
			    UUID=dis.readUTF();
				System.out.println(UUID);
			    s.close();
			    //send to node
			    try {
					System.out.println("send upload to mainNode "+mainPort);
				    Socket s1 = new Socket(mainIP, mainPort);
				    DataInputStream dis1 = new DataInputStream(s1.getInputStream());
				    DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
					
					dos1.writeInt(11);
					dos1.writeUTF(UUID);
					dos1.writeInt(FileLength);
					System.out.println("FileLength "+FileLength);
					
					int result=0;
					try {
					
					dos1.writeUTF(viceIP);
					dos1.writeInt(vicePort);
					dos1.writeUTF(viceNodeFolder);
					
					dos1.write(encryptFile);
					s1.shutdownOutput();
					result = dis1.readInt();
					System.out.print(result);
					s1.close();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					if(result==1){
						System.out.print("upload success");
					}
					else{
						    System.out.println("send upload to viceNode"+vicePort);
						    s1 = new Socket(viceIP,vicePort);
							dis1 = new DataInputStream(s1.getInputStream());
							dos1 = new DataOutputStream(s1.getOutputStream());
							
							dos1.writeInt(1);
							dos1.writeUTF(UUID);
							dos1.writeInt(FileLength);
							System.out.print("FileLength "+FileLength);
							
							dos1.write(encryptFile);
							s1.shutdownOutput();
							result = dis1.readInt();
							System.out.print(result);
							if(result==1){
								System.out.print("upload success");
							}
							s1.close();
				}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			    break;
				
			default:
				break;
			}
	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void download(String fileName) {
		try {
			System.out.println("send download to server");
			dos.writeUTF("download");
			dos.writeUTF(fileName);
		    dos.flush();
		   
		    String UUID=dis.readUTF();
			System.out.println(UUID);
			
			
			int mainPort=0;
			String mainIP="";
			int vicePort=0;
			String viceIP="";
			String viceFolder ="";
			System.out.println("viceFolder "+viceFolder);
			
			try {
				mainPort = dis.readInt();
				mainIP = dis.readUTF();
				vicePort = dis.readInt();
				viceIP = dis.readUTF();
				//viceFolder = dis.readUTF();
			} catch (java.io.EOFException e) {
				// TODO: handle exception
				System.out.println("throw io.EOFException because server shutdownOutputstream, don't care I already solve");
			}
			
		    dis.close();
		    dos.close();
		    
		    
		    try {
				System.out.println("send download to mainNode"+mainPort);
				Socket s1 = new Socket(mainIP, mainPort);
				DataInputStream dis1 = new DataInputStream(s1.getInputStream());
				DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
				
				dos1.writeInt(2);
				dos1.writeUTF(UUID);
				//dos1.writeUTF(viceFolder);
				//ystem.out.println("viceFolder "+viceFolder);
				
				int result = dis1.readInt();
				if(result==0){
					s1.close();
					
					//create with vice Node
					System.out.println("send download to viceNode"+vicePort);
					s1 = new Socket(viceIP, vicePort);
				    dis1 = new DataInputStream(s1.getInputStream());
				    dos1 = new DataOutputStream(s1.getOutputStream());
					
					dos1.writeInt(2);
					dos1.writeUTF(UUID);
					
					if(result==0){
						System.out.println("all downlode fail");
					}
					else{
						int length=dis1.readInt();
						System.out.println("read fileLength from viceNode  "+length);
						
						byte[] data = new byte[length];
						dis1.read(data);
						
						System.out.println(data[1]);
						System.out.println("start decrycy file");
						byte[] decryptFile = client.Tool.decrypt(data);
						byte[] deZipfile = client.Tool.decompress(decryptFile);
					    
						System.out.println(fileName);
						System.out.println(deZipfile[1]);
						FileOutputStream fos = new FileOutputStream(fileName);
						fos.write(deZipfile);
						dis1.close();
					    dos1.close();
					}
					
				}
				//continue contact with main node
				else{
					int length=dis1.readInt();
					System.out.println("read fileLength from mainNode  "+length);
					
					byte[] data = new byte[length];
					dis1.read(data);
					
					System.out.println(data[1]);
					System.out.println("start decrycy file");
					byte[] decryptFile = client.Tool.decrypt(data);
					byte[] deZipfile = client.Tool.decompress(decryptFile);
				    
					System.out.println(fileName);
					System.out.println(deZipfile[1]);
					FileOutputStream fos = new FileOutputStream(fileName);
					fos.write(deZipfile);
					dis1.close();
				    dos1.close();
				}
				
				
				
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("download fail");
			}
		    
		} catch (Exception e) {
			//receive java.io.EOFException,if file not exit
			//e.printStackTrace();
			System.out.println("download fai");
		}
		
		
	}
	
	public void delete(String fileName) {
		try {
			System.out.println("send delete to server");
			dos.writeUTF("delete");
			dos.writeUTF(fileName);
		    dos.flush();
		   
		    String UUID=dis.readUTF();
			System.out.println(UUID);
			
			int mainPort=dis.readInt();
		    System.out.println(mainPort);
			String mainIP=dis.readUTF();
		    System.out.println(mainIP);
		    
		    int vicePort=dis.readInt();
		    String viceIP=dis.readUTF();
		    String viceFolder = dis.readUTF();
		    s.close();
		    
		    System.out.println("send delete to mainNode"+mainPort);
			Socket s1 = new Socket(mainIP, mainPort);
			DataInputStream dis1 = new DataInputStream(s1.getInputStream());
			DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
			
			dos1.writeInt(3);
			dos1.writeUTF(UUID);
			dos1.writeUTF(viceIP);
			dos1.writeInt(vicePort);
			dos1.writeUTF(viceFolder);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
}

	
	
