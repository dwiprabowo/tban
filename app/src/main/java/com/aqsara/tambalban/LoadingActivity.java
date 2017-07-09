package com.aqsara.tambalban;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class LoadingActivity extends Base{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ban", "LoadingActivity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animateWheel();
    }

    private void animateWheel(){
        RotateAnimation anim = new RotateAnimation(
                0f, 359f
                ,RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF, .5f
        );
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2400);

        final ImageView wheel = (ImageView) findViewById(R.id.imageView);
        wheel.startAnimation(anim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void signedInUser() {
        delay(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void notSignedInUser() {
        delay(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, GoogleLoginActivity.class));
                finish();
            }
        });
    }

}
