package com.example.homecctv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

class DBHelper2 extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="nameurlip.db";
    private static final int DATABASE_VERSION=1;

    public DBHelper2(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE nameurlip ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, url TEXT, ip TEXT);");
        Log.d("DB","DB 생성");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS nameurlip");
        onCreate(db);
    }
}

public class Camera extends AppCompatActivity {

    private DBHelper2 helper;
    EditText nametext;
    TextView textView;
    String name;
    static SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cam);
        nametext=findViewById(R.id.editTextText8);
        textView=findViewById(R.id.textView13);

        helper=new DBHelper2(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex)
        {
            db = helper.getReadableDatabase();
        }
    }

    public void register(View target)
    {
        Intent intent=new Intent(Camera.this, CameraRegister.class);
        startActivity(intent);
    }

    public void connect(View target)
    {
        name=nametext.getText().toString();
        if (name.isEmpty()) {
            nametext.setError("이름을 입력해주세요.");
            return;
        }
        else {
            try {
                Cursor cursor = db.rawQuery("SELECT name,url,ip FROM nameurlip WHERE name=?", new String[]{name});
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                    @SuppressLint("Range") String ip = cursor.getString(cursor.getColumnIndex("ip"));

                    Intent intent = new Intent(Camera.this, CameraConnect.class);
                    intent.putExtra("name", name);
                    intent.putExtra("url", url);
                    intent.putExtra("ip", ip);
                    startActivity(intent);
                } else {
                    nametext.setText("");
                    nametext.setError("존재하지 않는 이름입니다.");
                }
                cursor.close();
            } catch (Exception e) {
                Log.d("Exception", "" + e);
            }
        }
    }

    public void delete(View target)
    {
        name=nametext.getText().toString();
        if (name.isEmpty()) {
            nametext.setError("이름을 입력해주세요.");
            return;
        }
        else
        {
            Cursor cursor=db.rawQuery("SELECT name FROM nameurlip WHERE name=?",new String[]{name});
            if (cursor.moveToFirst())
                cursor.close();
            else
            {
                nametext.setText("");
                nametext.setError("존재하지 않는 이름입니다.");
                return;
            }
            db.delete("nameurlip","name=?",new String[]{name});
            nametext.setText("");
            nametext.setError("삭제되었습니다.");
        }
    }

    public void cancel(View target)
    {
        finish();
    }

    @SuppressLint("Range")
    public void search(View target)
    {
        Cursor cursor=db.rawQuery("SELECT name FROM nameurlip",null);
        String str="";
        while (cursor.moveToNext())
        {
            str=str+cursor.getString(cursor.getColumnIndex("name"))+"\r\n";
        }
        textView.setText(str);
        cursor.close();
    }
}
