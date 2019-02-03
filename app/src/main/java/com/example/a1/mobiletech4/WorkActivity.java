package com.example.a1.mobiletech4;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;

public class WorkActivity extends AppCompatActivity {

    String resources[];
    String checkedResource;
    ListView listView;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        checkedResource = new String();

        listView = findViewById(R.id.resourceList);
        listView.setChoiceMode(listView.CHOICE_MODE_SINGLE);

        // getting string from MainActivity
        Bundle arguments = getIntent().getExtras();
        String passwordStatus = new String(arguments.get("password").toString());

        String currentCode = new String(PreferencesSettings.getCurrentCode(this));
        String previousCode = new String(PreferencesSettings.getPreviousCode(this));

        String oldCode = PreferencesSettings.getOldCode(this);
        if(oldCode.length() > 0) {
            PreferencesSettings.deleteOldCode(this);
            recryptAllEntries(oldCode);
        }

        refreshResourcesList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedResource = resources[listView.getCheckedItemPosition()];
            }
        });

        passwordText = findViewById(R.id.createPassEnterPass4);
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        CheckBox checkBox = findViewById(R.id.checkBox2);
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

        Button copePassButton = findViewById(R.id.copyPassBtn3);
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


        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startCreatingEntryActivity();
            }
        });

        Button changeButton = findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkedResource.length() == 0) {
                    Toast.makeText(WorkActivity.this, "No resources selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                startChangingEntryActivity();
            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkedResource.length() == 0) {
                    Toast.makeText(WorkActivity.this, "No resources selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                deleteDatabaseElement(checkedResource);
                refreshResourcesList();
            }
        });



        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String oldPass = PreferencesSettings.md5(passwordText.getText().toString());
                if(checkOldPassToChangeIt(oldPass))
                {
                    deleteCode();
                    saveOldPassword(oldPass);
                    startPasswordChangingActivity();
                }
            }
        });


        Button exportButton = findViewById(R.id.exportDataButton);
        exportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startExportDataActivity();
            }
        });

        Button importButton = findViewById(R.id.importDataButton);
        importButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startImportDataActivity();
            }
        });
    }

    void recryptAllEntries(String previousCode) {

        String currentCode = PreferencesSettings.getCurrentCode(this).substring(0, 32);
        //String previousCode = PreferencesSettings.getPreviousCode(this).substring(0, 32);

        Db database = new Db(this);
        database.setKey(previousCode);
        int nResources = database.getItemCount();
        String[] resources = database.getAllResources();

        String[] logins = new String[nResources];
        String[] passwords = new String[nResources] ;
        String[] notes = new String[nResources];

        for (int i = 0; i < nResources; i++) {
            // getting info about the current resource
            database.setKey(previousCode);
            logins[i] = database.getItemInfoByResourceName(resources[i], DbHelper.KEY_LOGIN);
            passwords[i] = database.getItemInfoByResourceName(resources[i], DbHelper.KEY_PASSWORD);
            notes[i] = database.getItemInfoByResourceName(resources[i], DbHelper.KEY_NOTE);

            database.deleteItem(resources[i]);

            database.setKey(currentCode);
            database.createItem(resources[i], logins[i], passwords[i], notes[i]);
        }

        database.close();
    }

    void deleteDatabaseElement(String resourceName) {
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));
        database.deleteItem(resourceName);
        database.close();
    }

    void refreshResourcesList() {
        Db database = new Db(this);
        database.setKey(PreferencesSettings.getCurrentCode(this).substring(0, 32));
        resources = database.getAllResources();
        database.close();
        if(resources.length == 0) {
            Toast.makeText(WorkActivity.this, "Resource list is empty", Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter <String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, resources);
        listView.setAdapter(adapter);
    }

    void startCreatingEntryActivity()
    {
        Intent intent = new Intent(this, CreatingEntryActivity.class);
        finish();
        startActivity(intent);
    }

    void startChangingEntryActivity()
    {
        Intent intent = new Intent(this, ChangingEntryActivity.class);
        finish();
        intent.putExtra("checked_resource", checkedResource);
        startActivity(intent);
    }

    void startImportDataActivity()
    {
        Intent intent = new Intent(this, ImportDataActivity.class);
        finish();
        startActivity(intent);
    }

    void startExportDataActivity()
    {
        Intent intent = new Intent(this, ExportDataActivity.class);
        finish();
        startActivity(intent);
    }

    void startPasswordChangingActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    void deleteCode()
    {
        PreferencesSettings.deleteCode(this);
    }

    boolean checkOldPassToChangeIt(String oldPass)
    {
        if(oldPass.equals(PreferencesSettings.getCurrentCode(this).toString()))
        {
            return true;
        }
        return false;
    }

    void saveOldPassword(String oldPass)
    {
        PreferencesSettings.saveOldCode(this, oldPass);
    }
}
