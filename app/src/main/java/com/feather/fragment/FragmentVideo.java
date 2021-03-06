package com.feather.fragment;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.ifuture.carcontrl_client.R;
import com.feather.activity.ClientMainActivity;
import com.feather.activity.Instruction;
import com.feather.bottombar.BaseFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentVideo extends BaseFragment{

	ClientMainActivity mainActivity;
	//Button video_button;
	ImageView cameraiImageView;
	private boolean selection = true;

	TextView bedroom_tempValue,bedroom_humiValue;
	Button bedroom_led1, bedroom_led2, bedroom_led3;
	Button bedroom_win1, bedroom_win2;
	Button bedroom_door;
	Button bedroom_humiControl, bedroom_tempControl;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.video_fragment, container, false);
	}
	
	

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		/*要在onCreateView之后得到空间才是有效的*/
		//video_button = (Button) getActivity().findViewById(R.id.video_button);
		cameraiImageView = (ImageView) getActivity().findViewById(R.id.camera_jpg);

		bedroom_tempValue = (TextView) getActivity().findViewById(R.id.room_tempValue);
		bedroom_humiValue = (TextView) getActivity().findViewById(R.id.room_humiValue);
		bedroom_led1 = (Button) getActivity().findViewById(R.id.room_led1_button);
		bedroom_led2 = (Button) getActivity().findViewById(R.id.room_led2_button);
		bedroom_led3 = (Button) getActivity().findViewById(R.id.room_led3_button);
		bedroom_tempControl = (Button)getActivity().findViewById(R.id.room_tempControl);
		bedroom_win1 = (Button) getActivity().findViewById(R.id.room_win1_button);
		bedroom_win2 = (Button) getActivity().findViewById(R.id.room_win2_button);
		bedroom_door = (Button) getActivity().findViewById(R.id.room_door_button);
		bedroom_humiControl = (Button) getActivity().findViewById(R.id.room_humiControl);
		/*设置监听器*/
		bedroom_led1.setOnClickListener(new LampButtonListener());
		bedroom_led2.setOnClickListener(new LampButtonListener());
		bedroom_led3.setOnClickListener(new LampButtonListener());
		/*demo*/
		bedroom_win1.setOnClickListener(new demoButtonListener());
		bedroom_win2.setOnClickListener(new demoButtonListener());
		bedroom_door.setOnClickListener(new demoButtonListener());
		bedroom_humiControl.setOnClickListener(new demoButtonListener());
		//video_button.setOnClickListener(new ButtonListener());
		//videoStart(); //start video
	}

	/**
	 *  @Description: 将FragmentVideo和ClientMainActivity的handler绑定起来，便于通信。
	 */

	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mainActivity = (ClientMainActivity) activity;
		mainActivity.setVideoHandler(videoHandler);
	}
	/**
	 *  处理Activity传递来的信息
	 *  打开图片进行显示
	 */
	public Handler videoHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			String typeString = bundle.getString("type");
			if(typeString.equals("videofinish"))
			{
				String path = bundle.getString("videofinish");
				File file = new File(path);
				if(file.exists()) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bm = BitmapFactory.decodeFile(path, options);
					cameraiImageView.setImageBitmap(bm);
					//Toast.makeText(getActivity(), "file found", Toast.LENGTH_SHORT).show();
				}else {
					//Toast.makeText(getActivity(), "file readme.txt not found", Toast.LENGTH_SHORT).show();
				}

			}
			else if(typeString.equals("temp"))/*设置温度*/
			{
				String IDString = bundle.getString("temp");//获取设备ID
				if(IDString.equals("10000"))
				{
					bedroom_tempValue.setText(bundle.getString("10000"));
				}
			}
			else if(typeString.equals("humi"))/*设置湿度*/
			{
				String IDString = bundle.getString("humi");
				if(IDString.equals("10000"))
				{
					bedroom_humiValue.setText(bundle.getString("10000"));
				}
			}
			else if(typeString.equals("ledon"))/*设置灯*/
			{
				if( bundle.getString("ledon").equals("0"))
				{
					//bedroom_led1.setText("台灯1");
					bedroom_led1.setTextColor(Color.RED);
				}
				else if( bundle.getString("ledon").equals("1"))
				{
					//bedroom_led2.setText("壁灯");
					bedroom_led2.setTextColor(Color.RED);
				}
				else if( bundle.getString("ledon").equals("2"))
				{
					//bedroom_led3.setText("台灯2");
					bedroom_led3.setTextColor(Color.RED);
				}
			}
			else if(typeString.equals("ledoff"))
			{
				if( bundle.getString("ledoff").equals("0"))
				{
					//bedroom_led1.setText("台灯1");
					bedroom_led1.setTextColor(getResources().getColor(R.color.text_color_default));
				}
				else if( bundle.getString("ledoff").equals("1"))
				{
					//bedroom_led2.setText("吊灯");
					bedroom_led2.setTextColor(getResources().getColor(R.color.text_color_default));
				}
				else if( bundle.getString("ledoff").equals("2"))
				{
					//bedroom_led3.setText("台灯2");
					bedroom_led3.setTextColor(getResources().getColor(R.color.text_color_default));
				}
			}

		}
	};

	/**
	 * 灯按键监听器
	 * @Descrption： 如果当前按钮的颜色为绿色则表示需要开启灯
	 */

	class LampButtonListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			int lampid = view.getId();
			byte type = Instruction.COMMAND_CONTRL;
			byte subtype = Instruction.CTL_LAMP;
			byte operator = 0;
			String IDString = new String("");

			int textColor = getResources().getColor(R.color.text_color_default);
			switch(lampid)			{
				case R.id.room_led1_button:
				{
					IDString = new String("0");
					int currentColor = bedroom_led1.getCurrentTextColor();
					if(currentColor == textColor)
					{
						operator = Instruction.LAMP_ON;
					}
					else
					{
						operator = Instruction.LAMP_OFF;
					}

				}
				break;
				case R.id.room_led2_button:
				{

					IDString = new String("1");
					int currentColor = bedroom_led2.getCurrentTextColor();
					if(currentColor == textColor)
					{
						operator = Instruction.LAMP_ON;
					}
					else
					{
						operator = Instruction.LAMP_OFF;
					}

				}
				break;
				case R.id.room_led3_button:
				{

					IDString = new String("2");
					int currentColor = bedroom_led3.getCurrentTextColor();
					if(currentColor == textColor)
					{
						operator = Instruction.LAMP_ON;
					}
					else
					{
						operator = Instruction.LAMP_OFF;
					}

				}
				break;
			}//end of switch

			try {
				/*需要发送的指令,byte数组*/
				byte typeBytes[] = {type,Instruction.COMMAND_SEPERATOR};
				byte subtypeBytes[] = {Instruction.COMMAND_SEPERATOR,subtype, Instruction.COMMAND_SEPERATOR};
				byte operatorBytes[] = {operator, Instruction.COMMAND_SEPERATOR};
				byte IDBytes[] = IDString.getBytes("UTF-8");
				byte endBytes[] = {Instruction.COMMAND_SEPERATOR, Instruction.COMMAND_END};
				byte buffer[] = new byte[subtypeBytes.length+operatorBytes.length
						+IDBytes.length+endBytes.length];

				/*转换account后面所有指令*/
				int start = 0;
				System.arraycopy(subtypeBytes ,0,buffer,start, subtypeBytes.length);
				start+=subtypeBytes.length;
				System.arraycopy(operatorBytes ,0,buffer,start, operatorBytes.length);
				start+=operatorBytes.length;
				System.arraycopy(IDBytes,0,buffer,start, IDBytes.length);
				start+=IDBytes.length;
				System.arraycopy(endBytes   ,0,buffer,start, endBytes.length);

				/*发送广播给Service，让其发送信息给服务器*/
				Intent intent = new Intent();
				intent.putExtra("type", "send");
				intent.putExtra("onefield", typeBytes);
				intent.putExtra("twofield", buffer);
				intent.setAction(intent.ACTION_MAIN);
				getActivity().sendBroadcast(intent);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 用于演示各个开关的用途，包括门窗等
	 */
	class demoButtonListener implements  OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			int textColor = getResources().getColor(R.color.text_color_default);
			switch(id)			{
				case R.id.room_win1_button:
				{
					int currentColor = bedroom_win1.getCurrentTextColor();
					if(currentColor == textColor)
					{
						bedroom_win1.setTextColor(Color.RED);
					}
					else
					{
						bedroom_win1.setTextColor(getResources().getColor(R.color.text_color_default));
					}

				}
				break;
				case R.id.room_win2_button:
				{
					int currentColor = bedroom_win2.getCurrentTextColor();
					if(currentColor == textColor)
					{
						bedroom_win2.setTextColor(Color.RED);
					}
					else
					{
						bedroom_win2.setTextColor(getResources().getColor(R.color.text_color_default));
					}

				}
				break;
				case R.id.room_door_button:
				{
					int currentColor = bedroom_door.getCurrentTextColor();
					if(currentColor == textColor)
					{
						bedroom_door.setTextColor(Color.RED);
					}
					else
					{
						bedroom_door.setTextColor(getResources().getColor(R.color.text_color_default));
					}

				}
				break;
				case R.id.room_humiControl:
				{
					int currentColor = bedroom_humiControl.getCurrentTextColor();
					if(currentColor == textColor)
					{
						bedroom_humiControl.setTextColor(Color.RED);
					}
					else
					{
						bedroom_humiControl.setTextColor(getResources().getColor(R.color.text_color_default));
					}

				}
				break;
			}//end of switch
		}
	}
	
	
}
