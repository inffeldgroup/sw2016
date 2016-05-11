package at.tugraz.inffeldgroup.dailypic.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.net.Uri;
import android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Implementation of the database
 * Provides purpose-built functions to access the database.
 * Implements object relational mapping.
 */
public class DbDatasource
{
	private static final String TAG = DbDatasource.class.getSimpleName();
	private final String[]	allColumnsFavorites				= {SqlLiteHelper.COLUMN_URI};

	/** Instance of the database */
	private SQLiteDatabase			database;

	/**
	 * Context to get an instance of the database
	 */
	private Context					context;

	/** Singleton instance */
	private static volatile DbDatasource instance = null;

	/**
	 * Private Constructor
	 * @param context
	 */
	private DbDatasource(Context context)
	{
		if (context != null)
		{
			this.context = context;
			try
			{
				SqlLiteHelper.getInstance(context);
			}
			catch (Exception e)
			{
				Log.e(TAG, "Failed to open database.");
			}
		}
	}

	public static DbDatasource getInstance(Context context)
	{
		if (instance == null)
		{
			synchronized (SqlLiteHelper.class)
			{
				if (instance == null)
				{
					instance = new DbDatasource(context);
				}
			}
		}
		instance.openDatabase();
		return instance;
	}

	private void openDatabase()
	{
		try
		{
			database = SqlLiteHelper.getInstance(context).getWritableDatabase();
		}
		catch (Exception e)
		{
			Log.e(TAG, "Failed to open database.");
		}
	}

	public void insert(UriWrapper uriWrapper)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();
			values.put(SqlLiteHelper.COLUMN_URI, uriWrapper.getUri().toString());

			try
			{
				database.insert(SqlLiteHelper.TABLE_FAVORITES, null, values);
			}
			catch (Exception e)
			{
				Log.e(TAG, "Failed to insert uriWrapper into database.");
			}
		}
	}

	public int delete(UriWrapper uriWrapper)
	{
		try
		{
			return database.delete(SqlLiteHelper.TABLE_FAVORITES,
					SqlLiteHelper.COLUMN_URI + " = '" + uriWrapper.getUri() + "'", null);
		}
		catch (Exception e)
		{
			Log.e(TAG, "Failed to delete uriWrapper.");
		}
		return 0;
	}

	public ArrayList<UriWrapper> getAllFavorites()
	{
		ArrayList<UriWrapper> favorites = new ArrayList<UriWrapper>();

		try
		{
			Cursor cursor = database.query(SqlLiteHelper.TABLE_FAVORITES,
					allColumnsFavorites, null, null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				favorites.add(cursorToUriWrapper(cursor));
				cursor.moveToNext();
			}
			cursor.close();
			return favorites;
		}
		catch (Exception e)
		{
			Log.e(TAG, "Failed to fetch favorites from db.");
		}
		return favorites;
	}

	private UriWrapper cursorToUriWrapper(Cursor cursor)
	{
		UriWrapper uriWrapper = null;
		if (cursor != null)
		{
			try
			{
				uriWrapper = new UriWrapper(Uri.parse(cursor.getString(0)), true);
			}
			catch (Exception e)
			{
				Log.e(TAG, "Failed to convert cursor to UriWrapper.");
			}
		}
		return uriWrapper;
	}
}