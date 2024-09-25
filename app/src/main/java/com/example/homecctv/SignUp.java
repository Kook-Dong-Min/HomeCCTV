package com.example.homecctv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    EditText name,id,pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sign);

        name=findViewById(R.id.editTextText2);
        id=findViewById(R.id.editTextText3);
        pw=findViewById(R.id.editTextText4);
    }

    @SuppressLint("Range")
    public void signup(View target)
    {
        String nametext=name.getText().toString();
        String idtext=id.getText().toString();
        String pwtext=pw.getText().toString();

        if (nametext.equals("") || idtext.equals("") || pwtext.equals(""))
        {
            Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Cursor cursor = LogIn.db.rawQuery("SELECT name, id FROM nameidpw WHERE name=? OR id=?", new String[]{nametext, idtext});

            if (cursor != null && cursor.moveToFirst()) {
                if (nametext.equals(cursor.getString(cursor.getColumnIndex("name")))) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show();
                }

                if (idtext.equals(cursor.getString(cursor.getColumnIndex("id")))) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                LogIn.db.execSQL("INSERT INTO nameidpw (_id,name,id,pw) VALUES(null, '" + nametext + "', '" + idtext + "', '" + pwtext + "');");
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            cursor.close();
        }
    }

    public void cancel(View target, Context context)
    {
        finish();
    }
}
