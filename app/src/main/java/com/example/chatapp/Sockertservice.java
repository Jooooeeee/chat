package com.example.chatapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chatapp.Bean.MessageBean;
import com.example.chatapp.Bean.Type;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Sockertservice extends Service {

    private MBinder mBinder=new MBinder();
    public Sockertservice() {
    }

    public static interface CallBack{
        void getServiceData(MessageBean data);

    }

    private CallBack callback=null;

    public CallBack getCallBack() {
        return callback;
    }

    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

    class MBinder extends Binder {
        public Sockertservice getService(){
            return Sockertservice.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                socketAccept();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Sockertservice", "onStartCommand: 服务开启");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void socketAccept(){
        Socket socket=null;
        try {
            ServerSocket mserSocket=new ServerSocket(9898);
            while (true){
                socket=mserSocket.accept();
                handleSocket(socket);
            }
    } catch (IOException e) {
        e.printStackTrace();
    }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleSocket(final Socket socket){
        int head=0;
        DataInputStream dataOutputStream=null;
        try{
             dataOutputStream=new DataInputStream(socket.getInputStream());
             head=dataOutputStream.readInt();
             switch (head){
                case Type.MESSAGE_TYPE_IMAGE:
                    getImageSocket(dataOutputStream);
                    break;
                case Type.MESSAGE_TYPE_TEXT:
                    getTextSocket(dataOutputStream);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    private void getImageSocket(DataInputStream inputStream){
        try {

            long size=inputStream.readLong();
            byte[] data=new byte[(int)size];


            inputStream.readFully(data);
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            Type type=new Type();
            type.setMessageType(Type.MESSAGE_TYPE_IMAGE);
            type.setWhereType(Type.WHERE_TYPE_OTHERS);
            MessageBean messageBean=new MessageBean();
            messageBean.setType(type);
            messageBean.setBitmap(bitmap);
            if (callback!=null){
                callback.getServiceData(messageBean);
            }
//            Message message=new Message();
//            Bundle bundle=new Bundle();
//            bundle.putParcelable("image",bitmap);
//            message.what=IMAGEMSG;
//            message.setData(bundle);
//            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getTextSocket(DataInputStream inputStream){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();
            String s=null;
            while (true){
                s=reader.readLine();
                if (s==null)
                    break;
                stringBuffer.append(s);
            }
            Type type=new Type();
            type.setMessageType(Type.MESSAGE_TYPE_TEXT);
            type.setWhereType(Type.WHERE_TYPE_OTHERS);
            MessageBean messageBean=new MessageBean();
            messageBean.setType(type);
            messageBean.setText(stringBuffer.toString());
            if (callback!=null){
                callback.getServiceData(messageBean);
            }

//            Message message= new Message();
//            message.what=TEXT;
//            Bundle bundle =new Bundle();
//            bundle.putString("msg",stringBuffer.toString());
//            message.setData(bundle);
//            handler.sendMessage(message);

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
                //  writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
