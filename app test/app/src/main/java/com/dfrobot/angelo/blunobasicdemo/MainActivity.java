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
	private TextView test2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();
		//onCreate Process by BlunoLibrary

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

		serialBegin(115200);    //set the default baud rate to 115200//藍芽連結率設定//與硬體設備鮑褒率相同                                                //set the Uart Baudrate on BLE chip to 115200


		//initial the EditText of the sending data

		//initial the button for sending the data


		buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device
		buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
			}
		});
	}

	protected void onResume() {
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();                                                        //onResume Process by BlunoLibrary
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess();                                                        //onPause Process by BlunoLibrary
	}

	protected void onStop() {
		super.onStop();
		onStopProcess();                                                        //onStop Process by BlunoLibrary
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {                                            //Four connection state
			case isConnected:
				buttonScan.setText("Connected");
				break;
			case isConnecting:
				buttonScan.setText("Connecting");
				break;
			case isToScan:
				buttonScan.setText("Scan");
				break;
			case isScanning:
				buttonScan.setText("Scanning");
				break;
			case isDisconnecting:
				buttonScan.setText("isDisconnecting");
				break;
			default:
				break;
		}
	}

	@Override
	public void onSerialReceived(String theString){                            //Once connection data received, this function will be called
		// TODO Auto-generated method stub
		test.append(theString);                            //append the text into the EditText
		String[] token = test.getText().toString().split(",");
		//for(int i = 0 ; i < token.length ; i=i+6){
		if(token.length%8 == 0) {
			for (int i = 0; i < token.length; i = i + 8) {
				text1.setText(token[i]);
				text2.setText(token[i + 1]);
				text3.setText(token[i + 2]);
				text4.setText(token[i + 3]);
				text5.setText(token[i + 4]);
				text6.setText(token[i + 5]);
				text7.setText(token[i + 6]);
				text8.setText(token[i + 7]);
			}
		}
	}
	public void onSerialReceived2(String theString){                            //Once connection data received, this function will be called
		// TODO Auto-generated method stub
		test2.append(theString);                            //append the text into the EditText

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
