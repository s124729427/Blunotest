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

	public abstract void onConectionStateChange(connectionStateEnum theconnectionStateEnum);
	public abstract void onSerialReceived(String theString);
	public abstract void onSerialReceived2(String theString);
	public void serialSend(String theString){
		if (mConnectionState == connectionStateEnum.isConnected) {
			mSCharacteristic.setValue(theString);
			mBluetoothLeService.writeCharacteristic(mSCharacteristic);
		}
	}
	
	private int mBaudrate=115200;	//set the default baud rate to 115200//藍芽連結率設定//與硬體設備鮑褒率相同
	private String mPassword="AT+PASSWOR=DFRobot\r\n";

	private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";

	public void serialBegin(int baud){
		mBaudrate=baud;
		mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
	}

    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
    BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning =false;
	AlertDialog mScanDeviceDialog;
    private String mDeviceName;
    private String mDeviceAddress;
	public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
	public connectionStateEnum mConnectionState = connectionStateEnum.isNull;
	private static final int REQUEST_ENABLE_BT = 1;

	private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

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
		}};
		
    private Runnable mDisonnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
        	if(mConnectionState==connectionStateEnum.isDisconnecting)
			mConnectionState=connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState);
			mBluetoothLeService.close();
		}};
    
	public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
	public static final String address1="A4:D5:78:0D:93:33";
	public static final String address2="A4:D5:78:0D:01:D4";
	private boolean address1blooean = true;
	private boolean address2blooean = true;
	
    public void onCreateProcess()
    {
    	if(!initiate())
		{
			Toast.makeText(mainContext, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			((Activity) mainContext).finish();
		}
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    
    public void onResumeProcess() {
    	System.out.println("BlUNOActivity onResume");
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				((Activity) mainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	    mainContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
    

    public void onPauseProcess() {
    	System.out.println("BLUNOActivity onPause");
		scanLeDevice(false);
		mainContext.unregisterReceiver(mGattUpdateReceiver);
		//mLeDeviceListAdapter.clear();
    	mConnectionState=connectionStateEnum.isToScan;
    	onConectionStateChange(mConnectionState);
		mScanDeviceDialog.dismiss();
		if(mBluetoothLeService!=null)
		{
			mBluetoothLeService.disconnect();
            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);
		}
		mSCharacteristic=null;
	}

	
	public void onStopProcess() {
		System.out.println("MiUnoActivity onStop");
		if(mBluetoothLeService!=null)
		{
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
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			((Activity) mainContext).finish();
			return;
		}
	}

	boolean initiate()
	{
		if (!mainContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) mainContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
		@Override
        public void onReceive(Context context, Intent intent) {
        	final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            	mHandler.removeCallbacks(mConnectingOverTimeRunnable);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mConnectionState = connectionStateEnum.isToScan;
                onConectionStateChange(mConnectionState);
            	mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
            	mBluetoothLeService.close();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	getGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            if (mSCharacteristic==mSerialPortCharacteristic) {
					mConnectionState = connectionStateEnum.isConnected;
					onConectionStateChange(mConnectionState);
					//傳DATA到顯示頁面
					String theString = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
					String theString2 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA2);
					if(theString != null) {
						onSerialReceived(theString);
						System.out.println("displayData "+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
					}else if(theString2 != null){
						onSerialReceived2(theString2);
						System.out.println("displayData "+intent.getStringExtra(BluetoothLeService.EXTRA_DATA2));
					}
				}
               // mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
               // mBluetoothLeService.readCharacteristic(mSCharacteristic);
            }
        }
    };
	
    void buttonScanOnClickProcess()
    {
    	switch (mConnectionState) {
		case isNull:
			mConnectionState=connectionStateEnum.isScanning;
			onConectionStateChange(mConnectionState);
			scanLeDevice(true);
			break;
		case isToScan:
			mConnectionState=connectionStateEnum.isScanning;
			onConectionStateChange(mConnectionState);
			scanLeDevice(true);
			break;
		case isScanning:
			
			break;

		case isConnecting:
			
			break;
		case isConnected:
			mBluetoothLeService.disconnect();
            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);
			mConnectionState=connectionStateEnum.isDisconnecting;
			onConectionStateChange(mConnectionState);
			break;
		case isDisconnecting:
			
			break;

		default:
			break;
		}
    	
    	
    }
    
	void scanLeDevice(final boolean enable) {
		if (enable) {
			System.out.println("mBluetoothAdapter.startLeScan");
			if(!mScanning)
			{
				mScanning = true;
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			}
		} else {
			if(mScanning)
			{
				mScanning = false;
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}
	}

   	 ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("mServiceConnection onServiceConnected");
        	mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                ((Activity) mainContext).finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	System.out.println("mServiceConnection onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			((Activity) mainContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					System.out.println("mLeScanCallback onLeScan run ");
					if(!equals(device)) {
						//if(device.getAddress().equals(address1) || device.getAddress().equals(address2) ) {
							if( address1blooean == true || address2blooean == true) {
								if(device.getAddress().equals(address1)) {
									mDeviceList.add(device);
									address1blooean = false;
									connectBle(device);
								}else if(device.getAddress().equals(address2)){
									mDeviceList.add(device);
									address2blooean = false;
									connectBle(device);
								}
						}
					}
				}
			});
		}
	};

	private void connectBle(BluetoothDevice device) {
		while (true) {
			if (device == null)
				return;
			scanLeDevice(false);

			if (device.getName() == null || device.getAddress() == null) {
				mConnectionState = connectionStateEnum.isToScan;
				onConectionStateChange(mConnectionState);
			} else {
				System.out.println("onListItemClick " + device.getName().toString());
				System.out.println("Device Name:" + device.getName() + "   " + "Device Name:" + device.getAddress());
				mDeviceName = device.getName().toString();
				mDeviceAddress = device.getAddress().toString();
				if (mBluetoothLeService != null) {
					if (mBluetoothLeService.connect(device.getAddress())) {
						Log.d(TAG, "Connect request success");
						mConnectionState = connectionStateEnum.isConnecting;
						onConectionStateChange(mConnectionState);
						mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
					} else {
						Log.d(TAG, "Connect request fail");
						mConnectionState = connectionStateEnum.isToScan;
						onConectionStateChange(mConnectionState);
					}
					break;
				}
			}
		}
	}
    private void getGattServices(List<BluetoothGattService> gattServices) {
		//連線到裝置之後獲取裝置的服務(Service)和服務對應的Characteristic
        if (gattServices == null) return;
        String uuid = null;
        mSerialPortCharacteristic=null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            System.out.println("displayGattServices + uuid="+uuid);
            
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                 if(uuid.equals(SerialPortUUID)){
                	mSerialPortCharacteristic = gattCharacteristic;
                	System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
                }
            }
            mGattCharacteristics.add(charas);
        }
        
        if (mSerialPortCharacteristic==null ) {
			Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
            mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
		}
        else {
        	mSCharacteristic=mSerialPortCharacteristic;
        	mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
        	mBluetoothLeService.readCharacteristic(mSCharacteristic);
		}

    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
