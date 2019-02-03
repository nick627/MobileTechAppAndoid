package com.example.a1.mobiletech4;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportDataActivity extends AppCompatActivity {

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        // check access to SD
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // creating folder if it does not exist
        checkDatabaseFolder();

        key = PreferencesSettings.getCurrentCode(this).substring(0, 32);

        Button exportButton = findViewById(R.id.exportToFileButton);
        exportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String filename = ((EditText)findViewById(R.id.filenameToExport)).getText().toString();
                exportDatabaseToFile(filename);
                startWorkActivity();
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton4);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startWorkActivity();
            }
        });
    }

    void exportDatabaseToFile(String filename) {
        File databaseFile = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/", filename);
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(databaseFile));
        }
        catch(IOException e) {
            Toast.makeText(this, "FileWriter() failed", Toast.LENGTH_SHORT).show();
            return;
        }

        Db database = new Db(this);
        database.setKey(key);

        String[] resources = database.getAllResources();

        for(int i = 0; i < database.getItemCount(); i++) {
            writeEntryToFile(resources[i], bw, database);
        }

        database.close();

        try {
            bw.close();
        }
        catch(IOException e) {
            Toast.makeText(this, "bw.close() failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    void writeEntryToFile(String resource, BufferedWriter bw, Db database) {

        String login = database.getItemInfoByResourceName(resource, DbHelper.KEY_LOGIN);
        String password = database.getItemInfoByResourceName(resource, DbHelper.KEY_PASSWORD);
        String note = database.getItemInfoByResourceName(resource, DbHelper.KEY_NOTE);

        try {
            bw.write(encrypt(resource) + " " +
                    encrypt(login) + " " +
                    encrypt(password) + " " +
                    encrypt(note) + '\n');
        }
        catch(IOException e) {
            Toast.makeText(this, "bw.write() failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    void startWorkActivity() {
        Intent intent = new Intent(this, WorkActivity.class);
        finish();
        intent.putExtra("password", "not_changed");
        startActivity(intent);
    }

    void checkDatabaseFolder() {
        File databaseFolder = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/");
        if(!databaseFolder.exists())
            databaseFolder.mkdirs();
    }

    String encrypt(String openedText) {

        CryptoProvider cp = new CryptoProvider();
        String cipherText = "";

        try {
            cipherText = cp.encryptMessage(openedText, key);
        }
        catch (Exception e) {
            Toast.makeText(this, "encryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return cipherText.substring(0, cipherText.length() - 1);
    }
}
