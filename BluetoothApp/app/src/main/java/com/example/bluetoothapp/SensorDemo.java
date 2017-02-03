package com.example.bluetoothapp;

import android.Manifest;
import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.os.Handler;


import com.example.bluetoothapp.BluetoothSerialClient.BluetoothStreamingHandler;
import com.example.bluetoothapp.BluetoothSerialClient.OnBluetoothEnabledListener;
import com.example.bluetoothapp.BluetoothSerialClient.OnScanListener;


import android.bluetooth.BluetoothDevice;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Set;

import android.widget.ArrayAdapter;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorDemo extends Activity {
    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private TextView mTextView_temperature;
    private TextView mTextView_flame;
    private TextView mTextView_door;
    private TextView mTextView_gas;
    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private Menu mMenu;
    private BluetoothSerialClient mClient;

    private int value=0;
    private boolean isTimer=true;
    private boolean isLight = false;
    private boolean isEmergency = false;


    public final static int REPEAT_DELAY = 1000;


    private Button EmergencyBtn;
    private Activity mthis;

    private Camera mCamera = null;

    private TextView mTimer,mTemp;
    private ImageView FlashBtn;
    private ImageView CallBtn;
    private int h,m,s;
    private TextView mPPMText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mClient = BluetoothSerialClient.getInstance();

        if(mClient == null) {
            Toast.makeText(getApplicationContext(), "Cannot use the Bluetooth device.", Toast.LENGTH_SHORT).show();
            finish();
        }


        mthis = this;

       if(!mClient.check())
       {
           Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           startActivityForResult(enableBtIntent, 2);
       }
        initSensor();
        initProgressDialog();
        initDeviceListDialog();
        handler.sendEmptyMessage(0);

        mTimer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               isTimer = !isTimer;
            }
        });


        CallBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CallBtn.setImageResource(R.mipmap.calling);
                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(mthis,new String[]{Manifest.permission.CALL_PHONE},
                                1000);
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-5848-9788")));
                    CallBtn.setImageResource(R.mipmap.call);

                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        FlashBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isLight)
                {
                    FlashBtn.setImageResource(R.mipmap.flash);
                }else{
                    FlashBtn.setImageResource(R.mipmap.flashon);
                }

                if (Build.VERSION.SDK_INT >= 21) {


                    try {
                        if (!isLight) {
                            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            String cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
                            camManager.setTorchMode(cameraId, true);
                        } else {
                            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            String cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
                            camManager.setTorchMode(cameraId, false);
                        }

                        isLight = !isLight;
                    } catch (CameraAccessException e) {

                    }
                }else
                {
                    if(mCamera==null)
                        mCamera = Camera.open();
                    if (!isLight) {
                        flashOn();
                    } else {
                        flashOff();
                    }

                }

            }
        });

       /* EmergencyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmergency)
                eHandler.sendEmptyMessage(0);

                isEmergency = !isEmergency;
            }
        });*/
    }

    public void flashOn() {


        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
        mCamera.startPreview();

    }


    public void flashOff() {


        Camera.Parameters parameters2 = mCamera.getParameters();
        parameters2.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters2);
        mCamera.stopPreview();

    }


    Handler eHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            // 할일들을 여기에 등록
            if(isEmergency) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(500);
                this.sendEmptyMessageDelayed(0, REPEAT_DELAY);
            }
                   // REPEAT_DELAY 간격으로 계속해서 반복하게 만들어준다
        }
    };


   Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            // 할일들을 여기에 등록
            if(isTimer) {
                mTimer.setText(h+":"+m+":"+s);
                s++;
                if(s>=60)
                {
                    s =0;
                    m++;
                    if(m>=60)
                    {
                        m =0;
                        h++;
                    }
                }
            }
            this.sendEmptyMessageDelayed(0, REPEAT_DELAY);        // REPEAT_DELAY 간격으로 계속해서 반복하게 만들어준다
        }
    };

    @Override
    protected void onPause() {
        mClient.cancelScan(getApplicationContext());
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        enableBluetooth();
    }
    private void initProgressDialog() {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
    }

    private void initDeviceListDialog() {
        mDeviceArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_device);
        ListView listView = new ListView(getApplicationContext());
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select bluetooth device");
        builder.setView(listView);
        builder.setPositiveButton("Scan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanDevices();
                    }
                });
        mDeviceListDialog = builder.create();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
    }

    private void addDeviceToArrayAdapter(BluetoothDevice device) {
        if(mBluetoothDevices.contains(device)) {
            mBluetoothDevices.remove(device);
            mDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
        }
        mBluetoothDevices.add(device);
        mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress() );
        mDeviceArrayAdapter.notifyDataSetChanged();
    }

    private void enableBluetooth() {
        BluetoothSerialClient btSet =  mClient;
        btSet.enableBluetooth(this, new OnBluetoothEnabledListener() {
            @Override
            public void onBluetoothEnabled(boolean success) {
                if(success) {
                    getPairedDevices();
                } else {
                    finish();
                }
            }
        });
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  mClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
    }

    private void scanDevices() {
        BluetoothSerialClient btSet = mClient;
        btSet.scanDevices(getApplicationContext(), new OnScanListener() {
            String message ="";
            @Override
            public void onStart() {
                Log.d("Test", "Scan Start.");
                mLoadingDialog.show();
                message = "Scanning....";
                mLoadingDialog.setMessage("Scanning....");
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        BluetoothSerialClient btSet = mClient;
                        btSet.cancelScan(getApplicationContext());
                    }
                });
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) {
                addDeviceToArrayAdapter(bluetoothDevice);
                message += "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress();
                mLoadingDialog.setMessage(message);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Scan finish.");
                message = "";
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                mDeviceListDialog.show();
            }
        });
    }

    private void connect(BluetoothDevice device) {
        mLoadingDialog.setMessage("Connecting....");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        BluetoothSerialClient btSet =  mClient;
        btSet.connect(getApplicationContext(), device, mBTHandler);
    }
    private BluetoothStreamingHandler mBTHandler = new BluetoothStreamingHandler() {
        ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);

        @Override
        public void onError(Exception e) {
            mLoadingDialog.cancel();
            Toast.makeText(getApplicationContext(), "The the Bluetooth device Connection error - " +  e.toString(), Toast.LENGTH_SHORT).show();
            mMenu.getItem(0).setTitle(R.string.action_connect);
        }

        @Override
        public void onDisconnected() {
            mMenu.getItem(0).setTitle(R.string.action_connect);
            mLoadingDialog.cancel();
            Toast.makeText(getApplicationContext(), "The the Bluetooth device Disconnected.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onData(byte[] buffer, int length) {
            if(length == 0) return;
            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer,0,length);
            Log.i("test","size="+length);
            if(length>=6) {
                /* ?곗씠?곕? 諛쏆븘???吏묒뼱?ｋ뒗 遺遺?Buffer*/
            Log.i("test","int="+unpack32(buffer));
                ByteBuffer wrapped = ByteBuffer.wrap(buffer,0,4);
                ByteBuffer wrapped2 = ByteBuffer.wrap(buffer,4,1);
                ByteBuffer wrapped3 = ByteBuffer.wrap(buffer,5,1);
                int ppm = wrapped.getInt();
                int temp = (int)wrapped2.get();
                int humi = (int)wrapped3.get();
                mPPMText.setText(ppm+"");
                mTemp.setText(temp+"");
               // mTextView_gas.setText(test+"ppm");

               // mTextView_temperature.setText(temp+"C");
               // mTextView_door.setText(humi+"%");
                Log.i("test","int C0="+ppm+" temp="+temp);
                readSensorData(new String(mmByteBuffer.array(), 0, mmByteBuffer.position()));
                Log.i("test","help="+mmByteBuffer.position()+" length="+length);

            mmByteBuffer.clear();
        }

        }

        @Override
        public void onConnected() {
            Toast.makeText(getApplicationContext(), "Messgae : " + mClient.getConnectedDevice().getName() + "Connected. ", Toast.LENGTH_SHORT).show();
            mLoadingDialog.cancel();
            mMenu.getItem(0).setTitle(R.string.action_disconnect);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean connect = mClient.isConnection();
        if(item.getItemId() == R.id.action_connect) {
            if (!connect) {
                mDeviceListDialog.show();
            } else {
                mBTHandler.close();
            }
            return true;
        }else {
            return true;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if(mCamera!=null)
        mCamera.release();
        mClient.claer();
    };

    private void initSensor(){

        mTimer = (TextView) findViewById(R.id.Timer);
        h=0;
        m=0;
        s=0;
        mTimer.setText(h+":"+m+":"+s);

        mTemp = (TextView)findViewById(R.id.tempText);

        FlashBtn = (ImageView)findViewById(R.id.flashBtn);
        CallBtn = (ImageView)findViewById(R.id.callBtn);
        mPPMText = (TextView)findViewById(R.id.ppmtext);

   /*     mTextView_temperature = (TextView) findViewById(R.id.temperature_value);
        mTextView_flame = (TextView) findViewById(R.id.flame_value);
        mTextView_door = (TextView) findViewById(R.id.door_value);
        mTextView_gas = (TextView) findViewById(R.id.gas_value);*/

    }

    private void readSensorData(String text) {
        String buf = text;

        Log.i("test","SensorData="+text);

        // Door
       /* if(buf.contains("a")){
            buf = buf.replace("a","");
            mTextView_door.setText("1 grade");
        }else if(buf.contains("b")){
            buf = buf.replace("b","");
            mTextView_door.setText("2 grade");
        }else if(buf.contains("c")){
            buf = buf.replace("c","");
            mTextView_door.setText("3 grade");
        }
        else if(buf.contains("d")){
            buf = buf.replace("d","");
            mTextView_door.setText("4 grade");
        }*/
/*
        // MQ-7 Gas
        if(buf.contains("g")){
            buf = buf.replace("g","");
            mTextView_gas.setText("Detect");
        }else if(buf.contains("n")){
            buf = buf.replace("n","");
            mTextView_gas.setText("None");
        }

        // Flame
        if(buf.contains("f")){
            buf = buf.replace("f","");
            mTextView_flame.setText("Detect");
        }else if(buf.contains("s")){
            buf = buf.replace("s","");
            mTextView_flame.setText("None");
        }

        // Temperature
        mTextView_temperature.setText(buf);*/
    }

    public int byteToint(byte[] arr){ return (arr[0] & 0xff)<<24 | (arr[1] & 0xff)<<16 | (arr[2] & 0xff)<<8 | (arr[3] & 0xff); }


    int unpack32(byte[] src)
    {
        int val;

        int[] tmp = new int[4];
        tmp[0] = src[0];
        tmp[1] = src[1];
        tmp[2] = src[2];
        tmp[3] = src[3];

        for(int i=0;i<4;i++)
        {
            if(tmp[i]<0)
                tmp[i]+=256;
        }

        val  = src[0] << 24;
        val |= src[1] << 16;
        val |= src[2] <<  8;
        val |= src[3]      ;

        return val;
    }
}