package com.example.homecctv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="nameidpw.db";
    private static final int DATABASE_VERSION=1;

    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE nameidpw ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, id TEXT, pw TEXT);");
        Log.d("DB","DB 생성");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS nameidpw");
        onCreate(db);
    }
}

public class LogIn extends AppCompatActivity {
    private DBHelper helper;
    private EditText ID, PW;
    CheckBox autologin;
    private String sharedPrefFile="login";
    private SharedPreferences sharedPreferences;
    static SQLiteDatabase db;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ID = findViewById(R.id.editTextText);
        PW = findViewById(R.id.editTextTextPassword);
        autologin = findViewById(R.id.checkBox);

        helper=new DBHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException ex)
        {
            db = helper.getReadableDatabase();
        }

        sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("autologin",false))
        {
            autologin.setChecked(true);
            loadidpw();
        }
    }

    @SuppressLint("Range")
    public void login(View target) {
        String id = ID.getText().toString();
        String pw = PW.getText().toString();
        if (id.equals("") || pw.equals("")) {
            Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            try {
                Cursor cursor = db.rawQuery("SELECT name,id,pw FROM nameidpw WHERE id=? AND pw=?", new String[]{id, pw});

                if (cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    Toast.makeText(getApplicationContext(), name + "님 환영합니다.", Toast.LENGTH_SHORT).show();

                    if (autologin.isChecked()) {
                        saveidpw();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id", "");
                        editor.putString("pw", "");
                        editor.putBoolean("autologin", false);
                        editor.apply();
                    }

                    Intent intent = new Intent(this, Main.class);
                    intent.putExtra("name", name);
                    intent.putExtra("id", id);
                    intent.putExtra("pw", pw);
                    startActivity(intent);

                } else {
                    ID.setText("");
                    PW.setText("");
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } catch (Exception e) {
                Log.d("Exception", "" + e);
            }
        }
    }

    public void signup(View target)
    {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    private void saveidpw()
    {
        String id=ID.getText().toString();
        String pw=PW.getText().toString();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("id",id);
        editor.putString("pw",pw);
        editor.putBoolean("autologin",true);
        editor.apply();
    }

    private void loadidpw()
    {
        String id=sharedPreferences.getString("id","");
        String pw=sharedPreferences.getString("pw","");
        ID.setText(id);
        PW.setText(pw);
    }
}