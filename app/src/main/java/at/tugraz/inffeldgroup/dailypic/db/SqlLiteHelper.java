package at.tugraz.inffeldgroup.dailypic.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.MatrixCursor;

import java.util.ArrayList;

/**
 * Helper class for SQL operations implemented as Singleton design pattern.
 * Defines the database scheme.
 * Implements SQLiteOpenHelper to handle the database.
 */
public class SqlLiteHelper extends SQLiteOpenHelper
{
	private static final String TAG = SqlLiteHelper.class.getSimpleName();

	/** Instance of the database */
	private static volatile SqlLiteHelper instance = null;


	private static final int DATABASE_VERSION = 4;
	public static final String	DATABASE_NAME = "MainDatabase.db";

	public static final String	TABLE_IMAGES  = "Images";
	public static final String	COLUMN_URI	= "URI";
	public static final String	COLUMN_IS_FAVORITE = "isFavorite";

	public static final String	TABLE_CREATE_IMAGES	= "CREATE TABLE "
			+ TABLE_IMAGES
			+ "("
			+ COLUMN_URI
			+ " TEXT PRIMARY KEY, "
			+ COLUMN_IS_FAVORITE
			+ " INTEGER NOT NULL"
			+ ");";


	/**
	 * Creates or returns the the SQL database
	 * 
	 * @param context
	 *           Context of the calling application
	 * @return The instance of the database
	 */
	public static SqlLiteHelper getInstance(Context context)
	{
		if (instance == null)
		{
			synchronized (SqlLiteHelper.class)
			{
				if (instance == null)
				{
					instance = new SqlLiteHelper(context);
				}
			}
		}
		return instance;
	}

	/**
	 * Private Constructor
	 * @param context
	 *           Context of calling application
	 */
	private SqlLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Gets called to create the table structure.
	 * 
	 * @param database
	 *           The SQL database
	 * 
	 */
	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try {
			database.execSQL(TABLE_CREATE_IMAGES);
		} finally {}
	}
	
	/**
	 * Enables foreign key constraints
	 * @param database The SQL database
	 */
	@Override
	public void onOpen(SQLiteDatabase database) {
	    super.onOpen(database);
	    if (!database.isReadOnly()) {
	        // Enable foreign key constraints
	        database.setForeignKeyConstraintsEnabled(true);
	    }
	}

	/**
	 * Gets called to update the table structure if changes were made.
	 * 
	 * @param db
	 *           A SQL database
	 * @param oldVersion
	 *           Old version of the database
	 * @param newVersion
	 *           New version of the database
	 * 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		} finally {} /*
		catch (SQLException e) {
			Log.e(TAG, "Upgrade to new database version failed.");
		} */
		onCreate(db);
	}

	public ArrayList<Cursor> getData(String Query){
		//get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		//an array list of cursor to save two cursors one has results from the query
		//other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2= new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);


		try{
			String maxQuery = Query ;
			//execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);


			//add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });

			alc.set(1,Cursor2);
			if (null != c && c.getCount() > 0) {


				alc.set(0,c);
				c.moveToFirst();

				return alc ;
			}
			return alc;
		} catch(SQLException sqlEx){
			Log.d("printing exception", sqlEx.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
			alc.set(1,Cursor2);
		} catch(Exception ex){

			Log.d("printing exception", ex.getMessage());

			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+ex.getMessage() });
			alc.set(1,Cursor2);
		}
        return alc;


	}
}