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

public class EnterPassActivity extends AppCompatActivity {


    EditText passwordText;
    String passwordStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pass);

        Bundle arguments = getIntent().getExtras();
        passwordStatus = new String(arguments.get("password").toString());

        passwordText = findViewById(R.id.createPassEnterPass2);
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());


        CheckBox checkBox = findViewById(R.id.createPassTick2);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        Button acceptButton = findViewById(R.id.createPassBtn2);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPass() == 1) {
                    startAnotherActivity();
                }
            }
        });

    }

    private void startAnotherActivity() {
        Intent intent = new Intent(this, WorkActivity.class);
        finish();
        intent.putExtra("password", passwordStatus);
        startActivity(intent);
    }

    int checkPass() {
        String enteredPass = PreferencesSettings.md5(passwordText.getText().toString());
        String defaultPass = PreferencesSettings.getCurrentCode(this);

        if (enteredPass.equals(defaultPass))
        {
            return 1;
        }
        return 0;
        }
}
