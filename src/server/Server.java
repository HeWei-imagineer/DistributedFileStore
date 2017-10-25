package server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;

import client.ClientModle1;
import storageNode.storageNode;

public class Server {
	//管理节点信息
	public static Set<storageNode>  NodeInformation = new TreeSet<storageNode>();
	//管理节点是否存活信息
	public static Map<Integer,Long> NodeIsAlive = new HashMap<Integer,Long>();
	//管理文件信息
	public static Map<String,Map<String, String>> FileInformation = new HashMap<String,Map<String, String>>();
	
    public static void main(String[] args) {
    	
    	
    	
    	//服务器初始化	
		File f = new File("FileInformation.dat"); 
		if(f.exists()){
			try {
			   System.out.println("server initialize.....");
			   ObjectInputStream in = new ObjectInputStream(new FileInputStream("file.dat"));
			   FileInformation = (Map<String,Map<String, String>>)in.readObject(); 
			   //System.out.println(FileInformation.);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		//节点监控
        Monitor.monitorNode.Mnode();
        //文件监控
		Monitor.monitorFile.MFile();
		
		//写出保留的文件信息	
		Timer timer = new Timer();
		TimerTask s = new TimerTask() {	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("FileInformation.dat"));
				out.writeObject(FileInformation);
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		};
		//每20分钟更新一次文件信息
		timer.schedule(s,1000,1000*10);
	
		
		
		//检测心跳包
    	
	    System.out.println("listen thread");
		new Thread() {	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				 System.out.println("listen heart");
					
					try {
						System.out.println(1);
						DatagramSocket serverSocket = new DatagramSocket(2222);
						 System.out.println(2);
						byte[] buffer = new byte[1024 * 4];
						DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
						serverSocket.receive(recvPacket);
		                int port = recvPacket.getPort();
		                 
						
						System.out.println("接收到 " + port + " 发送过来的数据包");

						
						byte[] recvData = new byte[recvPacket.getLength()];
						System.arraycopy(buffer, 0, recvData, 0, recvData.length);
						ByteArrayInputStream bais = new ByteArrayInputStream(recvData);
						ObjectInputStream ois = new ObjectInputStream(bais);
						storageNode s = (storageNode) ois.readObject();
						 System.out.println("read node name"+s.NodeName);
						//更新节点信息
						Server.NodeInformation.add(s);
						System.out.println("Server.NodeInformation "+Server.NodeInformation.size());
						
						//更新是否存活信息
						long currentTime = Calendar.getInstance().getTimeInMillis();
						Server.NodeIsAlive.put(s.NodePort, currentTime);
						Iterator<Entry<Integer, Long>> iterator= Server.NodeIsAlive.entrySet().iterator();
						while(iterator.hasNext()){
							Map.Entry entry = (Map.Entry) iterator.next();
		                    long lastTime = (long)entry.getValue();
							long internal = currentTime - lastTime;
							int port1=(int)entry.getKey();
							//超过五分钟认为节点失活
						    System.out.println("q");
							if(internal>1000*60*5){
								Server.NodeIsAlive.remove(port1);
							    System.out.println(port+"  node dead");
								//处理文件信息
								Iterator<storageNode> it = Server.NodeInformation.iterator();  
								while (it.hasNext()) {
									storageNode node = it.next();
								    if(node.NodePort==port1){
								    	node.IsAlive=false;
								    	Server.NodeInformation.remove(node);
								    }
								}   
							}
							else{
							Server.NodeIsAlive.put(port1, currentTime);
							}
							
							
						}
						serverSocket.close();
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("recerive node information error");
					}
			}
			
				
			
			}
		}.start();
		//每半分钟检测一次心跳包
	
		
		new NwServer(4001, new ThreadPoolSupport(new ClientServerProtocol()));
	}
}
