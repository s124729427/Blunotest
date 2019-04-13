/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfrobot.angelo.blunobasicdemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mBluetoothGatt;
    public String mBluetoothDeviceAddress;
    
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public int mConnectionState = STATE_DISCONNECTED;

    private ArrayList<BluetoothGatt> connectionQueue = new ArrayList<BluetoothGatt>();

    private static final int WRITE_NEW_CHARACTERISTIC = -1;

    private static final int MAX_CHARACTERISTIC_LENGTH = 17;

    private boolean mIsWritingCharacteristic=false;

    private class BluetoothGattCharacteristicHelper{
    	BluetoothGattCharacteristic mCharacteristic;
    	String mCharacteristicValue;
    	BluetoothGattCharacteristicHelper(BluetoothGattCharacteristic characteristic, String characteristicValue){
    		mCharacteristic=characteristic;
    		mCharacteristicValue=characteristicValue;
    	}
    }

    private RingBuffer<BluetoothGattCharacteristicHelper> mCharacteristicRingBuffer = new RingBuffer<BluetoothGattCharacteristicHelper>(8);
    
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        //寫入成功之後，開始讀取裝置返回來的資料。
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            System.out.println("BluetoothGattCallback----onConnectionStateChange"+newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                if(mBluetoothGatt.discoverServices())
                {
                    Log.i(TAG, "Attempting to start service discovery:");
                }
                else{
                    Log.i(TAG, "Attempting to start service discovery:not success");
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        //// New services discovered//发现新的服务
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	System.out.println("onServicesDiscovered "+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
        	//this block should be synchronized to prevent the function overloading
            //write操作会调用此方法
			synchronized(this)
			{
				//CharacteristicWrite success
	        	if(status == BluetoothGatt.GATT_SUCCESS)
	        	{
	        		System.out.println("onCharacteristicWrite success:"+ new String(characteristic.getValue()));
            		if(mCharacteristicRingBuffer.isEmpty())
            		{
    	        		mIsWritingCharacteristic = false;
            		}
            		else
	            	{
	            		BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
	            		if(bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
	            		{
	            	        try {
		            			bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));

	            	        } catch (UnsupportedEncodingException e) {
	            	            // this should never happen because "US-ASCII" is hard-coded.
	            	            throw new IllegalStateException(e);
	            	        }
	            			
	            			
	            	        if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
	            	        {
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
	            	        }else{
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
	            	        }
	            			bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
	            		}
	            		else
	            		{
	            	        try {
	            	        	bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
	            	        } catch (UnsupportedEncodingException e) {
	            	            // this should never happen because "US-ASCII" is hard-coded.
	            	            throw new IllegalStateException(e);
	            	        }
	            			
	            	        if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
	            	        {
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
	            	        }else{
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
	            	        }
	            			bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

//	            			System.out.print("before pop:");
//	            			System.out.println(mCharacteristicRingBuffer.size());
	            			mCharacteristicRingBuffer.pop();
//	            			System.out.print("after pop:");
//	            			System.out.println(mCharacteristicRingBuffer.size());
	            		}
	            	}
	        	}
	        	//WRITE a NEW CHARACTERISTIC
	        	else if(status == WRITE_NEW_CHARACTERISTIC)
	        	{
	        		if((!mCharacteristicRingBuffer.isEmpty()) && mIsWritingCharacteristic==false)
	            	{
	            		BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
	            		if(bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
	            		{
	            			
	            	        try {
		            			bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));
	            	        } catch (UnsupportedEncodingException e) {
	            	            // this should never happen because "US-ASCII" is hard-coded.
	            	            throw new IllegalStateException(e);
	            	        }
	            			
	            	        if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
	            	        {
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
	            	        }else{
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
	            	        }
	            			bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
	            		}
	            		else
	            		{
	            	        try {
		            			bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
	            	        } catch (UnsupportedEncodingException e) {
	            	            // this should never happen because "US-ASCII" is hard-coded.
	            	            throw new IllegalStateException(e);
	            	        }
	            			

	            	        if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
	            	        {
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
	            	        }else{
	            	        	System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
	            	        }
	            			bluetoothGattCharacteristicHelper.mCharacteristicValue = "";
		            			mCharacteristicRingBuffer.pop();
	            		}
	            	}
	        		
    	        	mIsWritingCharacteristic = true;
    	        	if(mCharacteristicRingBuffer.isFull())
    	        	{
    	        		mCharacteristicRingBuffer.clear();
        	        	mIsWritingCharacteristic = false;
    	        	}
	        	}
	        	else{
	        		mCharacteristicRingBuffer.clear();
	        		System.out.println("onCharacteristicWrite fail:"+ new String(characteristic.getValue()));
	        		System.out.println(status);
	        	}
			}
        }
        
        @Override
        // // 接收数据
        //getValue 可以读取到蓝牙设备的数据
        //從特徵中讀取資料
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	System.out.println("onCharacteristicRead  "+characteristic.getUuid().toString());
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        @Override
        // //notify 会回调用此方法
        public void  onDescriptorWrite(BluetoothGatt gatt, 
        								BluetoothGattDescriptor characteristic,
        								int status){
        	System.out.println("onDescriptorWrite  "+characteristic.getUuid().toString()+" "+status);
        }


         @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	System.out.println("onCharacteristicChanged  "+new String(characteristic.getValue()));
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

    };
    
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //顯示ACTIVITY
        sendBroadcast(intent);
    }
    //第五步

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                intent.putExtra(EXTRA_DATA, new String(data));
        		sendBroadcast(intent);
            }
//        }
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        //第一步 先拿到BluetoothManager
    	System.out.println("BluetoothLeService initialize"+mBluetoothManager);
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        //第二步 再拿到BluetoothAdapt
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean connect(final String address) {
        //拿到ADDRESS做事
    	System.out.println("BluetoothLeService connect"+address+mBluetoothGatt);
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
//BluetoothDevice  描述了一個藍芽裝置 提供了getAddress()裝置Mac地址,getName()裝置的名稱   //開始連線裝置
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        System.out.println("device.connectGatt connect");
		synchronized(this)
		{
			mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
            if(checkGatt(mBluetoothGatt)){
                connectionQueue.add(mBluetoothGatt);
            }
		}
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        //存入ADDRESS
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    private boolean checkGatt(BluetoothGatt bluetoothGatt) {
        if (!connectionQueue.isEmpty()) {
            for(BluetoothGatt btg:connectionQueue){
                if(btg.equals(bluetoothGatt)){
                    return false;
                }
            }
        }
        return true;
    }

    public void disconnect() {
        //斷開連線
    	System.out.println("BluetoothLeService disconnect");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
    	System.out.println("BluetoothLeService close");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        //通知系统去读取特定的数据
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        //獲取到特徵之後，找到服務中可以向下位機寫指令的特徵，向該特徵寫入指令
        //寫入成功之後，開始onConnectionStateChange讀取裝置返回來的資料。
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        String writeCharacteristicString;
        try {
        	writeCharacteristicString = new String(characteristic.getValue(),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // this should never happen because "US-ASCII" is hard-coded.
            throw new IllegalStateException(e);
        }
        System.out.println("allwriteCharacteristicString:"+writeCharacteristicString);

    	mCharacteristicRingBuffer.push(new BluetoothGattCharacteristicHelper(characteristic,writeCharacteristicString) );
    	System.out.println("mCharacteristicRingBufferlength:"+mCharacteristicRingBuffer.size());

    	mGattCallback.onCharacteristicWrite(mBluetoothGatt, characteristic, WRITE_NEW_CHARACTERISTIC);

    }    

    //获得属性后需要进行判断设备是否支持notify操作，然后再设备打开notify通知
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    
    
}
