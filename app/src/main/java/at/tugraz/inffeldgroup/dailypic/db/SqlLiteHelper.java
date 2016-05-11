package at.tugraz.inffeldgroup.dailypic.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class for SQL operations implemented as Singleton design pattern.
 * Defines the database scheme.
 * Implements SQLiteOpenHelper to handle the database.
 */
public class SqlLiteHelper extends SQLiteOpenHelper
{
	private static final String TAG = SqlLiteHelper.class.getSimpleName();

	/** Instance of the database */
	private static volatile SqlLiteHelper	instance										= null;


	private static final int					DATABASE_VERSION							= 1;
	public static final String					DATABASE_NAME								= "MainDatabase.db";

	public static final String					TABLE_FAVORITES									= "Favorites";
	public static final String					COLUMN_FAVORITE_ID					= "favID";
	public static final String					COLUMN_URI								= "URI";

	public static final String					TABLE_CREATE_FAVORITES					= "CREATE TABLE "
																												+ TABLE_FAVORITES
																												+ "("
																												+ COLUMN_FAVORITE_ID
																												+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																												+ COLUMN_URI
																												+ " TEXT NOT NULL);";


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
		try
		{
			database.execSQL(TABLE_CREATE_FAVORITES);
		}
		catch (SQLException e)
		{
			Log.e(TAG, "Creation of database failed.");
		}
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
		try
		{
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
		}
		catch (SQLException e)
		{
			Log.e(TAG, "Upgrade to new database version failed.");
		}
		onCreate(db);
	}
}