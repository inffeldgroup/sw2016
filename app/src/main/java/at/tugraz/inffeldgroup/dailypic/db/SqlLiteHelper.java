package at.tugraz.inffeldgroup.dailypic.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for SQL operations implemented as Singleton design pattern.
 * Defines the database scheme.
 * Implements SQLiteOpenHelper to handle the database.
 */
public class SqlLiteHelper extends SQLiteOpenHelper
{

	/** Instance of the database */
	private static volatile SqlLiteHelper	instance										= null;

	private static final int					DATABASE_VERSION							= 1;
	public static final String					DATABASE_NAME								= "MainDatabase.db";

	public static final String					TABLE_FAVORITES									= "Favorites";
	public static final String					COLUMN_URI								= "URI";

	public static final String					TABLE_PATIENTS								= "Patients";
	public static final String					COLUMN_PATIENT_ID							= "pId";
	public static final String					COLUMN_PATIENT_FIRSTNAME				= "pFirstName";
	public static final String					COLUMN_PATIENT_LASTNAME					= "pLastName";
	public static final String					COLUMN_PATIENT_PATIENT_NUMBER			= "pPatientNumber";

	public static final String					TABLE_MEASUREMENTS						= "Measurements";
	public static final String					COLUMN_MEASUREMENT_ID					= "mId";
	public static final String					COLUMN_MEASUREMENT_PATIENT_ID			= "mPId";
	public static final String					COLUMN_MEASUREMENT_ASIG_ID				= "mAId";
	public static final String					COLUMN_MEASUREMENT_START_TIME			= "mStartTime";
	public static final String					COLUMN_MEASUREMENT_STOP_TIME			= "mStopTime";
	public static final String					COLUMN_MEASUREMENT_INTERVAL			= "mInterval";

	public static final String					TABLE_TEMPERATURES						= "Temperatures";
	public static final String					COLUMN_TEMPERATURE_ID					= "tId";
	public static final String					COLUMN_TEMPERATURE_MEASUREMENT_ID	= "tMId";
	public static final String					COLUMN_TEMPERATURE_TEMPERATURE		= "tTemp";
	public static final String					COLUMN_TEMPERATURE_TIME					= "tTime";

	public static final String					TABLE_CREATE_PATIENTS					= "CREATE TABLE "
																												+ TABLE_PATIENTS
																												+ "("
																												+ COLUMN_PATIENT_ID
																												+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																												+ COLUMN_PATIENT_FIRSTNAME
																												+ " TEXT NOT NULL, "
																												+ COLUMN_PATIENT_LASTNAME
																												+ " TEXT NOT NULL, "
																												+ COLUMN_PATIENT_PATIENT_NUMBER
																												+ " TEXT);";

	public static final String					TABLE_CREATE_ASIGS						= "CREATE TABLE "
																												+ TABLE_ASIGS
																												+ "("
																												+ COLUMN_ASIG_ID
																												+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																												+ COLUMN_ASIG_UID
																												+ " TEXT NOT NULL, "
																												+ COLUMN_ASIG_WAIT_COEFF
																												+ " INTEGER DEFAULT 2000000, "
																												+ COLUMN_ASIG_TEMPERATURE_COEFF_D
																												+ " DOUBLE DEFAULT 330, "
																												+ COLUMN_ASIG_TEMPERATURE_COEFF_K
																												+ " DOUBLE DEFAULT -0.3221, "
																												+ COLUMN_ASIG_TYPE
																												+ " INTEGER NOT NULL);";

	public static final String					TABLE_CREATE_MEASUREMENTS				= "CREATE TABLE "
																												+ TABLE_MEASUREMENTS
																												+ "("
																												+ COLUMN_MEASUREMENT_ID
																												+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																												+ COLUMN_MEASUREMENT_PATIENT_ID
																												+ " INTEGER NOT NULL REFERENCES "
																												+ TABLE_PATIENTS + "(" + COLUMN_PATIENT_ID + ")" + " ON DELETE CASCADE, "
																												+ COLUMN_MEASUREMENT_ASIG_ID
																												+ " INTEGER NOT NULL REFERENCES "
																												+ TABLE_ASIGS + "(" + COLUMN_ASIG_ID + ")" + " ON DELETE CASCADE, "
																												+ COLUMN_MEASUREMENT_START_TIME
																												+ " INTEGER NOT NULL, "
																												+ COLUMN_MEASUREMENT_STOP_TIME
																												+ " INTEGER NOT NULL, "
																												+ COLUMN_MEASUREMENT_INTERVAL
																												+ " INTEGER NOT NULL);";

	public static final String					TABLE_CREATE_TEMPERATURES				= "CREATE TABLE "
																												+ TABLE_TEMPERATURES
																												+ "("
																												+ COLUMN_TEMPERATURE_ID
																												+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																												+ COLUMN_TEMPERATURE_MEASUREMENT_ID
																												+ " INTEGER NOT NULL REFERENCES "
																												+ TABLE_MEASUREMENTS + "(" + COLUMN_MEASUREMENT_ID + ")" + " ON DELETE CASCADE, "
																												+ COLUMN_TEMPERATURE_TEMPERATURE
																												+ " DOUBLE NOT NULL, "
																												+ COLUMN_TEMPERATURE_TIME
																												+ " INTEGER NOT NULL);";

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
			database.execSQL(TABLE_CREATE_ASIGS);
			database.execSQL(TABLE_CREATE_PATIENTS);
			database.execSQL(TABLE_CREATE_MEASUREMENTS);
			database.execSQL(TABLE_CREATE_TEMPERATURES);
		}
		catch (SQLException e)
		{
			dbErrorHandler.addError(e);
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
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASIGS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPERATURES);
		}
		catch (SQLException e)
		{
			dbErrorHandler.addError(e);
		}
		onCreate(db);
	}

	/**
	 * Gets called to create the table structure.
	 * 
	 * @return the actual DBErrorHandler
	 * 
	 */
	public DBErrorHandler getDBErrorHandler()
	{
		return dbErrorHandler;
	}
}