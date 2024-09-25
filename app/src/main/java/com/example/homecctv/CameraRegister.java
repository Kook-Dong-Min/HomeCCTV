package com.example.homecctv;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CameraRegister extends AppCompatActivity {

    EditText editname, editURL, editIP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camreg);

        editname=findViewById(R.id.editTextText9);
        editURL=findViewById(R.id.editTextText10);
        editIP=findViewById(R.id.editTextText11);
    }

    @SuppressLint("Range")
    public void register(View target) {
        String name = editname.getText().toString();
        String url= editURL.getText().toString();
        String ip=editIP.getText().toString();

        if (name.equals("") || url.equals("") || ip.equals("")) {
            Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Cursor cursor=Camera.db.rawQuery("SELECT name,url,ip FROM nameurlip WHERE name=? OR url=? OR ip=?", new String[]{name,url,ip});

            if (cursor != null && cursor.moveToFirst()) {
                if (name.equals(cursor.getString(cursor.getColumnIndex("name")))) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show();
                }

                if (url.equals(cursor.getString(cursor.getColumnIndex("url")))) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 주소입니다.", Toast.LENGTH_SHORT).show();
                }

                if (ip.equals(cursor.getString(cursor.getColumnIndex("url")))) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 IP입니다.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Camera.db.execSQL("INSERT INTO nameurlip (_id,name,url,ip) VALUES(null, '" + name + "', '" + url + "', '" + ip + "');");
                Toast.makeText(getApplicationContext(), "카메라 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            cursor.close();
        }
    }

    public void cancel(View target)
    {
        finish();
    }
}
