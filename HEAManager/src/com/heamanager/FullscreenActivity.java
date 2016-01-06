package com.heamanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class FullscreenActivity extends Activity {
	
	private final static String go_data    = "car00";
	private final static String back_data  = "car11";
	private final static String left_data  = "car22";
	private final static String right_data = "car33";
	private final static String stop_data  = "car44";
	
	private boolean flush_flag = false;
	private final static float alphavaule =  0.5f;
	private final static String closesingal = "close"; 
	private final static String[] ipsource = new String[]{"emcc123.imwork.net","103.44.145.243"};
	
	private String ip;
	private int port = 0;
	
	private Socket mSocket;
	private boolean isStartSendMsg;
	protected BufferedReader mReader;
	protected BufferedWriter mWriter;
	private SocketHandler mHandler = new SocketHandler();
	private StringBuffer mConsoleStr = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		findview();
		listener();
	}
	
	//找到按钮和文本框
	void findview(){
		viewHolder.autodamainname = (AutoCompleteTextView) findViewById(R.id.autoText);
		viewHolder.domainport = (EditText) findViewById(R.id.editText1);
		viewHolder.senddata = (TextView) findViewById(R.id.textView2);
		viewHolder.confirm = (Button) findViewById(R.id.button11);
		viewHolder.disconnect = (Button) findViewById(R.id.button12);
		viewHolder.forward = (Button) findViewById(R.id.button6);
		viewHolder.back = (Button) findViewById(R.id.button8);
		viewHolder.left = (Button) findViewById(R.id.button7);
		viewHolder.right = (Button) findViewById(R.id.button9);
		viewHolder.stop = (Button) findViewById(R.id.button10);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,   
                android.R.layout.simple_dropdown_item_1line, ipsource);
		viewHolder.autodamainname.setAdapter(adapter);
		viewHolder.autodamainname.setAlpha(alphavaule);
		
	}
	
	 //监听事件
   	void listener(){
   		//确认按钮
 	   viewHolder.confirm.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub				
 				startSocket();
 			}
 		});
 	   
		//断开连接按钮
	   viewHolder.disconnect.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				senddata(closesingal);
			}
		});
		//前进按钮
	   viewHolder.forward.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				senddata(go_data);
			}
		});
		//后退按钮
	   viewHolder.back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				senddata(back_data);
				
			}
		});
		//左转按钮
	   viewHolder.left.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				senddata(left_data);
			}
		});
		//右转按钮
	   viewHolder.right.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				senddata(right_data);
			}
		});
		//停止按钮
	   viewHolder.stop.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				senddata(stop_data);
			}
		});
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		isStartSendMsg = false;
	}
	
	//初始化获得ip地址
	private void initSocket() {
			isStartSendMsg = true;
			if(viewHolder.autodamainname.getText().toString().contains("imwork")){
				GetIpByName gbn = new GetIpByName(viewHolder.autodamainname.getText().toString());
				ip = gbn.getServerIP().getHostAddress();
			}
			else
				ip = viewHolder.autodamainname.getText().toString();
			 port = Integer.parseInt(viewHolder.domainport.getText().toString());
		}


	//新建一个线程，用于初始化socket和检测是否有接收到新的消息  
	private void startSocket() {  
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				flush_flag = true;
				initSocket();				
				try {
					isStartSendMsg = true;
					mSocket = new Socket(ip , port);
					mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "utf-8"));
					mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "utf-8"));
					while(isStartSendMsg) {
						if(mReader.ready()) {
					        mHandler.obtainMessage(0, mReader.readLine()).sendToTarget();
						}
						Thread.sleep(200);
					}
					mWriter.close();
					mReader.close();
					mSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		});
		thread.start();
	} 
	//发送数据
	protected void senddata(Object data) {
		try {
			if(flush_flag){
				mWriter.write(data+"\n");
				mWriter.flush();
				mConsoleStr.append(data);
				if(data.toString().contains("close"))
					flush_flag = false;
				viewHolder.senddata.setText("");
				viewHolder.senddata.setText(data.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	class SocketHandler extends Handler {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				try {
					Toast.makeText(FullscreenActivity.this, "发送消息："+msg, Toast.LENGTH_SHORT).show();
					viewHolder.senddata.setText("");
					viewHolder.senddata.setText(mConsoleStr);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		}
	}
}

