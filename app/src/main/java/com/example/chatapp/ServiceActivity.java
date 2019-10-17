package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.Bean.MessageBean;
import com.example.chatapp.Bean.Type;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

public class ServiceActivity extends AppCompatActivity {

    private TextView textViewipAdress,msg1;
    private ImageView imageView;
    private static final String TAG = "ServiceActivity";
    private static final int IMAGEMSG=2;
    @SuppressLint("HandlerLeak")
//    public Handler handler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//
//            if (msg.what==1){
//                Bundle data=msg.getData();
//                Log.d(TAG, "handleMessage: "+data.getString("msg"));
//                msg1.setText(data.getString("msg"));
//            }
//            if (msg.what==IMAGEMSG){
//                Bundle data=msg.getData();
//                Bitmap bitmap=(Bitmap) data.getParcelable("image");
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Sockertservice.MBinder mBinder= (Sockertservice.MBinder) service;
            mBinder.getService().setCallback(new Sockertservice.CallBack() {
                @Override
                public void getServiceData(final MessageBean data) {
                    ServiceActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (data.getType().getMessageType()){
                                case Type
                                        .MESSAGE_TYPE_IMAGE:
                                    imageView.setImageBitmap(data.getBitmap());
                                break;
                                case Type.MESSAGE_TYPE_TEXT:
                                    msg1.append(data.getText());
                            }
                            msg1.append(data.getText());
                        }
                    });

                }

            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        init();
        Log.e(TAG, "run: accept"+Thread.currentThread());
        Intent intent=new Intent(this,Sockertservice.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
//
//        try {
//        ServerSocket mserSocket=new ServerSocket(9898);
//            Thread thread=new Thread(new SocketAceeptThread(mserSocket));
//            thread.start();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        textViewipAdress.setText(getlocallip());
    }

    private void init(){
        textViewipAdress=findViewById(R.id.ipAdress);
        msg1=findViewById(R.id.msg);
        imageView=findViewById(R.id.image_view);
    }
    private String getlocallip(){
        try{
            Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()){
                NetworkInterface networkInterface=en.nextElement();
                Enumeration<InetAddress> inetAddresses =networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()){
                    InetAddress inetAddress=inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address){
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    //    class SocketAceeptThread implements Runnable{
//
//
//        private ServerSocket serverSocket;
//        public SocketAceeptThread(ServerSocket socket){
//            this.serverSocket=socket;
//        }
//        @Override
//        public void run() {
//             Socket socket=null;
//            try {
//
//                socket=serverSocket.accept();
//                //stratSocket(socket);
//                getImageSocket(socket);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    private void stratSocket(final Socket socket){
//        BufferedReader reader = null;
//        OutputStream writer =null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            StringBuffer stringBuffer=new StringBuffer();
//            String s=null;
//                while (true){
//                    s=reader.readLine();
//                    if (s==null)
//                        break;
//                    stringBuffer.append(s);
//                }
//
//                Message message= new Message();
//                message.what=1;
//                Bundle bundle =new Bundle();
//                bundle.putString("msg",stringBuffer.toString());
//                message.setData(bundle);
//                handler.sendMessage(message);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                reader.close();
//              //  writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

//    public void getImageSocket(final Socket socket){
//        DataInputStream inputStream=null;
//        try {
//             inputStream=new DataInputStream(socket.getInputStream());
//             long size=inputStream.readLong();
//             byte[] data=new byte[(int)size];
//
//            inputStream.readFully(data);
//            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
//             Bitmap bitmap=BitmapFactory.decodeByteArray(data,0,data.length);
//             bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
//            Message message=new Message();
//            Bundle bundle=new Bundle();
//            bundle.putParcelable("image",bitmap);
//            message.what=IMAGEMSG;
//            message.setData(bundle);
//            handler.sendMessage(message);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
