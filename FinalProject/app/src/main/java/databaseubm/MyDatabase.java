package databaseubm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by helmine on 2015-02-04.
 */
public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase (Context c){
        context = c;
        helper = new MyHelper(context);
    }

    public long insertUserToUserTable (String username, String pass, String email, String phone)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Deletes old user with same name exist =>delete.
        int x = db.delete(Constants.TABLE_USER, Constants.NAME + " = ?", new String[]{username});

        // Add new user
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, username);
        contentValues.put(Constants.PWD, pass);
        contentValues.put(Constants.EMAIL, email);
        contentValues.put(Constants.PHONE, phone);
        long id = db.insert(Constants.TABLE_USER, null, contentValues);
        return id;
    }

    public long insertLogIntoLOGTable (String username, String action, String dateCurrent, String startTime, String endTime, String elapsedTime)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Insert Log
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, username);
        contentValues.put(Constants.ACTION, action);
        contentValues.put(Constants.DATE, dateCurrent);
        contentValues.put(Constants.TIMESTART, startTime);
        contentValues.put(Constants.TIMEEND, endTime);
        contentValues.put(Constants.TIMEELAPSED, elapsedTime);
        long id = db.insert(Constants.TABLE_LOG, null, contentValues);
        return id;
    }


    public String getStringDataLOG(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.TIMESTART,Constants.DATE,Constants.TIMEEND};
        Cursor cursor = db.query(Constants.TABLE_LOG, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constants.NAME);
            int index2 = cursor.getColumnIndex(Constants.TIMESTART);
            int index3 = cursor.getColumnIndex(Constants.DATE);
            int index4 = cursor.getColumnIndex(Constants.TIMEEND);

            String name = cursor.getString(index1);
            String pwd = cursor.getString(index2);
            String phone = cursor.getString(index3);
            String email = cursor.getString(index4);

            buffer.append(name + " " + pwd + " " + phone + " " + email+ "\n");
        }
        return buffer.toString();
    }


    public Cursor getLogData(String uname)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.NAME, Constants.ACTION,Constants.DATE,Constants.TIMEEND,Constants.TIMEELAPSED};


        String selection = Constants.NAME + "='" +uname+ "'";
        Cursor cursor = db.query(Constants.TABLE_LOG, columns, selection, null, null, null, null);
        return cursor;
    }


    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.NAME, Constants.PWD};
        Cursor cursor = db.query(Constants.TABLE_USER, columns, null, null, null, null, null);
        return cursor;
    }






    // Returns User Data as Array
    // If data does not exist, return an empty array.
    public String[] getUserDataStringData(String uname){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.PWD,Constants.EMAIL,Constants.PHONE};
        String selection = Constants.NAME + "='" +uname+ "'";
        Cursor cursor = db.query(Constants.TABLE_USER, columns, selection, null, null, null, null);

        if(cursor.moveToNext()){
            int index1 = cursor.getColumnIndex(Constants.NAME);
            int index2 = cursor.getColumnIndex(Constants.PWD);
            int index3 = cursor.getColumnIndex(Constants.EMAIL);
            int index4 = cursor.getColumnIndex(Constants.PHONE);
            String name = cursor.getString(index1);
            String pwd = cursor.getString(index2);
            String phone = cursor.getString(index3);
            String email = cursor.getString(index4);
            String[] s = {name,pwd,phone,email};
            return s;
        }
        else{
            String[] s = {};
            return s;
        }
    }

    public String getStringData(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.PWD,Constants.EMAIL,Constants.PHONE};
        Cursor cursor = db.query(Constants.TABLE_USER, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constants.NAME);
            int index2 = cursor.getColumnIndex(Constants.PWD);
            int index3 = cursor.getColumnIndex(Constants.EMAIL);
            int index4 = cursor.getColumnIndex(Constants.PHONE);

            String name = cursor.getString(index1);
            String pwd = cursor.getString(index2);
            String phone = cursor.getString(index3);
            String email = cursor.getString(index4);

            buffer.append(name + " " + pwd + " " + phone + " " + email+ "\n");
        }
        return buffer.toString();
    }


    public void deleteUserFromUserTable(String username){
        SQLiteDatabase db = helper.getWritableDatabase();
        int x = db.delete(Constants.TABLE_USER, Constants.NAME+" = ?", new String[]{username});
    }

    public boolean DoesuserNameMatchPassword(String uname, String upass){
        boolean match = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.NAME, Constants.PWD};

        String selection = Constants.NAME + "='" +uname+ "'";
        Cursor cursor = db.query(Constants.TABLE_USER, columns, selection, null, null, null, null);

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(Constants.NAME);
            int index2 = cursor.getColumnIndex(Constants.PWD);
            String name = cursor.getString(index1);
            String pwd = cursor.getString(index2);

            if(name.equals(uname) && pwd.equals(upass)){
                match = true;
                break;
            }
        }
        return match;
    }


    public String getSelectedData(String type)
    {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.NAME, Constants.PWD};

        String selection = Constants.PWD + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_USER, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constants.NAME);
            int index2 = cursor.getColumnIndex(Constants.PWD);
            String plantName = cursor.getString(index1);
            String plantType = cursor.getString(index2);
            buffer.append(plantName + " " + plantType + "\n");
        }
        return buffer.toString();
    }

}

