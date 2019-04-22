package com.dfrobot.angelo.blunobasicdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.*;

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonScan2;

	private TextView test, text1, text2, text3, text4, text5, text6, text7, text8;
	private TextView test2, text9, text10, text11, text12, text13, text14, text15, text16;
	private TextView texttotal1, texttotal2, texttotalfinal;
	private int time = 0;
	private int time2 = 0;
	private float total1 = 1;
	private float total2 = 1;
	private float total1total2 = 1;

	private int initial = 0;
	private float[] texttotalfinalTOKEN ={0,0,0,0,0};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();


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

	@Override
	public void onSerialReceived(String theString){
		// TODO Auto-generated method stub
		test.append(theString);
		String[] token = test.getText().toString().split(",");
		if(token.length%8 == 0) {
			if(time < token.length) {
				text1.setText(String .format("%.3f", Float.parseFloat(token[time])/total1total2));
				text2.setText(String .format("%.3f", Float.parseFloat(token[time+ 1])/total1total2));
				text3.setText(String .format("%.3f", Float.parseFloat(token[time+ 2])/total1total2));
				text4.setText(String .format("%.3f", Float.parseFloat(token[time+ 3])/total1total2));
				text5.setText(String .format("%.3f", Float.parseFloat(token[time+ 4])/total1total2));
				text6.setText(String .format("%.3f", Float.parseFloat(token[time+ 5])/total1total2));
				text7.setText(String .format("%.3f", Float.parseFloat(token[time+ 6])/total1total2));
				text8.setText(String .format("%.3f", Float.parseFloat(token[time+ 7])/total1total2));
				total1= Integer.parseInt(token[time])+Integer.parseInt(token[time+ 1])+Integer.parseInt(token[time+ 2])+Integer.parseInt(token[time+ 3])+
						Integer.parseInt(token[time+ 4])+Integer.parseInt(token[time+ 5])+Integer.parseInt(token[time+ 6])+Integer.parseInt(token[time+ 7]);
				total1total2 = total1+total2;
				texttotal1.setText(String .format("%.3f", total1/total1total2));
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
				text9.setText(String .format("%.3f", Float.parseFloat(token2[time2])/total1total2));
				text10.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 1])/total1total2));
				text11.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 2])/total1total2));
				text12.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 3])/total1total2));
				text13.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 4])/total1total2));
				text14.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 5])/total1total2));
				text15.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 6])/total1total2));
				text16.setText(String .format("%.3f", Float.parseFloat(token2[time2 + 7])/total1total2));
				total2= Integer.parseInt(token2[time2])+Integer.parseInt(token2[time2+ 1])+Integer.parseInt(token2[time2+ 2])+Integer.parseInt(token2[time2+ 3])+
						Integer.parseInt(token2[time2+ 4])+Integer.parseInt(token2[time2+ 5])+Integer.parseInt(token2[time2+ 6])+Integer.parseInt(token2[time2+ 7]);
				total1total2 = total1+total2;
                texttotal2.setText(String .format("%.3f", total2/total1total2));
				time2 = time2+8;
				if(total1 != 1 && total2 != 1 && initial<5){
					texttotalfinalTOKEN[initial] = total1total2;
					initial++;
				}
				if(initial == 5){
					float x =( texttotalfinalTOKEN[0] + texttotalfinalTOKEN[1] + texttotalfinalTOKEN[2] + texttotalfinalTOKEN[3] + texttotalfinalTOKEN[4] )/5;
					texttotalfinal.setText(String .format("%.3f",x));
				}
			}
		}
	}
}



/*
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.

	public class MySQLiteManager extends SQLiteOpenHelper {

		private final static int DB_VERSION = 1; // 資料庫版本


		private final static String DB_NAME = "MySQLite.db"; //資料庫名稱，附檔名為db


		private final static String INFO_TABLE = "valuetable";
		private final static String ROW_ID = "rowId"; //欄位名稱

		private final static String NAME = "name"; //欄位名稱
		private final static String TIME = "time"; //欄位名稱
		private final static String VALUE = "value"; //欄位名稱


		public MySQLiteManager (Context context) {
*/
			/*
			 *   SQLiteOpenHelper
			 * (Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
			 * 通常我們只會傳回context, DB_NAME, DB_VERSION;
			 * factory 我也不大了解作用是什麼。
			 */
/*
			super(context, DB_NAME, null, DB_VERSION);
		}

		// 每次使用將會檢查是否有無資料表，若無，則會建立資料表


		@Override
		public void onCreate(SQLiteDatabase db) {

			//SQLite 所支援屬性帳面上的不多，但事實上也是能夠讀取一些其它屬性

			String createTable = "CREATE TABLE " + INFO_TABLE+ " ("
					+ ROW_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "  //這個屬性可以讓每次新增一筆資料，自動累加

					+ NAME + " TEXT, "
					+ TIME + " TEXT, "
					+ VALUE + " TEXT);";

			db.execSQL(createTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int i, int i2) {

		}
		//以下為新增方法
		public void insterInfo(SQLiteDatabase db, String name, String time, String value) {

			ContentValues contentValues = new ContentValues();
			contentValues.put(NAME, name);
			contentValues.put(TIME, time);
			contentValues.put(VALUE, value);

			db.insert(INFO_TABLE, null, contentValues);
		}

		//以下為刪除法
		public void removeInfo(SQLiteDatabase db, String rowId) {
			db.delete(INFO_TABLE, ROW_ID + "=?", new String[]{rowId});
		}
	}

*/
