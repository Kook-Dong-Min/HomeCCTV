package com.example.homecctv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Locale;

public class CameraConnect extends AppCompatActivity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

    String name,ip;
    static String url;
    CCTV cctv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camcon);

        name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");
        ip=getIntent().getStringExtra("ip");

        requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 0);
        cctv=findViewById(R.id.cctv);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "RECORD_AUDIO 퍼미션이 허용되었습니다.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "RECORD_AUDIO 퍼미션이 거부되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void voice(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성 인식 중");
        try {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "음성 인식이 불가능합니다", Toast.LENGTH_SHORT).show();
        }
    }

    //음성 인식 활동 결과 처리
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            TextView voiceMsg = findViewById(R.id.textView15);
            voiceMsg.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            String command= voiceMsg.getText().toString();
            sendVoiceMsg(command, ip);
        }
    }

    public void up(View view) {
        String command="Up";
        SendMsg(command, ip);
    }

    public void down(View view) {
        String command="Down";
        SendMsg(command, ip);
    }

    public void left(View view) {
        String command="Left";
        SendMsg(command, ip);
    }

    public void right(View view) {
        String command="Right";
        SendMsg(command, ip);
    }

    public void SendMsg(String command,String ip){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket ds=new DatagramSocket();
                    InetAddress ia=InetAddress.getByName(ip);

                    byte[] data=command.getBytes();
                    DatagramPacket dp=new DatagramPacket(data,data.length,ia,7777);
                    ds.send(dp);
                    ds.close();
                }catch (Exception e){
                    Log.d("UDPClient","Error: "+e.getMessage());
                }
            }
        }).start();
    }

    public void sendVoiceMsg(String msg,String ip){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket ds=new DatagramSocket();
                    InetAddress ia=InetAddress.getByName(ip);
                    DatagramPacket dp=new DatagramPacket(msg.getBytes(),msg.getBytes().length,ia,9999);
                    ds.send(dp);
                    ds.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

class CCTV extends SurfaceView implements SurfaceHolder.Callback,Runnable
{
    Thread threadSView;
    boolean threadRunning=true;

    public CCTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        threadSView=new Thread(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        threadSView.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        threadRunning=false;
        try {
            threadSView.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        final int maxImgSize=1000000;
        byte[] arr=new byte[maxImgSize];
        try {
            URL myUrl = new URL(CameraConnect.url);
            HttpURLConnection connection=(HttpURLConnection) myUrl.openConnection();
            InputStream in=connection.getInputStream();
            while (threadRunning)
            {
                int i=0;
                for (i=0;i<1000;i++)
                {
                    int b=in.read();
                    if(b==0xff)
                    {
                        int b1=in.read();
                        if (b1==0xd8) break;
                    }
                }

                if(i>999)
                {
                    Log.e("MyCCTV","Bad head");
                    continue;
                }

                arr[0]=(byte) 0xff;
                arr[1]=(byte) 0xd8;
                i=2;
                for(;i<maxImgSize;i++)
                {
                    int b=in.read();
                    arr[i]=(byte) b;
                    if(b==0xff)
                    {
                        i++;
                        int b1=in.read();
                        arr[i]=(byte) b1;
                        if (b1==0xd9) break;
                    }
                }
                i++;
                int nBytes=i;

                Bitmap bitmap= BitmapFactory.decodeByteArray(arr,0,nBytes);
                bitmap=Bitmap.createScaledBitmap(bitmap,getWidth(),getHeight(),false);
                SurfaceHolder holder=getHolder();
                Canvas canvas=null;
                canvas=holder.lockCanvas();
                if(canvas!=null)
                {
                    canvas.drawColor(Color.TRANSPARENT);
                    canvas.drawBitmap(bitmap,0,0,null);
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        } catch (Exception e)
        {
            Log.e("MyCCTV","Error"+e.toString());
        }
    }
}