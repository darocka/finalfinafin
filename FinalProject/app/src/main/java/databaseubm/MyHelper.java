package databaseubm;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by helmine on 2015-02-02.
 */
public class MyHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE "+
                    Constants.TABLE_USER + " (" +
                    Constants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.NAME + " TEXT, " +
                    Constants.PWD + " TEXT, " +
                    Constants.EMAIL + " TEXT, " +
                    Constants.PHONE + " TEXT);" ;

    private static final String CREATE_TABLE_LOG =
            "CREATE TABLE "+
                    Constants.TABLE_LOG+ " (" +
                    Constants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.NAME + " TEXT, " +
                    Constants.ACTION + " TEXT, " +
                    Constants.DATE + " TEXT, " +
                    Constants.TIMESTART + " TEXT, " +
                    Constants.TIMEEND + " TEXT, " +
                    Constants.TIMEELAPSED + " TEXT);" ;


    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS " + Constants.TABLE_USER;
    private static final String DROP_TABLE_LOG = "DROP TABLE IF EXISTS " + Constants.TABLE_LOG;

    // String Insert_Data="INSERT INTO "+Constants.TABLE_USER+" VALUES('guest','12345',500,1,'04/06/2011')";


    public MyHelper(Context context){
        super (context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_LOG);
            Toast.makeText(context, "onCreate() called", Toast.LENGTH_LONG).show();
            // db.execSQL(Insert_Data);

        } catch (SQLException e) {
            Toast.makeText(context, "exception onCreate() db-", Toast.LENGTH_LONG).show();
            // Log.e("DEXC", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE_USER);
            db.execSQL(DROP_TABLE_LOG);
            onCreate(db);
            Toast.makeText(context, "onUpgrade called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }
}