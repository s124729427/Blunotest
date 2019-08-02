package com.dfrobot.angelo.blunobasicdemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;
import android.content.Intent;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import android.media.AudioManager;
import android.media.SoundPool;


import android.os.Vibrator;
import android.widget.Toast;


public class MainActivity  extends BlunoLibrary {


	private static String DATABASE_TABLE = "SL";
	private static String DATABASE_TABLE2 = "SR";
	private SQLiteDatabase db;
	private MyDBHelper dbHelper;


	private Spinner spinner1,spinner2;



	private Button buttonScan;
	private Button buttonScan2;

	private Button buttonScan3;
	private Button buttonScan4;

//	private Button limitbutton;
//	private Button cancelbutton;

//	private Button limitbutton2;
//	private Button cancelbutton2;

	private TextView test, text1, text2, text3, text4, text5, text6, text7, text8;
	private TextView test2, text9, text10, text11, text12, text13, text14, text15, text16;
	private TextView texttotal1, texttotal2, texttotalfinal, output;
	private TextView LtextView, RtextView;
	private EditText limit,limit2;

	private int time = 0;
	private int time2 = 0;
	private float total1 = 0;
	private float total2 = 0;
	private float total1total2 = 1;
    private float x = 0;

	private int n1 = 0; //計算空值
	private int n2 = 0; //計算空值


	private int initial = 0;
	private float[] texttotalfinalTOKEN ={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
			,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//	private String[] token1 = {String.format("0"),String.format("0"),String.format("0"),String.format("0"),String.format("0"),String.format("0"),String.format("0"),String.format("0"),};

	private float limitPercentlow = 0;
	private float limitPercenthigh = 0;
	private float limitPercent2low = 0;
	private float limitPercent2high = 0;

	private int safe1 = 0;
	private int safe11 = 0;

	private int safe2 = 0;
	private int safe22 = 0;

	private SoundPool soundpool;//声明一个SoundPool对象
	private HashMap<Integer,Integer> soundmap=new HashMap<Integer,Integer>();//创建一个HashMap对象

	public MainActivity() {
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();

		dbHelper = new MyDBHelper(this);
		db = dbHelper.getWritableDatabase();


		//创建一个SoundPool对象，该对象可以容纳5个音频流
		soundpool=new SoundPool(2,AudioManager.STREAM_MUSIC,0);

		//将要播放的音频流保存到HashMap对象中
		soundmap.put(1,soundpool.load(this, R.raw.test,1));
		soundmap.put(2,soundpool.load(this, R.raw.test2,1));



		test = (TextView) findViewById(R.id.testtest);
		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);
		text5 = (TextView) findViewById(R.id.text5);
		text6 = (TextView) findViewById(R.id.text6);
		text7 = (TextView) findViewById(R.id.text7);
		text8 = (TextView) findViewById(R.id.text8);

		test2 = (TextView) findViewById(R.id.testtest2);
		text9 = (TextView) findViewById(R.id.text9);
		text10 = (TextView) findViewById(R.id.text10);
		text11 = (TextView) findViewById(R.id.text11);
		text12 = (TextView) findViewById(R.id.text12);
		text13 = (TextView) findViewById(R.id.text13);
		text14 = (TextView) findViewById(R.id.text14);
		text15 = (TextView) findViewById(R.id.text15);
		text16 = (TextView) findViewById(R.id.text16);

		texttotal1 = (TextView) findViewById(R.id.texttotal1);
		texttotal2 = (TextView) findViewById(R.id.texttotal2);
		texttotalfinal = (TextView) findViewById(R.id.texttotalfinal);
		output = (TextView) findViewById(R.id.output);

		LtextView = (TextView) findViewById(R.id.LtextView);
		RtextView = (TextView) findViewById(R.id.RtextView);
	/*
	output = (TextView) findViewById(R.id.output);

		limit = (EditText) findViewById(R.id.limit);
		limit2 = (EditText) findViewById(R.id.limit2);
 */
		spinner1 = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> spinner1list = ArrayAdapter.createFromResource(MainActivity.this,R.array.lunch,
				android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(spinner1list);

		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0,
									   View arg1, int arg2, long arg3) {
				int index = arg0.getSelectedItemPosition();

				switch(index) {
					case 0:
						limitPercentlow = 0;
						LtextView.setBackgroundColor(Color.argb(255,255,255,255));
						System.out.println(limitPercentlow);
						System.out.println(limitPercenthigh);
						break;
					case 1:
						limitPercentlow = (float)0.05;
						limitPercenthigh = (float)0.15;
						System.out.println(limitPercentlow);
						System.out.println(limitPercenthigh);
						break;
					case 2:
						limitPercentlow = (float)0.15;
						limitPercenthigh = (float)0.25;
						System.out.println(limitPercentlow);
						System.out.println(limitPercenthigh);
						break;
					case 3:
						limitPercentlow = (float)0.25;
						limitPercenthigh = (float)0.35;
						System.out.println(limitPercentlow);
						System.out.println(limitPercenthigh);
						break;
					case 4:
						limitPercentlow = (float)0.35;
						limitPercenthigh = (float)0.45;
						System.out.println(limitPercentlow);
						System.out.println(limitPercenthigh);
						break;
					default:
						limitPercentlow = (float)0.45;
						limitPercenthigh = (float)0.55;
						System.out.println(limitPercentlow);
						System.out.println(limitPercenthigh);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});


		spinner2 = (Spinner)findViewById(R.id.spinner2);
		ArrayAdapter<CharSequence> spinner2list = ArrayAdapter.createFromResource(MainActivity.this,R.array.lunch,
				android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(spinner2list);

		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0,
									   View arg1, int arg2, long arg3) {
				int index = arg0.getSelectedItemPosition();

				switch(index) {
					case 0:
						limitPercent2low = 0;
						RtextView.setBackgroundColor(Color.argb(255,255,255,255));
						System.out.println(limitPercent2low);
						System.out.println(limitPercent2high);
						break;
					case 1:
						limitPercent2low = (float)0.05;
						limitPercent2high = (float)0.15;
						System.out.println(limitPercent2low);
						System.out.println(limitPercent2high);
						break;
					case 2:
						limitPercent2low = (float)0.15;
						limitPercent2high = (float)0.25;
						System.out.println(limitPercent2low);
						System.out.println(limitPercent2high);
						break;
					case 3:
						limitPercent2low = (float)0.25;
						limitPercent2high = (float)0.35;
						System.out.println(limitPercent2low);
						System.out.println(limitPercent2high);
						break;
					case 4:
						limitPercent2low = (float)0.35;
						limitPercent2high = (float)0.45;
						System.out.println(limitPercent2low);
						System.out.println(limitPercent2high);
						break;
					default:
						limitPercent2low = (float)0.45;
						limitPercent2high = (float)0.55;
						System.out.println(limitPercent2low);
						System.out.println(limitPercent2high);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});


		serialBegin(115200);



		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();
			}
		});

		buttonScan2 = (Button) findViewById(R.id.buttonScan2);
		buttonScan2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess2();
			}
		});

		buttonScan3 = (Button) findViewById(R.id.buttonScan3);
		buttonScan3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess3();
			}
		});
		buttonScan4 = (Button) findViewById(R.id.buttonScan4);
		buttonScan4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess4();
			}
		});
/*
		limitbutton = (Button) findViewById(R.id.limitbutton);
		limitbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				limitPercent = Float.parseFloat(limit.getText().toString());
				System.out.println(limitPercent);


			}
		});

		cancelbutton = (Button) findViewById(R.id.cancelbutton);
		cancelbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				limitPercent = 0;
				System.out.println(limitPercent);


			}
		});

		limitbutton2 = (Button) findViewById(R.id.limitbutton2);
		limitbutton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				limitPercent2 = Float.parseFloat(limit2.getText().toString());
				System.out.println(limitPercent2);


			}
		});

		cancelbutton2 = (Button) findViewById(R.id.cancelbutton2);
		cancelbutton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				limitPercent2 = 0;
				System.out.println(limitPercent2);


			}
		});
		*/

	}

	protected void onResume() {
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess();
	}

	protected void onStop() {
		super.onStop();
		db.close();
		onStopProcess();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();
	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {
		switch (theConnectionState) {
			case isConnected:
				buttonScan.setText("L已連線");
				break;
			case isConnecting:
				buttonScan.setText("正在連結...");
				break;
			case isToScan:
				buttonScan.setText("連結L藍牙裝置");
				break;
			case isScanning:
				buttonScan.setText("掃描...");
				break;
			case isDisconnecting:
				buttonScan.setText("連結已斷開");
				break;
			default:
				break;
		}
	}

	public void onConectionStateChange2(connectionStateEnum theConnectionState) {
		switch (theConnectionState) {
			case isConnected:
				buttonScan2.setText("R已連線");
				break;
			case isConnecting:
				buttonScan2.setText("正在連結...");
				break;
			case isToScan:
				buttonScan2.setText("連結R藍牙裝置");
				break;
			case isScanning:
				buttonScan2.setText("掃描...");
				break;
			case isDisconnecting:
				buttonScan2.setText("連結已斷開");
				break;
			default:
				break;
		}
	}

	public void setVibrate(int time){
		Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		Log.e("TAG", "vibrator="+ myVibrator.hasVibrator());
		myVibrator.vibrate(time);
	}

	/*		if(token.length <= 8) {
			for (int p = 0; p < token.length; p++) {
				token1[p] = token[p];
				System.out.println(token1[p]);
			}
		}
*/
/*		if(token.length%4 != 0) {
			if(token.length <= 4) {
				for (int p = 0; p < 4; p++) {
					if (token[p] == null) {
						System.out.println(token[p]);
						token[p] = String.format("0");
						System.out.println(token[p]);
					}
				}
			}else{
				for (int p = 4; p < 8; p++) {
					if (token[p] == null) {
						token[p] = String.format("0");
						System.out.println(token[p]);
					}
				}
			}
		}
*/
	//	test.setText("");
	//	String[] token = theString.split(",");
	//	System.out.println(token);

	@Override
	public void onSerialReceived(String theString){
		// TODO Auto-generated method stub
		test.append(theString);
		String[] token = test.getText().toString().split(",|_");
		System.out.println("1yes1");
		if(theString.indexOf("_")!=-1 && token.length%8 == 0) {
			System.out.println("1yes2");
			for (int p = 0; p < token.length; p++) {
				if("".equals(token[p]) ) {
					token[p] = String.format("0");
				}
			}
			LtextView.setBackgroundColor(Color.argb(255,255,255,255));
			if(time < token.length) {
				total1= Float.parseFloat(token[time])+Float.parseFloat(token[time+ 1])+Float.parseFloat(token[time+ 2])+Float.parseFloat(token[time+ 3])+
						Float.parseFloat(token[time+ 4])+Float.parseFloat(token[time+ 5])+Float.parseFloat(token[time+ 6])+Float.parseFloat(token[time+ 7]);
			    if(x != 0 && limitPercentlow == 0) {
                    text1.setText(String.format("%.3f", Float.parseFloat(token[time]) / x));
                    text2.setText(String.format("%.3f", Float.parseFloat(token[time + 1]) / x));
                    text3.setText(String.format("%.3f", Float.parseFloat(token[time + 2]) / x));
                    text4.setText(String.format("%.3f", Float.parseFloat(token[time + 3]) / x));
                    text5.setText(String.format("%.3f", Float.parseFloat(token[time + 4]) / x));
                    text6.setText(String.format("%.3f", Float.parseFloat(token[time + 5]) / x));
                    text7.setText(String.format("%.3f", Float.parseFloat(token[time + 6]) / x));
                    text8.setText(String.format("%.3f", Float.parseFloat(token[time + 7]) / x));
                    texttotal1.setText(String .format("%.3f", total1/x));

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss:SSS");
					Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
					String str = formatter.format(curDate);

					ContentValues cv = new ContentValues();
					cv.put("PTOTAL", String.format("%.3f",x));
                    cv.put("time", str);
					cv.put("LR", String.format("%.3f",total1/x));
					cv.put("SL1", String.format("%.3f",Float.parseFloat(token[time])));
					cv.put("SL2", String.format("%.3f",Float.parseFloat(token[time + 1])));
					cv.put("SL3", String.format("%.3f",Float.parseFloat(token[time + 2])));
					cv.put("SL4", String.format("%.3f",Float.parseFloat(token[time + 3])));
					cv.put("SL5", String.format("%.3f",Float.parseFloat(token[time + 4])));
					cv.put("SL6", String.format("%.3f",Float.parseFloat(token[time + 5])));
					cv.put("SL7", String.format("%.3f",Float.parseFloat(token[time + 6])));
					cv.put("SL8", String.format("%.3f",Float.parseFloat(token[time + 7])));



					long new_id = db.insert(DATABASE_TABLE, null, cv);


					System.out.println("DBConnection"+ new_id + curDate);
					System.out.println(str);
                }else if(x != 0 && limitPercentlow != 0){
					text1.setText(String.format("%.3f", Float.parseFloat(token[time]) / x));
					text2.setText(String.format("%.3f", Float.parseFloat(token[time + 1]) / x));
					text3.setText(String.format("%.3f", Float.parseFloat(token[time + 2]) / x));
					text4.setText(String.format("%.3f", Float.parseFloat(token[time + 3]) / x));
					text5.setText(String.format("%.3f", Float.parseFloat(token[time + 4]) / x));
					text6.setText(String.format("%.3f", Float.parseFloat(token[time + 5]) / x));
					text7.setText(String.format("%.3f", Float.parseFloat(token[time + 6]) / x));
					text8.setText(String.format("%.3f", Float.parseFloat(token[time + 7]) / x));
					texttotal1.setText(String .format("%.3f", total1/x));

					if(total1/x >= limitPercentlow){
						safe1 = 1;
					}
					if(total1/x > 0){
						safe11 = 0;
					}
					if(total1/x > limitPercenthigh){
						setVibrate(300);
						soundpool.play(soundmap.get(1), 1,1,0,0,1);
						LtextView.setBackgroundColor(Color.argb(255,240,9,9));
					}
					if(total1 == 0){
						if(safe1 == 0 && safe11 == 0){
							LtextView.setBackgroundColor(Color.argb(255,68,255,0));
							soundpool.play(soundmap.get(2), 1,1,0,0,1);
						}else if(safe1 == 1){
							safe1 = 0;
						}
						safe11 = 1;
					}

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss:SSS");
					Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
					String str = formatter.format(curDate);

					ContentValues cv = new ContentValues();
					cv.put("PTOTAL", String.format("%.3f",x));
					cv.put("time", str);
					cv.put("LR", String.format("%.3f",total1/x));
					cv.put("SL1", String.format("%.3f",Float.parseFloat(token[time])));
					cv.put("SL2", String.format("%.3f",Float.parseFloat(token[time + 1])));
					cv.put("SL3", String.format("%.3f",Float.parseFloat(token[time + 2])));
					cv.put("SL4", String.format("%.3f",Float.parseFloat(token[time + 3])));
					cv.put("SL5", String.format("%.3f",Float.parseFloat(token[time + 4])));
					cv.put("SL6", String.format("%.3f",Float.parseFloat(token[time + 5])));
					cv.put("SL7", String.format("%.3f",Float.parseFloat(token[time + 6])));
					cv.put("SL8", String.format("%.3f",Float.parseFloat(token[time + 7])));

					long new_id = db.insert(DATABASE_TABLE, null, cv);


					System.out.println("DBConnection"+ new_id + curDate);
					System.out.println(str);


				}

				//time = time+8;
				test.setText(null);
				//System.out.println(test);
			}
		}else if(theString.indexOf("_")!=-1 && token.length > 8){
			test.setText(null);
			System.out.println("n1"+n1++);
		}
	}


	public void onSerialReceived2(String theString){
		// TODO Auto-generated method stub
		test2.append(theString);
		String[] token2 = test2.getText().toString().split(",|_");
		System.out.println("2yes1");
		if(theString.indexOf("_") != -1 && token2.length%8 == 0){
			System.out.println("2yes2");
			for (int p = 0; p < token2.length; p++) {
				if("".equals(token2[p]) ) {
					token2[p] = String.format("0");
				}
			}
			RtextView.setBackgroundColor(Color.argb(255,255,255,255));
			if(time2 < token2.length) {
				total2= Float.parseFloat(token2[time2])+Float.parseFloat(token2[time2+ 1])+Float.parseFloat(token2[time2+ 2])+Float.parseFloat(token2[time2+ 3])+
						Float.parseFloat(token2[time2+ 4])+Float.parseFloat(token2[time2+ 5])+Float.parseFloat(token2[time2+ 6])+Float.parseFloat(token2[time2+ 7]);
			    if(x != 0 && limitPercent2low == 0) {
                    text9.setText(String.format("%.3f", Float.parseFloat(token2[time2]) / x));
                    text10.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 1]) / x));
                    text11.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 2]) / x));
                    text12.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 3]) / x));
                    text13.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 4]) / x));
                    text14.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 5]) / x));
                    text15.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 6]) / x));
                    text16.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 7]) / x));
                    texttotal2.setText(String .format("%.3f", total2/x));

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss:SSS");
					Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
					String str = formatter.format(curDate);

					ContentValues cv = new ContentValues();
					cv.put("PTOTAL", String.format("%.3f",x));
					cv.put("time", str);
					cv.put("LR", String.format("%.3f",total2/x));
					cv.put("SR1", String.format("%.3f",Float.parseFloat(token2[time2])));
					cv.put("SR2", String.format("%.3f",Float.parseFloat(token2[time2 + 1])));
					cv.put("SR3", String.format("%.3f",Float.parseFloat(token2[time2 + 2])));
					cv.put("SR4", String.format("%.3f",Float.parseFloat(token2[time2 + 3])));
					cv.put("SR5", String.format("%.3f",Float.parseFloat(token2[time2 + 4])));
					cv.put("SR6", String.format("%.3f",Float.parseFloat(token2[time2 + 5])));
					cv.put("SR7", String.format("%.3f",Float.parseFloat(token2[time2 + 6])));
					cv.put("SR8", String.format("%.3f",Float.parseFloat(token2[time2 + 7])));


					long new_id = db.insert(DATABASE_TABLE2, null, cv);


					System.out.println("DBConnection22222222"+ new_id + curDate);
					System.out.println(str);
                }else if(x != 0 && limitPercent2low != 0){
					text9.setText(String.format("%.3f", Float.parseFloat(token2[time2]) / x));
					text10.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 1]) / x));
					text11.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 2]) / x));
					text12.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 3]) / x));
					text13.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 4]) / x));
					text14.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 5]) / x));
					text15.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 6]) / x));
					text16.setText(String.format("%.3f", Float.parseFloat(token2[time2 + 7]) / x));
					texttotal2.setText(String .format("%.3f", total2/x));

					if(total2/x >= limitPercent2low){
						safe2 = 1;
					}
					if(total2/x > 0){
						safe22 = 0;
					}
					if(total2/x > limitPercent2high){
						setVibrate(300);
						soundpool.play(soundmap.get(1), 1,1,0,0,1);
						RtextView.setBackgroundColor(Color.argb(255,240,9,9));
					}
					if(total2 == 0){
						if(safe2 == 0 && safe22 == 0){
							RtextView.setBackgroundColor(Color.argb(255,68,255,0));
							soundpool.play(soundmap.get(2), 1,1,0,0,1);
						}else if(safe2 == 1){
							safe2 = 0;
						}
						safe22 = 1;
					}


					SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss:SSS");
					Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
					String str = formatter.format(curDate);

					ContentValues cv = new ContentValues();
					cv.put("PTOTAL", String.format("%.3f",x));
					cv.put("time", str);
					cv.put("LR", String.format("%.3f",total2/x));
					cv.put("SR1", String.format("%.3f",Float.parseFloat(token2[time2])));
					cv.put("SR2", String.format("%.3f",Float.parseFloat(token2[time2 + 1])));
					cv.put("SR3", String.format("%.3f",Float.parseFloat(token2[time2 + 2])));
					cv.put("SR4", String.format("%.3f",Float.parseFloat(token2[time2 + 3])));
					cv.put("SR5", String.format("%.3f",Float.parseFloat(token2[time2 + 4])));
					cv.put("SR6", String.format("%.3f",Float.parseFloat(token2[time2 + 5])));
					cv.put("SR7", String.format("%.3f",Float.parseFloat(token2[time2 + 6])));
					cv.put("SR8", String.format("%.3f",Float.parseFloat(token2[time2 + 7])));


					long new_id = db.insert(DATABASE_TABLE2, null, cv);


					System.out.println("DBConnection22222222"+ new_id + curDate);
					System.out.println(str);
				}

				total1total2 = total1+total2;
				//time2 = time2+8;
				test2.setText(null);
				//System.out.println(test2);


				if(total1 != 0 && total2 != 0 && initial<100){
					texttotalfinalTOKEN[initial] = total1total2;
					initial++;
				}
				if(initial == 100){
					x =( texttotalfinalTOKEN[0] + texttotalfinalTOKEN[1] + texttotalfinalTOKEN[2] + texttotalfinalTOKEN[3] + texttotalfinalTOKEN[4] +
							texttotalfinalTOKEN[5] +texttotalfinalTOKEN[6] + texttotalfinalTOKEN[7]  + texttotalfinalTOKEN[8] + texttotalfinalTOKEN[9] +
							texttotalfinalTOKEN[10] +texttotalfinalTOKEN[11] + texttotalfinalTOKEN[12]  + texttotalfinalTOKEN[13] + texttotalfinalTOKEN[14] +
							texttotalfinalTOKEN[15] +texttotalfinalTOKEN[16] + texttotalfinalTOKEN[17]  + texttotalfinalTOKEN[18] + texttotalfinalTOKEN[19] +
							texttotalfinalTOKEN[20] +texttotalfinalTOKEN[21] + texttotalfinalTOKEN[22]  + texttotalfinalTOKEN[23] + texttotalfinalTOKEN[24] +
							texttotalfinalTOKEN[25] +texttotalfinalTOKEN[26] + texttotalfinalTOKEN[27]  + texttotalfinalTOKEN[28] + texttotalfinalTOKEN[29] +
							texttotalfinalTOKEN[30] +texttotalfinalTOKEN[31] + texttotalfinalTOKEN[32]  + texttotalfinalTOKEN[33] + texttotalfinalTOKEN[34] +
							texttotalfinalTOKEN[35] +texttotalfinalTOKEN[36] + texttotalfinalTOKEN[37]  + texttotalfinalTOKEN[38] + texttotalfinalTOKEN[39] +
							texttotalfinalTOKEN[40] +texttotalfinalTOKEN[41] + texttotalfinalTOKEN[42]  + texttotalfinalTOKEN[43] + texttotalfinalTOKEN[44] +
							texttotalfinalTOKEN[45] +texttotalfinalTOKEN[46] + texttotalfinalTOKEN[47]  + texttotalfinalTOKEN[48] + texttotalfinalTOKEN[49] +
							texttotalfinalTOKEN[50] +texttotalfinalTOKEN[51] + texttotalfinalTOKEN[52]  + texttotalfinalTOKEN[53] + texttotalfinalTOKEN[54] +
							texttotalfinalTOKEN[55] +texttotalfinalTOKEN[56] + texttotalfinalTOKEN[57]  + texttotalfinalTOKEN[58] + texttotalfinalTOKEN[59] +
							texttotalfinalTOKEN[60] +texttotalfinalTOKEN[61] + texttotalfinalTOKEN[62]  + texttotalfinalTOKEN[63] + texttotalfinalTOKEN[64] +
							texttotalfinalTOKEN[65] +texttotalfinalTOKEN[66] + texttotalfinalTOKEN[67]  + texttotalfinalTOKEN[68] + texttotalfinalTOKEN[69] +
							texttotalfinalTOKEN[70] +texttotalfinalTOKEN[71] + texttotalfinalTOKEN[72]  + texttotalfinalTOKEN[73] + texttotalfinalTOKEN[74] +
							texttotalfinalTOKEN[75] +texttotalfinalTOKEN[76] + texttotalfinalTOKEN[77]  + texttotalfinalTOKEN[78] + texttotalfinalTOKEN[79] +
							texttotalfinalTOKEN[80] +texttotalfinalTOKEN[81] + texttotalfinalTOKEN[82]  + texttotalfinalTOKEN[83] + texttotalfinalTOKEN[84] +
							texttotalfinalTOKEN[85] +texttotalfinalTOKEN[86] + texttotalfinalTOKEN[87]  + texttotalfinalTOKEN[88] + texttotalfinalTOKEN[89] +
							texttotalfinalTOKEN[90] +texttotalfinalTOKEN[91] + texttotalfinalTOKEN[92]  + texttotalfinalTOKEN[93] + texttotalfinalTOKEN[94] +
							texttotalfinalTOKEN[95] +texttotalfinalTOKEN[96] + texttotalfinalTOKEN[97]  + texttotalfinalTOKEN[98] + texttotalfinalTOKEN[99] )/100;
					texttotalfinal.setText(String .format("%.3f",x));
					texttotalfinalTOKEN = null;
					initial++;
				}
				System.out.println(initial);
			}
		}else if(theString.indexOf("_")!=-1 && token2.length > 8){
			test2.setText(null);
			System.out.println("n2"+n2++);
		}
	}


	public void buttonScanOnClickProcess3()
	{
		String[] colNames=new String[]{"_id","PTOTAL","time","LR","SL1"};
		String str = "";
		Cursor c = db.query(DATABASE_TABLE, colNames,null, null, null, null,null);
		// 顯示欄位名稱
		for (int i = 0; i < colNames.length; i++)
			str += colNames[i] + "\t\t";
		str += "\n";
		c.moveToFirst();  // 第1筆
		// 顯示欄位值
		for (int i = 0; i < c.getCount(); i++) {
			str += c.getString(c.getColumnIndex(colNames[0])) + "\t\t";
			str += c.getString(1) + "\t\t";
			str += c.getString(2) + "\t\t";
			str += c.getString(3) + "\t\t";
			str += c.getString(4) + "\n";
			c.moveToNext();  // 下一筆
		}
		output.setText(str.toString());
	}
	public void buttonScanOnClickProcess4()
	{
		String[] colNames=new String[]{"_id","PTOTAL","time","LR","SR1"};
		String str = "";
		Cursor c = db.query(DATABASE_TABLE2, colNames,null, null, null, null,null);
		// 顯示欄位名稱
		for (int i = 0; i < colNames.length; i++)
			str += colNames[i] + "\t\t";
		str += "\n";
		c.moveToFirst();  // 第1筆
		// 顯示欄位值
		for (int i = 0; i < c.getCount(); i++) {
			str += c.getString(c.getColumnIndex(colNames[0])) + "\t\t";
			str += c.getString(1) + "\t\t";
			str += c.getString(2) + "\t\t";
			str += c.getString(3) + "\t\t";
			str += c.getString(4) + "\n";
			c.moveToNext();  // 下一筆
		}
		output.setText(str.toString());
	}

}