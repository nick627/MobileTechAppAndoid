package com.example.a1.mobiletech4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class CreatePassActivity extends AppCompatActivity {

    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass);

        passwordText = findViewById(R.id.createPassEnterPass);
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());


        CheckBox checkBox = findViewById(R.id.createPassTick);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        Button acceptButton = findViewById(R.id.createPassBtn);
        acceptButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                savePass(passwordText.getText().toString());
                startMainActivity();
            }
        });


    }

    void savePass(String pass)
    {
        PreferencesSettings.saveToPref(this, PreferencesSettings.md5(pass));
    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
