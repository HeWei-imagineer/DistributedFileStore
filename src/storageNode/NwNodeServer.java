package storageNode;

import java.net.*;
import java.util.*;

import server.*;


public class NwNodeServer {
   
	public NwNodeServer(int port, IOStrategy ios) {
		
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("NodeServer is ready");
			
			//不停接受客户端请求并处理
			while (true) {
				Socket socket = ss.accept(); // 负责接受连接请求，
				ios.service(socket); // 将服务器端的socket对象传递给
			} // ThreadSupport对象
	} catch (Exception e) {
		// TODO: handle exception
	}   
		
	}
	
}
