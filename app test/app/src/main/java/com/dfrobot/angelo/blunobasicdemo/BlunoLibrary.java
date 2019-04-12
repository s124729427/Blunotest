package com.dfrobot.angelo.blunobasicdemo;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public abstract  class BlunoLibrary  extends Activity{

	private Context mainContext=this;


	
//	public BlunoLibrary(Context theContext) {
//		
//		mainContext=theContext;
//	}

	public abstract void onConectionStateChange(connectionStateEnum theconnectionStateEnum);
	public abstract void onConectionStateChange2(connectionStateEnum theconnectionStateEnum);
	public abstract void onSerialReceived(String theString);
	public abstract void onSerialReceived2(String theString);
	public void serialSend(String theString){
		if (mConnectionState == connectionStateEnum.isConnected) {
			mSCharacteristic.setValue(theString);
			mBluetoothLeService.writeCharacteristic(mSCharacteristic);
		}
	}

	public void serialSend2(String theString){
		if (mConnectionState2 == connectionStateEnum.isConnected) {
			mSCharacteristic.setValue(theString);
			mBluetoothLeService.writeCharacteristic(mSCharacteristic);
		}
	}
	
	private int mBaudrate=115200;	//set the default baud rate to 115200                                      //藍芽連結胞率設定(頻率設定)//與硬體設備鮑率相同
	private String mPassword="AT+PASSWOR=DFRobot\r\n";
	
	
	private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
	
//	byte[] mBaudrateBuffer={0x32,0x00,(byte) (mBaudrate & 0xFF),(byte) ((mBaudrate>>8) & 0xFF),(byte) ((mBaudrate>>16) & 0xFF),0x00};;
	
	
	public void serialBegin(int baud){
		mBaudrate=baud;
		mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
	}
	
	
	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}
    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
    BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private LeDeviceListAdapter mLeDeviceListAdapter=null;
	private LeDeviceListAdapter mLeDeviceListAdapter2=null;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning =false;
	AlertDialog mScanDeviceDialog;
	AlertDialog mScanDeviceDialog2;
    private String mDeviceName;
	private String mDeviceName2;
    private String mDeviceAddress;
	private String mDeviceAddress2;
	public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
	public connectionStateEnum mConnectionState = connectionStateEnum.isNull;
	public connectionStateEnum mConnectionState2 = connectionStateEnum.isNull;
	private static final int REQUEST_ENABLE_BT = 1;

	private Handler mHandler= new Handler();
	
	public boolean mConnected = false;

    private final static String TAG = BlunoLibrary.class.getSimpleName();

    private Runnable mConnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
        	if(mConnectionState==connectionStateEnum.isConnecting)
			mConnectionState=connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState);
			mBluetoothLeService.close();
			if(mConnectionState2==connectionStateEnum.isConnecting)
				mConnectionState2=connectionStateEnum.isToScan;
			onConectionStateChange2(mConnectionState2);
			mBluetoothLeService.close();
		}};
		
    private Runnable mDisonnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
        	if(mConnectionState==connectionStateEnum.isDisconnecting)
			mConnectionState=connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState);
			mBluetoothLeService.close();
			if(mConnectionState2==connectionStateEnum.isDisconnecting)
				mConnectionState2=connectionStateEnum.isToScan;
			onConectionStateChange2(mConnectionState2);
			mBluetoothLeService.close();
		}};
	                                                                                                                //uuid為世界唯一獨有不重複的識別碼//包含service或Characteristic都不重複
	public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";                          //裝有感測器數值的Characteristic
	public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";                          //不確定功能的Characteristic
    public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";                //裝有device name的Characteristic
	
    public void onCreateProcess()
    {
    	if(!initiate())   //先呼叫下面的initiate()//拿到BluetoothManager與BluetoothAdapt//
		{
			Toast.makeText(mainContext, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			((Activity) mainContext).finish();
		}


		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);            //呼叫mServiceConnection//BluetoothLeService的initialize()
        
		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();       // LeDeviceListAdapter()定義在最下面

		mLeDeviceListAdapter2 = new LeDeviceListAdapter();     // LeDeviceListAdapter()定義在最下面

		// Initializes and show the scan Device Dialog
		mScanDeviceDialog = new AlertDialog.Builder(mainContext)            //初始化對話小視窗  //設定觸及監聽事件OnClickListener()  //自訂義對話框使用setAdapter()
		.setTitle("BLE Device Scan...").setAdapter(mLeDeviceListAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				final BluetoothDevice device = mLeDeviceListAdapter.getDevice(which);        //確認觸及小視窗裝置位置對應的device
				if (device == null)
					return;
				scanLeDevice(false);                                                 //呼叫scanLeDevice停止掃描

		        if(device.getName()==null || device.getAddress()==null)                        //如果有一個是null，更改buttonj文字改成isToScan
		        {
		        	mConnectionState=connectionStateEnum.isToScan;
		        	onConectionStateChange(mConnectionState);
		        }
		        else{                                                                         //都有的話 //裝進mDeviceName與mDeviceAddress

					System.out.println("onListItemClick " + device.getName().toString());                                         //串流顯示  沒有實際效果  //debug專用

					System.out.println("Device Name:"+device.getName() + "   " + "Device Name:" + device.getAddress());

					mDeviceName=device.getName().toString();
					mDeviceAddress=device.getAddress().toString();

		        	if (mBluetoothLeService.connect(mDeviceAddress)) {                      //獲得connect address//跳到bluetoothleservice.java的connect()功能 //進行裝置連結
				        Log.d(TAG, "Connect request success");
			        	mConnectionState=connectionStateEnum.isConnecting;                     //更改buttonj文字改成isConnecting
			        	onConectionStateChange(mConnectionState);
			            mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
		        	}

			        else {
				        Log.d(TAG, "Connect request fail");
			        	mConnectionState=connectionStateEnum.isToScan;
			        	onConectionStateChange(mConnectionState);
					}
		        }
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {        //斷線  或  取消連線的監聽  //還未認真看   //但與其他城市連動性不大   //連帶影響關西不大   //可暫時忽略

			@Override
			public void onCancel(DialogInterface arg0) {
				System.out.println("mBluetoothAdapter.stopLeScan");

				mConnectionState = connectionStateEnum.isToScan;
				onConectionStateChange(mConnectionState);
				mScanDeviceDialog.dismiss();

				scanLeDevice(false);
			}
		}).create();

		//第二個Dialog
		mScanDeviceDialog2 = new AlertDialog.Builder(mainContext)               //初始化對話小視窗  //設定觸及監聽事件OnClickListener()  //自訂義對話框使用setAdapter()
				.setTitle("BLE Device Scan...").setAdapter(mLeDeviceListAdapter2, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						final BluetoothDevice device = mLeDeviceListAdapter2.getDevice(which);
						if (device == null)
							return;
						scanLeDevice(false);//如果device是null //呼叫scanLeDevice停止掃描

						if(device.getName()==null || device.getAddress()==null)
						{
							mConnectionState2=connectionStateEnum.isToScan;
							onConectionStateChange2(mConnectionState2);
						}
						else{

							System.out.println("onListItemClick " + device.getName().toString());

							System.out.println("Device Name:"+device.getName() + "   " + "Device Name:" + device.getAddress());

							mDeviceName2=device.getName().toString();
							mDeviceAddress2=device.getAddress().toString();

							if (mBluetoothLeService.connect(mDeviceAddress2)) {
								Log.d(TAG, "Connect request success");
								mConnectionState2=connectionStateEnum.isConnecting;
								onConectionStateChange2(mConnectionState2);
								mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
							}

							else {
								Log.d(TAG, "Connect request fail");
								mConnectionState2=connectionStateEnum.isToScan;
								onConectionStateChange2(mConnectionState2);
							}
						}
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						System.out.println("mBluetoothAdapter.stopLeScan");

						mConnectionState2 = connectionStateEnum.isToScan;
						onConectionStateChange2(mConnectionState2);
						mScanDeviceDialog2.dismiss();

						scanLeDevice2(false);
					}
				}).create();

		
    }

    
    
    public void onResumeProcess() {   //初始化
    	System.out.println("BlUNOActivity onResume");
		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		//確認获取 BluetoothAdapter
		if (!mBluetoothAdapter.isEnabled()) {                                                               //確認裝置是否支援藍芽  //開啟裝置藍芽的相關操做
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				((Activity) mainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	    mainContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());                     //註冊mGattUpdateReceiver//還有對應的IntentFilter  //IntentFilter定義在下面
		mainContext.registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter2());                  //註冊mGattUpdateReceiver//還有對應的IntentFilter  //IntentFilter定義在下面
      //開始顯示起始界面
	}
    

    public void onPauseProcess() {                               //跳轉activity時會呼叫 //清除取消所有城市暫存與使用資源
    	System.out.println("BLUNOActivity onPause");
		scanLeDevice(false);
		mainContext.unregisterReceiver(mGattUpdateReceiver);   //對應onResumeProcess()//要在這邊取消註冊
		mainContext.unregisterReceiver(mGattUpdateReceiver2);   //對應onResumeProcess()//要在這邊取消註冊
		mLeDeviceListAdapter.clear();                            //清除mLeDeviceListAdapter
        mLeDeviceListAdapter2.clear();
    	mConnectionState=connectionStateEnum.isToScan;
		mConnectionState2=connectionStateEnum.isToScan;
    	onConectionStateChange(mConnectionState);
		onConectionStateChange2(mConnectionState2);
		mScanDeviceDialog.dismiss();
		mScanDeviceDialog2.dismiss();
		if(mBluetoothLeService!=null)
		{
			mBluetoothLeService.disconnect();                                     //藍芽連線取消
            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);

//			mBluetoothLeService.close();
		}
		mSCharacteristic=null;

	}

	
	public void onStopProcess() {
		System.out.println("MiUnoActivity onStop");
		if(mBluetoothLeService!=null)
		{
//			mBluetoothLeService.disconnect();
//            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);
        	mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
			mBluetoothLeService.close();
		}
		mSCharacteristic=null;
	}

	public void onDestroyProcess() {
        mainContext.unbindService(mServiceConnection);
        mBluetoothLeService = null;
	}
	
	public void onActivityResultProcess(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			((Activity) mainContext).finish();
			return;
		}
	}

	boolean initiate()                                                    //android城市最開始步驟onCreateProcess時有呼叫使用 //取的BluetoothAdapter
	{
		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!mainContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}
		
		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) mainContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();                              //取的BluetoothAdapter
	
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}
	
	 // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {                          //廣播器//負責接收intent訊息
        @SuppressLint("DefaultLocale")
		@Override
        public void onReceive(Context context, Intent intent) {
        	final String action = intent.getAction();
 //           System.out.println("mGattUpdateReceiver->onReceive->action="+action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {                           //比對intent中夾帶命令已進行動作
                mConnected = true;
            	mHandler.removeCallbacks(mConnectingOverTimeRunnable);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mConnectionState = connectionStateEnum.isToScan;
                onConectionStateChange(mConnectionState);
            	mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
            	mBluetoothLeService.close();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	for (BluetoothGattService gattService : mBluetoothLeService.getSupportedGattServices()) {
            		System.out.println("ACTION_GATT_SERVICES_DISCOVERED  "+
            				gattService.getUuid().toString());
            	}
            	getGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {                                         //如命令為ACTION_DATA_AVAILABLE
            	if(mSCharacteristic==mModelNumberCharacteristic)                                                                       //取得裝置Characteristic
            	{
            		if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
						mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, false);                        //設定裝置Characteristic監聽(未確定)
						mSCharacteristic=mCommandCharacteristic;
						mSCharacteristic.setValue(mPassword);
						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic.setValue(mBaudrateBuffer);
						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic=mSerialPortCharacteristic;
						mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);                          //設定裝置Characteristic監聽(未確定)
						mConnectionState = connectionStateEnum.isConnected;
						onConectionStateChange(mConnectionState);
						
					}
            		else {
            			Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
                        mConnectionState = connectionStateEnum.isToScan;
                        onConectionStateChange(mConnectionState);
					}
            	}

            	else if (mSCharacteristic==mSerialPortCharacteristic) {
					//傳DATA到顯示頁面
					String theString = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);                                        //取得挾帶的EXTRA_DATA //Characteristic的value
            		onSerialReceived(theString);                                                                                         //呼叫mainactivity中onSerialReceived//顯示在手機畫面中
				}
            	
            
            	System.out.println("displayData "+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));                            //串流debug使用

//            	mPlainProtocol.mReceivedframe.append(intent.getStringExtra(BluetoothLeService.EXTRA_DATA)) ;
//            	System.out.print("mPlainProtocol.mReceivedframe:");
//            	System.out.println(mPlainProtocol.mReceivedframe.toString());

            	
            }
        }
    };

	private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
		@SuppressLint("DefaultLocale")
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			//           System.out.println("mGattUpdateReceiver->onReceive->action="+action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				mHandler.removeCallbacks(mConnectingOverTimeRunnable);

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				mConnectionState2 = connectionStateEnum.isToScan;
				onConectionStateChange2(mConnectionState2);
				mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
				mBluetoothLeService.close();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				for (BluetoothGattService gattService : mBluetoothLeService.getSupportedGattServices()) {
					System.out.println("ACTION_GATT_SERVICES_DISCOVERED  "+
							gattService.getUuid().toString());
				}
				getGattServices2(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if(mSCharacteristic==mModelNumberCharacteristic)
				{
					if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
						mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, false);
						mSCharacteristic=mCommandCharacteristic;
						mSCharacteristic.setValue(mPassword);
						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic.setValue(mBaudrateBuffer);
						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic=mSerialPortCharacteristic;
						mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
						mConnectionState2 = connectionStateEnum.isConnected;
						onConectionStateChange2(mConnectionState2);

					}
					else {
						Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
						mConnectionState2 = connectionStateEnum.isToScan;
						onConectionStateChange2(mConnectionState2);
					}
				}

				else if (mSCharacteristic==mSerialPortCharacteristic) {
					//傳DATA到顯示頁面
					String theString = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
					onSerialReceived2(theString);
				}


				System.out.println("displayData "+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

//            	mPlainProtocol.mReceivedframe.append(intent.getStringExtra(BluetoothLeService.EXTRA_DATA)) ;
//            	System.out.print("mPlainProtocol.mReceivedframe:");
//            	System.out.println(mPlainProtocol.mReceivedframe.toString());


			}
		}
	};
	
    void buttonScanOnClickProcess()                                     //第一個按鈕的動作
    {
    	switch (mConnectionState) {
		case isNull:                                                     //mConnectionState預設是isnull//所以按完按鈕會線執行case isNull//然後開啟藍芽掃描//顯示mScanDeviceDialog
			mConnectionState=connectionStateEnum.isScanning;
			onConectionStateChange(mConnectionState);                     //改變按鈕顯示文字
			scanLeDevice(true);                                 //呼叫scanLeDevice進行藍芽裝置掃描
			mScanDeviceDialog.show();                                   //呼叫對話小視窗Dialog顯示
			break;

		case isToScan:
			mConnectionState=connectionStateEnum.isScanning;
			onConectionStateChange(mConnectionState);
			scanLeDevice(true);
			mScanDeviceDialog.show();
			break;
		case isScanning:
			
			break;

		case isConnecting:
			
			break;
		case isConnected:                                                                          //如已經連線//再按一次按鈕會依據狀態為isConnected//段開連線
			mBluetoothLeService.disconnect();
            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);

//			mBluetoothLeService.close();
			mConnectionState=connectionStateEnum.isDisconnecting;
			onConectionStateChange(mConnectionState);
			break;
		case isDisconnecting:
			
			break;

		default:
			break;
		}
    }
	void buttonScanOnClickProcess2()
	{
		switch (mConnectionState2) {
			case isNull:
				mConnectionState2=connectionStateEnum.isScanning;
				onConectionStateChange2(mConnectionState2);
				scanLeDevice2(true);
				mScanDeviceDialog2.show();
				break;

			case isToScan:
				mConnectionState2=connectionStateEnum.isScanning;
				onConectionStateChange2(mConnectionState2);
				scanLeDevice2(true);
				mScanDeviceDialog2.show();
				break;
			case isScanning:

				break;

			case isConnecting:

				break;
			case isConnected:
				mBluetoothLeService.disconnect();
				mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);

//			mBluetoothLeService.close();
				mConnectionState2=connectionStateEnum.isDisconnecting;
				onConectionStateChange2(mConnectionState2);
				break;
			case isDisconnecting:

				break;

			default:
				break;
		}
	}
    
	void scanLeDevice(final boolean enable) {                                     //如無裝置連結  //按下button後呼叫進行藍芽掃苗
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			System.out.println("mBluetoothAdapter.startLeScan");
			
			if(mLeDeviceListAdapter != null)                                      //清除mLeDeviceListAdapter
			{
				mLeDeviceListAdapter.clear();
				mLeDeviceListAdapter.notifyDataSetChanged();
			}
			
			if(!mScanning)
			{
				mScanning = true;
				mBluetoothAdapter.startLeScan(mLeScanCallback);                  //開始掃描
				//开始扫描
			}
		} else {
			if(mScanning)
			{
				mScanning = false;
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}
	}
	void scanLeDevice2(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			System.out.println("mBluetoothAdapter.startLeScan");

			if(mLeDeviceListAdapter2 != null)
			{
				mLeDeviceListAdapter2.clear();
				mLeDeviceListAdapter2.notifyDataSetChanged();
			}

			if(!mScanning)
			{
				mScanning = true;
				mBluetoothAdapter.startLeScan(mLeScanCallback2);
				//开始扫描
			}
		} else {
			if(mScanning)
			{
				mScanning = false;
				mBluetoothAdapter.stopLeScan(mLeScanCallback2);
			}
		}
	}
	
	// Code to manage Service lifecycle.
   	 ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("mServiceConnection onServiceConnected");
        	mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();                //取得mbluetoothleservice  與初始化
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                ((Activity) mainContext).finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {                         //裝置斷線  //清除mBluetoothLeService
        	System.out.println("mServiceConnection onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {     //scanLeDevice的回調  //還未理解

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			((Activity) mainContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					System.out.println("mLeScanCallback onLeScan run ");
					mLeDeviceListAdapter.addDevice(device);                                               //嫁入裝置到列表
					mLeDeviceListAdapter.notifyDataSetChanged();                                         //可能為設定數值改變通知之類的
				}
			});
		}
	};

	private BluetoothAdapter.LeScanCallback mLeScanCallback2 = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
							 byte[] scanRecord) {
			((Activity) mainContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					System.out.println("mLeScanCallback onLeScan run ");
					mLeDeviceListAdapter2.addDevice(device);
					mLeDeviceListAdapter2.notifyDataSetChanged();
				}
			});
		}
	};
	
    private void getGattServices(List<BluetoothGattService> gattServices) {                   //連線到裝置之後獲取裝置的服務Service和服務對應的Characteristic
        if (gattServices == null) return;                                                     //算是塞選出需要的service與Characteristic
        String uuid = null;
        mModelNumberCharacteristic=null;
        mSerialPortCharacteristic=null;
        mCommandCharacteristic=null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            System.out.println("displayGattServices + uuid="+uuid);
            
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(ModelNumberStringUUID)){
                	mModelNumberCharacteristic=gattCharacteristic;
                	System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
                }
                else if(uuid.equals(SerialPortUUID)){
                	mSerialPortCharacteristic = gattCharacteristic;
                	System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
                else if(uuid.equals(CommandUUID)){
                	mCommandCharacteristic = gattCharacteristic;
                	System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
            }
            mGattCharacteristics.add(charas);
        }
        
        if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
			Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
            mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
		}
        else {
        	mSCharacteristic=mModelNumberCharacteristic;
        	mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
        	mBluetoothLeService.readCharacteristic(mSCharacteristic);
		}
        
    }

	private void getGattServices2(List<BluetoothGattService> gattServices) {                                //連線到裝置之後獲取裝置的服務(Service)和服務對應的Characteristic
		if (gattServices == null) return;
		String uuid = null;
		mModelNumberCharacteristic=null;
		mSerialPortCharacteristic=null;
		mCommandCharacteristic=null;
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			uuid = gattService.getUuid().toString();
			System.out.println("displayGattServices + uuid="+uuid);

			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas =
					new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				uuid = gattCharacteristic.getUuid().toString();
				if(uuid.equals(ModelNumberStringUUID)){
					mModelNumberCharacteristic=gattCharacteristic;
					System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
				}
				else if(uuid.equals(SerialPortUUID)){
					mSerialPortCharacteristic = gattCharacteristic;
					System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
				}
				else if(uuid.equals(CommandUUID)){
					mCommandCharacteristic = gattCharacteristic;
					System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
				}
			}
			mGattCharacteristics.add(charas);
		}

		if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
			Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
			mConnectionState2 = connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState2);
		}
		else {
			mSCharacteristic=mModelNumberCharacteristic;
			mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
			mBluetoothLeService.readCharacteristic(mSCharacteristic);
		}

	}

    
    private static IntentFilter makeGattUpdateIntentFilter() {                              //定義IntentFilter內容 //挾帶傳送的文字之類的  //會在廣播器broadcast中進行比對出對應的動作
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    private static IntentFilter makeGattUpdateIntentFilter2() {                             //定義IntentFilter內容 //挾帶傳送的文字之類的  //會在廣播器broadcast中進行比對出對應的動作
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter2.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter2.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter2.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter2;
    }


	
	private class LeDeviceListAdapter extends BaseAdapter {                        //小視窗定義
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator =  ((Activity) mainContext).getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				System.out.println("mInflator.inflate  getView");
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}
	}


}
