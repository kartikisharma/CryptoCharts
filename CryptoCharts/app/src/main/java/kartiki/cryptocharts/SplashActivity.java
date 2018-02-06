package kartiki.cryptocharts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Kartiki on 2018-02-05.
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.date)
    TextView date;

    private boolean mIsShowNextScreen;
    private boolean mIsPaused =  false;
    private Handler mHandler;
    private final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        date.setText(format.format(calendar.getTime()));

        mIsShowNextScreen = false;
        mIsPaused = false;
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
                mHandler.removeCallbacksAndMessages(null);
                if (!mIsPaused) {
                    startNextActivity();
                } else {
                    mIsShowNextScreen = true;
                }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsShowNextScreen) {
            mIsPaused = false;
            startNextActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

        private synchronized void startNextActivity() {
            if (!mIsPaused) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
}
