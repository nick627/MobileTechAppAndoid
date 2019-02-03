package com.example.a1.mobiletech4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;

public class MainActivity extends AppCompatActivity {

    private String passwordStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordStatus = new String("changed");

        String code = PreferencesSettings.getCurrentCode(this);

        // if pass exist
        if(code.length() > 0)
        {
            startEnterPassActivity();
        }
        else
        {
            passwordStatus = "not_changed";
            startCreatePassActivity();
        }
        //showLockScreenFragment();
    }

    private void startCreatePassActivity() {
        Intent intent = new Intent(this, CreatePassActivity.class);
        finish();
        startActivity(intent);
    }

    private void startEnterPassActivity() {
        Intent intent = new Intent(this, EnterPassActivity.class);
        finish();
        intent.putExtra("password", passwordStatus);
        startActivity(intent);
    }

}
