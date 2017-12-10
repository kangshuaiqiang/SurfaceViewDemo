package demo.ksq.com.surfaceviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Luck luck;
    private ImageView kStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luck = findViewById(R.id.lucl);
        kStart = findViewById(R.id.start_btn);
        kStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luck.isStarte()) {
                    luck.luckStart(1);
                    kStart.setImageResource(R.mipmap.stop);
                } else {
                    if (!luck.isShouldEnd()) {
                        luck.luckEnd();
                        kStart.setImageResource(R.mipmap.start);
                    }
                }
            }
        });

    }
}
