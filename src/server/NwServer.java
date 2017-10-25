package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.net.*;

import storageNode.NwNodeServer;
import storageNode.storageNode;



public class NwServer { // NwServer.java����������������󣬲���������Socket����
	// ͨ��IOStrategy�ӿڴ��ݸ�ThreadSupport����
	public NwServer(int port, IOStrategy ios) { // ��������������߳���ִ��
		try {
			System.out.println("server is ready");
			ServerSocket ss = new ServerSocket(port);
			
			while (true) {
				Socket socket = ss.accept(); // ���������������
				ios.service(socket); // ���������˵�socket���󴫵ݸ�
			} // ThreadSupport����
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
