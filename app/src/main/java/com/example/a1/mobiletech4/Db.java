package com.example.a1.mobiletech4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class Db {
    DbHelper dbHelper;
    Context context;
    Cursor cursor;
    SQLiteDatabase database;

    String key;
    String[] resourcesList;

    public Db(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void setKey(String key_) {
        key = new String(key_);
    }

    public int getItemCount() {
        cursor = database.query(dbHelper.TABLE_NAME, null, null, null, null, null, null);
        int itemCount = cursor.getCount();
        cursor.close();

        return itemCount;
    }

    public String getItemInfoByResourceName(String resourceName, String columnName) {

        cursor = database.query(dbHelper.TABLE_NAME, null,
                dbHelper.KEY_RESOURCE + " = ?",
                new String[] {encrypt(resourceName)},
                null, null, null);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(columnName);
        return decrypt(cursor.getString(columnIndex));
    }

    public long createItem(String resourceName, String login, String password, String note) {

        if(resourceName.length() == 0)
            return 0;

        ContentValues cv = new ContentValues();
        cv.put(dbHelper.KEY_RESOURCE, encrypt(resourceName));
        cv.put(dbHelper.KEY_LOGIN, encrypt(login));
        cv.put(dbHelper.KEY_PASSWORD, encrypt(password));
        cv.put(dbHelper.KEY_NOTE, encrypt(note));

        return database.insert(dbHelper.TABLE_NAME, null, cv);
    }

    public void deleteItem(String resourceName) {
        database.delete(
                dbHelper.TABLE_NAME,
                dbHelper.KEY_RESOURCE + " = ?",
                new String[] {encrypt(resourceName)});
    }

    public void updateResource(String resourceName, String newResource){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.KEY_RESOURCE, encrypt(newResource));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    public void updateLogin(String resourceName, String newLogin){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.KEY_LOGIN, encrypt(newLogin));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    public void updatePassword(String resourceName, String newPassword){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.KEY_PASSWORD, encrypt(newPassword));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    public void updateNote(String resourceName, String newNote){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.KEY_NOTE, encrypt(newNote));
        String[] args = new String[]{encrypt(resourceName)};
        database.update(DbHelper.TABLE_NAME, cv, "resource = ?", args);
    }

    public String[] getAllResources() {
        resourcesList = new String[this.getItemCount()];
        cursor = database.query(dbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int nameColInd = cursor.getColumnIndex(dbHelper.KEY_RESOURCE);

            int i = 0;
            do {
                String resourceName = decrypt(cursor.getString(nameColInd));
                resourcesList[i] = resourceName;
                i++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        return resourcesList;
    }

    public void close() {
        database.close();
        dbHelper.close();
    }

    String encrypt(String openedText) {

        CryptoProvider cp = new CryptoProvider();
        String cipherText = "";

        try {
            cipherText = cp.encryptMessage(openedText, key);
        }
        catch (Exception e) {
            Toast.makeText(this.context, "encryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return cipherText;
    }

    String decrypt(String cipherText) {

        CryptoProvider cp = new CryptoProvider();
        String openedText = "";

        try {
            openedText = cp.decryptMessage(cipherText, key);
        }
        catch (Exception e) {
            Toast.makeText(this.context, "decryptMessage failed", Toast.LENGTH_SHORT).show();
        }

        return openedText;
    }
}
