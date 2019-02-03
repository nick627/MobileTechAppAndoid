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

public class ChangingEntryActivity extends AppCompatActivity {
    EditText resourceText;
    EditText loginText;
    EditText passwordText;
    EditText noteText;

    String checkedResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changing_entry);

        Bundle arguments = getIntent().getExtras();
        checkedResource = arguments.get("checked_resource").toString();

        resourceText = findViewById(R.id.resourceText);
        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        noteText = findViewById(R.id.noteText);

        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        resourceText.setText(checkedResource);

        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));

        loginText.setText(database.getItemInfoByResourceName(checkedResource, DbHelper.KEY_LOGIN));
        passwordText.setText(database.getItemInfoByResourceName(checkedResource, DbHelper.KEY_PASSWORD));
        noteText.setText(database.getItemInfoByResourceName(checkedResource, DbHelper.KEY_NOTE));
        database.close();

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

        Button cancelButton = findViewById(R.id.cancelButton2);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startWorkActivity();
            }
        });




        Button copePassButton = findViewById(R.id.copyPassBtn);
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
                String resource = ((EditText)findViewById(R.id.resourceText)).getText().toString();
                String login = ((EditText)findViewById(R.id.loginText)).getText().toString();
                String password = ((EditText)findViewById(R.id.passwordText)).getText().toString();
                String note = ((EditText)findViewById(R.id.noteText)).getText().toString();

                updateFields(resource, login, password, note);
                startWorkActivity();
            }
        });
    }

    void updateFields(String resource, String login, String password, String note)
    {
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));

        database.updateLogin(checkedResource, login);
        database.updatePassword(checkedResource, password);
        database.updateNote(checkedResource, note);
        database.updateResource(checkedResource, resource);

        database.close();
    }

    void startWorkActivity()
    {
        Intent intent = new Intent(this, WorkActivity.class);
        finish();
        intent.putExtra("password", "not_changed");
        startActivity(intent);
    }
}
