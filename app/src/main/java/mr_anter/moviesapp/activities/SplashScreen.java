package mr_anter.moviesapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import mr_anter.moviesapp.R;

public class SplashScreen extends AppCompatActivity {
    private ImageView image0;
    private Animation fade0;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // for change font of textView :)
        textView = (TextView) findViewById(R.id.splash_text);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/daisy.ttf");
        textView.setTypeface(type);


        image0 = (ImageView) findViewById(R.id.splash_image);
        fade0 = AnimationUtils.loadAnimation(this, R.anim.fade_in_enter);

        image0.startAnimation(fade0);
        fade0.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
