package com.example.a1.mobiletech4;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ImportDataActivity extends AppCompatActivity {

    String checkedFile;
    String[] fileList;
    ListView listView;
    String key;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);

        // check access to SD
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // creating folder if it does not exist
        checkDatabaseFolder();

        passwordText = findViewById(R.id.createPassEnterPass3);

        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        CheckBox checkBox = findViewById(R.id.checkBox3);
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

        Button copePassButton = findViewById(R.id.copyPassBtn4);
        copePassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {


                //passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                //String srcText = passwordText.getText().toString();
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



        // key = PreferencesSettings.getCurrentCode(this).substring(0, 32);
        //String qwe = PreferencesSettings.getCurrentCode(this).substring(0, 32);
        checkedFile = new String();

        // getting file list from AppDatabases folder
        fileList = getFileListFromFolder();

        listView = findViewById(R.id.fileListForImport);
        listView.setChoiceMode(listView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedFile = fileList[listView.getCheckedItemPosition()];
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, fileList);
        listView.setAdapter(adapter);

        Button importButton = findViewById(R.id.importFromFileButton);
        importButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (checkedFile.length() == 0) {
                    Toast.makeText(ImportDataActivity.this, "No resources selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                //key = PreferencesSettings.md5(passwordText.getText().toString());
                importDatabaseFromFile(checkedFile);
                startWorkActivity();
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton3);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startWorkActivity();
            }
        });
    }

    void importDatabaseFromFile(String filename) {
        File databaseFile = new File(Environment.getExternalStorageDirectory() + "/AppDatabases/", filename);
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(databaseFile));
        }
        catch (IOException e) {
            Toast.makeText(this, "FileReader() failed", Toast.LENGTH_SHORT).show();
            return;
        }

        Db database = new Db(this);
        key = PreferencesSettings.getCurrentCode(this).substring(0, 32);
        database.setKey(key);

        String[] resources = database.getAllResources();
        int N = database.getItemCount();
        // deletion of all items from database
        for(int i = 0; i < N; i++) {
            database.deleteItem(resources[i]);
        }

        String line;

        try {
            while((line = br.readLine()) != null) {

                String[] parameters = line.split(" ");
                if(parameters.length < 4) {
                    continue;
                }

                key = PreferencesSettings.md5(passwordText.getText().toString());
                database.setKey(key);

                String resource = decrypt(parameters[0]);
                String login = decrypt(parameters[1]);
                String password = decrypt(parameters[2]);
                String note = decrypt(parameters[3]);

                key = PreferencesSettings.getCurrentCode(this).substring(0, 32);
                database.setKey(key);
                database.createItem(resource, login, password, note);
            }
        }
        catch(IOException e) {
            Toast.makeText(this, "readLine() failed", Toast.LENGTH_SHORT).show();
            return;
        }

        database.close();

        try {
            br.close();
        }
        catch(IOException e) {
            Toast.makeText(this, "br.close() failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    String[] getFileListFromFolder() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "AppDatabases");
        File[] filesArray = yourDir.listFiles();
        String[] list = new String[filesArray.length];

        for (int i = 0; i < filesArray.length; i++) {
            list[i] = filesArray[i].getName();
        }

        return list;
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

    String decrypt(String cipherText) {

        CryptoProvider cp = new CryptoProvider();
        String openedText = "";

        try {
            openedText = cp.decryptMessage(cipherText, key);
        }
        catch (Exception e) {
            Toast.makeText(this, "decryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return openedText;
    }

}
