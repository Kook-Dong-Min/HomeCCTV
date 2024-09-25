package com.example.homecctv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Personal extends AppCompatActivity {
    EditText Name, ID, PW;
    String name, id, pw,orgname, orgid, orgpw;
    Button button1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_per);

        Name=findViewById(R.id.editTextText5);
        ID=findViewById(R.id.editTextText6);
        PW=findViewById(R.id.editTextText7);
        button1=findViewById(R.id.button7);

        Intent intent=getIntent();
        if (intent!=null)
        {
            orgname=intent.getStringExtra("name");
            orgid=intent.getStringExtra("id");
            orgpw=intent.getStringExtra("pw");
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("Range")
    public void change(View target)
    {
        name=Name.getText().toString();
        id=ID.getText().toString();
        pw=PW.getText().toString();

        if (name.equals("") || id.equals("") || pw.equals(""))
        {
            Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            Cursor cursor = LogIn.db.rawQuery("SELECT name, id FROM nameidpw WHERE name=? OR id=?", new String[]{name, id});

            if (cursor != null && cursor.moveToFirst()) {
                if (name.equals(cursor.getString(cursor.getColumnIndex("name"))) && !name.equals(orgname)) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show();
                }

                if (id.equals(cursor.getString(cursor.getColumnIndex("id"))) && !id.equals(orgid)) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                LogIn.db.execSQL("INSERT INTO nameidpw (_id,name,id,pw) VALUES(null, '" + name + "', '" + id + "', '" + pw + "');");
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                LogIn.db.execSQL("DELETE FROM nameidpw WHERE name=? AND id=? AND pw=?", new String[]{orgname, orgid, orgpw});
                finish();
            }
            cursor.close();
        }
    }
}
