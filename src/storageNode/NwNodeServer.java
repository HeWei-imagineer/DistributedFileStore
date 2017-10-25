package storageNode;

import java.net.*;
import java.util.*;

import server.*;


public class NwNodeServer {
   
	public NwNodeServer(int port, IOStrategy ios) {
		
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("NodeServer is ready");
			
			//��ͣ���ܿͻ������󲢴���
			while (true) {
				Socket socket = ss.accept(); // ���������������
				ios.service(socket); // ���������˵�socket���󴫵ݸ�
			} // ThreadSupport����
	} catch (Exception e) {
		// TODO: handle exception
	}   
		
	}
	
}
