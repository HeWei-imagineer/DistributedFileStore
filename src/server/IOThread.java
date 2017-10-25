package server;

import java.net.Socket;

public class IOThread extends Thread {
	private Socket socket = null;
	private IOStrategy ios = null;

	public IOThread(IOStrategy ios) { // ��Ƚ���һ���е�IOThread��Ĺ��췽��
		this.ios = ios; // �кβ�ͬ��
	}

	public boolean isIdle() { // ���socket����Ϊ�գ���ô����̵߳�Ȼ�ǿ��е�
		return socket == null;
	}

	public synchronized void setSocket(Socket socket) {
		this.socket = socket; // ���ݸ�����������߳�һ�������񡱣�����������
		System.out.println("set socket");
		notify();
	}

	public synchronized void run() { // ���ͬ�����������Ǳ���ʲô�������ݣ�
		while (true) { // ������Ϊwait�������ñ���ӵ�ж�����
			try {
				wait(); // �����߳�������̽����������ȴ�״̬
				System.out.println("notify success");
				ios.service(socket); // �����Ѻ����̿�ʼִ�з���Э��
				socket = null; // ������������̷��ص�����״̬
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

