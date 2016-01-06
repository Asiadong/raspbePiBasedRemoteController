package heaserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket; 
import java.net.Socket;


public class MyServer {
	
	private final static String go_data    =   "0";
	private final static String back_data  =   "1";
	private final static String left_data  =   "2";
	private final static String right_data =   "3";
	private final static String stop_data  =   "4";
	
	private boolean isStartServer = true;
	private boolean tocloseServer;
	private ServerSocket mServer;
	private native boolean control(byte[] parament);
	
	static{ 
        System.loadLibrary("controller"); 
    }
	
	
	private  synchronized void startSocket() {
		try {
			tocloseServer = false;
			int prot = 6666;
			mServer = new ServerSocket(prot);//����һ��ServerSocket
			Socket socket = null;
			System.out.println("����server,�˿ڣ�"+prot);
			CloseThread closethread = new CloseThread();
			closethread.start();
			while(isStartServer) {
				if(!mServer.isClosed()){
					socket = mServer.accept();					
					SocketThread startthread = new SocketThread(socket);	
					startthread.start();
					tocloseServer = true;
				}
				else 
					exit(); 
			}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class CloseThread extends Thread{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		public CloseThread() {
			
		}
		@Override
		public synchronized void run() {
			// TODO �Զ����ɵķ������
			super.run();
			while (true){
				try {
					if(br.readLine().contains("close")){
						if(tocloseServer){
							mServer.close();
							}
						else{
							isStartServer = false;
							}
						System.out.println("the server program has been closed!");
						Thread.sleep(20);
						exit();
					}

				} catch (IOException | InterruptedException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * ����һ��SocketThread�࣬���ڽ�����Ϣ
	 *
	 */
	public class SocketThread extends Thread {
		
		public Socket socket;//Socket���ڻ�ȡ�������������
		public BufferedWriter writer;
		public BufferedReader reader;
		public SocketThread(Socket socket) {
			this.socket = socket;		
		}
		
		@Override
		public void run() {
			super.run();
			System.out.println("�пͻ������ӣ�\r\n");
			try {		 
				//��ʼ��BufferedReader
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
				//��ʼ��BufferedWriter
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
				while(isStartServer) {
					//���ж�reader�Ƿ��Ѿ�׼����
					if(reader.ready()) {
						String data = reader.readLine();
						control(stringtobytearray(data));
						System.out.println("�յ�һ����Ϣ��"+data);
						if(data.length()>2)
							if(data.toString().contentEquals("close")){
								socket.close();
								System.out.println("�ͻ��˹رգ�\r\n");
							}
					}
					//˯��100ms��ÿ100ms���һ���Ƿ��н��յ���Ϣ
					Thread.sleep(100);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}  

		}
	}
	
	private byte[] stringtobytearray(String para){	
		int length = para.length();
		byte [] data = new byte[length];
		if(para.startsWith("car"))
			if(para.endsWith("00"))
				data = go_data.getBytes();
			else if(para.endsWith("11"))
				data = back_data.getBytes();
			else if(para.endsWith("22"))
				data = left_data.getBytes();
			else if(para.endsWith("33"))
				data = right_data.getBytes();
			else if(para.endsWith("44"))
				data = stop_data.getBytes();
		if(para.contains("close"))
			data = stop_data.getBytes();
		return data;
	}
	
	private void exit(){
		System.exit(0);
	}
	
	public static void main(String[] args) {
		MyServer server = new MyServer();
		server.startSocket();		
	}
}
