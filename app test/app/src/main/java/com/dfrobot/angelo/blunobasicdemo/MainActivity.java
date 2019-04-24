package com.dfrobot.angelo.blunobasicdemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Vibrator;

public class MainActivity  extends BlunoLibrary {


	private static String DATABASE_TABLE = "ONE";
	private static String DATABASE_TABLE2 = "TWO";
	private SQLiteDatabase db;
	private MyDBHelper dbHelper;



	private Button buttonScan;
	private Button buttonScan2;

	private Button buttonScan3;

	private Button limitbutton;
	private Button cancelbutton;

	private TextView test, text1, text2, text3, text4, text5, text6, text7, text8;
	private TextView test2, text9, text10, text11, text12, text13, text14, text15, text16;
	private TextView texttotal1, texttotal2, texttotalfinal, output;
	private EditText limit;
	private int time = 0;
	private int time2 = 0;
	private float total1 = 1;
	private float total2 = 1;
	private float total1total2 = 1;
    private float x = 0;

	private int initial = 0;
	private float[] texttotalfinalTOKEN ={0,0,0,0,0};

	private float limitPercent = 0;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();

		dbHelper = new MyDBHelper(this);
		db = dbHelper.getWritableDatabase();


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
		limit = (EditText) findViewById(R.id.limit);

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
				buttonScan.setText("1號藍牙裝置已連線");
				break;
			case isConnecting:
				buttonScan.setText("正在連結中");
				break;
			case isToScan:
				buttonScan.setText("連結1號藍牙裝置");
				break;
			case isScanning:
				buttonScan.setText("Scanning");
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
				buttonScan2.setText("Connected");
				break;
			case isConnecting:
				buttonScan2.setText("Connecting");
				break;
			case isToScan:
				buttonScan2.setText("Scan");
				break;
			case isScanning:
				buttonScan2.setText("Scanning");
				break;
			case isDisconnecting:
				buttonScan2.setText("isDisconnecting");
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

	@Override
	public void onSerialReceived(String theString){
		// TODO Auto-generated method stub
		test.append(theString);
		String[] token = test.getText().toString().split(",");
		if(token.length%8 == 0) {
			if(time < token.length) {
			    if(x != 0 && limitPercent == 0) {
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
					cv.put("x", x);
					cv.put("LR", 1);
                    cv.put("time", str);
					cv.put("value", total1/x);
					long new_id = db.insert(DATABASE_TABLE, null, cv);


					System.out.println("DBConnection"+ new_id + curDate);
					System.out.println(str);
                }else if(x != 0 && limitPercent != 0){
					text1.setText(String.format("%.3f", Float.parseFloat(token[time]) / x));
					text2.setText(String.format("%.3f", Float.parseFloat(token[time + 1]) / x));
					text3.setText(String.format("%.3f", Float.parseFloat(token[time + 2]) / x));
					text4.setText(String.format("%.3f", Float.parseFloat(token[time + 3]) / x));
					text5.setText(String.format("%.3f", Float.parseFloat(token[time + 4]) / x));
					text6.setText(String.format("%.3f", Float.parseFloat(token[time + 5]) / x));
					text7.setText(String.format("%.3f", Float.parseFloat(token[time + 6]) / x));
					text8.setText(String.format("%.3f", Float.parseFloat(token[time + 7]) / x));
					texttotal1.setText(String .format("%.3f", total1/x));

					if(total1/x > limitPercent){
						setVibrate(5000);
					}

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss:SSS");
					Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
					String str = formatter.format(curDate);

					ContentValues cv = new ContentValues();
					cv.put("x", x);
					cv.put("LR", 1);
					cv.put("time", str);
					cv.put("value", total1/x);
					long new_id = db.insert(DATABASE_TABLE, null, cv);


					System.out.println("DBConnection"+ new_id + curDate);
					System.out.println(str);


				}
				total1= Integer.parseInt(token[time])+Integer.parseInt(token[time+ 1])+Integer.parseInt(token[time+ 2])+Integer.parseInt(token[time+ 3])+
						Integer.parseInt(token[time+ 4])+Integer.parseInt(token[time+ 5])+Integer.parseInt(token[time+ 6])+Integer.parseInt(token[time+ 7]);
				time = time+8;
			}
		}
	}
	public void onSerialReceived2(String theString){
		// TODO Auto-generated method stub
		test2.append(theString);
		String[] token2 = test2.getText().toString().split(",");
		if(token2.length%8 == 0) {
			if(time2 < token2.length) {
			    if(x != 0) {
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
					cv.put("x", x);
					cv.put("LR", 2);
					cv.put("time", str);
					cv.put("value", total2/x);
					long new_id = db.insert(DATABASE_TABLE2, null, cv);


					System.out.println("DBConnection22222222"+ new_id + curDate);
					System.out.println(str);
                }
				total2= Integer.parseInt(token2[time2])+Integer.parseInt(token2[time2+ 1])+Integer.parseInt(token2[time2+ 2])+Integer.parseInt(token2[time2+ 3])+
						Integer.parseInt(token2[time2+ 4])+Integer.parseInt(token2[time2+ 5])+Integer.parseInt(token2[time2+ 6])+Integer.parseInt(token2[time2+ 7]);
				total1total2 = total1+total2;
				time2 = time2+8;
				if(total1 != 1 && total2 != 1 && initial<5){
					texttotalfinalTOKEN[initial] = total1total2;
					initial++;
				}
				if(initial == 5){
					x =( texttotalfinalTOKEN[0] + texttotalfinalTOKEN[1] + texttotalfinalTOKEN[2] + texttotalfinalTOKEN[3] + texttotalfinalTOKEN[4] )/5;
					texttotalfinal.setText(String .format("%.3f",x));
				}
			}
		}
	}
	public void buttonScanOnClickProcess3()
	{
		String[] colNames=new String[]{"_id","x","LR","time","value"};
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
}