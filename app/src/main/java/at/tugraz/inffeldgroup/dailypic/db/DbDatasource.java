package at.tugraz.inffeldgroup.dailypic.db;

import java.util.ArrayList;

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
	private final String[]	allColumnsFavorites				= {SqlLiteHelper.COLUMN_URI, SqlLiteHelper.COLUMN_IS_FAVORITE};

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
			try {
				SqlLiteHelper.getInstance(context);
			}
			catch (Exception e) {
				Log.e(TAG, "Failed to initialize database.");
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
					// Use application context to avoid memory-leaking activity context!
					instance = new DbDatasource(context.getApplicationContext());
				}
			}
		}
		instance.openDatabase();
		return instance;
	}

	private void openDatabase()
	{
		try {
			database = SqlLiteHelper.getInstance(context).getWritableDatabase();
		}
		catch (Exception e) {
			Log.e(TAG, "Failed to open database.");
		}
	}

	public UriWrapper getUriWrapper(Uri uri) {
		UriWrapper ret = null;
		try {
			Cursor cursor = database.rawQuery(
					"Select * from " + SqlLiteHelper.TABLE_IMAGES + " where " + SqlLiteHelper.COLUMN_URI
							+ " = '" + uri.toString() + "'", null);

			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				ret = cursorToUriWrapper(cursor);
			}
			else {
				ret = new UriWrapper(uri, false);
			}
			cursor.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ret;

	}

	public void insert(UriWrapper uriWrapper)
	{
		ContentValues values = new ContentValues();
		values.put(SqlLiteHelper.COLUMN_URI, uriWrapper.getUri().toString());
		values.put(SqlLiteHelper.COLUMN_IS_FAVORITE, uriWrapper.isFav());

		try {
			database.insert(SqlLiteHelper.TABLE_IMAGES, null, values);
		}
		catch (Exception e) {
			Log.e(TAG, "Failed to insert uriWrapper into database.");
		}
	}

	public int delete(UriWrapper uriWrapper)
	{
		try {
			return database.delete(SqlLiteHelper.TABLE_IMAGES,
					SqlLiteHelper.COLUMN_URI + " = '" + uriWrapper.getUri() + "'", null);
		}
		catch (Exception e) {
			Log.e(TAG, "Failed to delete uriWrapper.");
		}
		return 0;
	}

	public ArrayList<UriWrapper> getAllFavorites()
	{
		ArrayList<UriWrapper> favorites = new ArrayList<UriWrapper>();

		try {
			Cursor cursor = database.query(SqlLiteHelper.TABLE_IMAGES,
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
		catch (Exception e) {
			Log.e(TAG, "Failed to fetch favorites from db.");
		}
		return favorites;
	}

	public void update(UriWrapper uriWrapper)
	{
		ContentValues values = new ContentValues();
		values.put(SqlLiteHelper.COLUMN_URI, uriWrapper.getUri().toString());
		values.put(SqlLiteHelper.COLUMN_IS_FAVORITE, uriWrapper.isFav());

		try {
			database.update(
							SqlLiteHelper.TABLE_IMAGES,
							values,
							SqlLiteHelper.COLUMN_URI + " = " + "'" + uriWrapper.getUri() + "'",
							null);
		}
		catch (Exception e) {
			Log.e(TAG, "Failed to update image.");
		}
	}

	private UriWrapper cursorToUriWrapper(Cursor cursor)
	{
		UriWrapper uriWrapper = null;
		if (cursor != null)
		{
			try {
				uriWrapper = new UriWrapper(Uri.parse(cursor.getString(0)), cursor.getInt(1) != 0);
			}
			catch (Exception e) {
				Log.e(TAG, "Failed to convert cursor to UriWrapper.");
				e.printStackTrace();
			}
		}
		return uriWrapper;
	}
}