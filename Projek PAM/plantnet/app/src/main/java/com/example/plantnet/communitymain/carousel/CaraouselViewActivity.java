package com.example.plantnet.communitymain.carousel;


import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.plantnet.R;

public class CaraouselViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_caraousel_view);

        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(CaraouselViewActivity.this).load(getIntent().getStringExtra("image")).into(imageView);
    }
}
