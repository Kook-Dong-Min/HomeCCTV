package com.example.homecctv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {
    ImageView imageView1, imageView2, imageView3, imageView4;
    String name, id, pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mai);

        imageView1=findViewById(R.id.imageView2);
        imageView2=findViewById(R.id.imageView3);
        imageView3=findViewById(R.id.imageView4);
        imageView4=findViewById(R.id.imageView5);

        Intent intent=getIntent();
        if (intent!=null)
        {
            name=intent.getStringExtra("name");
            id=intent.getStringExtra("id");
            pw=intent.getStringExtra("pw");
        }

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Main.this, Camera.class);
                startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Main.this, Light.class);
                startActivity(intent);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Main.this, Temperature.class);
                startActivity(intent);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Main.this, Settings.class);
                intent.putExtra("name",name);
                intent.putExtra("id",id);
                intent.putExtra("pw",pw);
                startActivity(intent);
            }
        });
    }
}
