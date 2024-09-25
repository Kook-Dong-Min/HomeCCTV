package com.example.homecctv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    String name, id, pw;
    TextView textView;
    Button button1, button2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_set);

        Intent intent=getIntent();
        if (intent!=null)
        {
            name=intent.getStringExtra("name");
            id=intent.getStringExtra("id");
            pw=intent.getStringExtra("pw");
        }

        textView=findViewById(R.id.textView6);
        textView.setText(name+"님의 설정.");
        button1=findViewById(R.id.button6);
        button2=findViewById(R.id.button8);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Settings.this, Personal.class);
                intent.putExtra("name",name);
                intent.putExtra("id",id);
                intent.putExtra("pw",pw);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
