package com.example.a1.mobiletech4;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class CreatingEntryActivity extends AppCompatActivity {

    Db database;

    EditText resourceText;
    EditText loginText;
    EditText passwordText;
    EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_entry);

        database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));

        resourceText = findViewById(R.id.resourceText);
        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        noteText = findViewById(R.id.noteText);

        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        CheckBox checkBox = findViewById(R.id.checkBox);
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

        Button copePassButton = findViewById(R.id.copyPassBtn2);
        copePassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                //passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                String srcText = passwordText.getText().toString();
                //passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
                final ClipboardManager clipboardManager = (ClipboardManager)clipboardService;
                // Create a new ClipData.
                ClipData clipData = ClipData.newPlainText("Source Text", srcText);
                // Set it as primary clip data to copy text to system clipboard.
                clipboardManager.setPrimaryClip(clipData);
                // Popup a snackbar.
                Snackbar snackbar = Snackbar.make(v, "Source text has been copied to system clipboard.", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                long result = database.createItem(
                        resourceText.getText().toString(),
                        loginText.getText().toString(),
                        passwordText.getText().toString(),
                        noteText.getText().toString()
                );
                if(result < 0) {
                    Toast.makeText(CreatingEntryActivity.this, "[-] createItem returned -1", Toast.LENGTH_SHORT).show();
                }

                database.close();
                startWorkActivity();
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                database.close();
                startWorkActivity();
            }
        });
    }

    void startWorkActivity()
    {
        Intent intent = new Intent(this, WorkActivity.class);
        finish();
        intent.putExtra("password", "not_changed");
        startActivity(intent);
    }
}


