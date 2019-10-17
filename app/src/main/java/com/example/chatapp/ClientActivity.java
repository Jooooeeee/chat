package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapp.Bean.MessageBean;
import com.example.chatapp.Bean.Type;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.listener.OnSelectedListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextmsg;
    private Button sengmsg;
    private String ip="192.168.1.228";
    private ImageButton imageButton;
    private static final String TAG = "ClientActivity";
    private List<MessageBean> messageBeanList;
    private static final int REQUEST_CODE_CHOOSE=23;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter=null;



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Sockertservice.MBinder mBinder= (Sockertservice.MBinder) service;
            mBinder.getService().setCallback(new Sockertservice.CallBack() {
                @Override
                public void getServiceData(final MessageBean data) {
                    ClientActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageBeanList.add(data);
                            messageAdapter.notifyItemInserted(messageBeanList.size()-1);
                            recyclerView.scrollToPosition(messageBeanList.size()-1);
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
        setContentView(R.layout.activity_client);
        init();
         recyclerView=findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageBeanList=new ArrayList<>();
         messageAdapter=new MessageAdapter(messageBeanList);
         recyclerView.setAdapter(messageAdapter);
        Intent intent=new Intent(this,Sockertservice.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private void init(){
        editTextmsg=findViewById(R.id.msg);
        sengmsg=findViewById(R.id.sentmsg);
        sengmsg.setOnClickListener(this);
        imageButton=findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sentmsg:
                 if (editTextmsg.getText().toString().isEmpty()){
                    Toast.makeText(ClientActivity.this,"发送内容不能为空",Toast.LENGTH_SHORT);
                }
                else {

                    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
                    cachedThreadPool.execute(new  SendSocker(editTextmsg.getText().toString()));
                    cachedThreadPool.shutdown();

                    MessageBean messageBean=new MessageBean();
                    messageBean.setText(editTextmsg.getText().toString());
                    Type type=new Type();
                    type.setWhereType(Type.WHERE_TYPE_OWN);
                    type.setMessageType(Type.MESSAGE_TYPE_TEXT);
                    messageBean.setType(type);

                    messageBeanList.add(messageBean);
                    messageAdapter.notifyItemInserted(messageBeanList.size()-1);
                    recyclerView.scrollToPosition(messageBeanList.size()-1);
                    editTextmsg.setText("");

                }
                break;
            case R.id.imageButton:
                request(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                default:
                    break;
        }
    }
    class SendSocker implements Runnable{

        private String msg;

        public SendSocker(String msg){
            this.msg=msg;
        }
        @Override
        public void run() {
            DataOutputStream  ou=null;
            Socket socket=null;
            try {
                 socket=new Socket();
                socket.connect(new InetSocketAddress(ip,9898),5000);
                ou=new DataOutputStream(socket.getOutputStream());
                sendTextMsg(ou,msg);
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                    ou.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public void request(String permission){
        if (ContextCompat.checkSelfPermission(ClientActivity.this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ClientActivity.this,new String[]{permission},1);
        }
        else {
            matisse();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    matisse();
                }
                else {
                    Toast.makeText(this,"YOU DENIED THE PERMISSION",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void matisse(){
        Set<MimeType> set=new HashSet<>();
        set.add(MimeType.JPEG);
        set.add(MimeType.PNG);
        Matisse.from(ClientActivity.this)
                .choose(set,false)
                .countable(true)
                .maxSelectable(9)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
                        cachedThreadPool.execute(new SendImageSocker(pathList));
                        cachedThreadPool.shutdown();

                        for (String s:pathList){
                            Bitmap bitmap=BitmapFactory.decodeFile(s);
                            MessageBean messageBean=new MessageBean();
                            messageBean.setBitmap(bitmap);
                            Type type=getType(Type.WHERE_TYPE_OWN,Type.MESSAGE_TYPE_IMAGE);
                            messageBean.setType(type);
                            messageBeanList.add(messageBean);
                            messageAdapter.notifyItemInserted(messageBeanList.size()-1);
                            recyclerView.scrollToPosition(messageBeanList.size()-1);
                        }

                    }
                })
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private Type getType(int where,int messageType){
        Type type=new Type();
        type.setMessageType(messageType);
        type.setWhereType(where);
        return type;
    }

    class SendImageSocker implements Runnable{

        private List<String> msg;

        public SendImageSocker(List<String> msg){
            this.msg=msg;
        }
        @Override
        public void run() {
            DataOutputStream  ou=null;
            Socket socket=null;
            try {
                socket=new Socket();
                socket.connect(new InetSocketAddress(ip,9898),5000);
                ou=new DataOutputStream(socket.getOutputStream());
                for (String s:msg){
                    sengImageMsg(ou,s);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                    ou.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void sendTextMsg(DataOutputStream out,String msg) throws IOException{
        out.writeInt(Type.MESSAGE_TYPE_TEXT);
        out.write(msg.getBytes());
        out.flush();
    }

    public void sengImageMsg(DataOutputStream out,String pathName) throws IOException{
        BitmapFactory.Options options=new BitmapFactory.Options();
        Bitmap bitmap= BitmapFactory.decodeFile(pathName,options);
        String type=options.outMimeType;
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        if (type.equals("image/png")){
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        }
        else if (type.equals("image/jpeg")){
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        }
        else {
            Toast.makeText(ClientActivity.this,"未能识别图片格式",Toast.LENGTH_SHORT).show();
        }
        out.writeInt(Type.MESSAGE_TYPE_IMAGE);
        out.writeLong(byteArrayOutputStream.size());
        out.write(byteArrayOutputStream.toByteArray());
        out.flush();
    }
}
