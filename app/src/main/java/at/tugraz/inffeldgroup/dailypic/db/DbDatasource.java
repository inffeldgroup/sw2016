package com.infineon.dcgr.cre.skinpatch.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Implementation of the database
 * Provides purpose-built functions to access the database.
 * Implements object relational mapping.
 * 
 * @author Rablc
 * 
 */
public class DbDatasource
{
	/** The actual database connection */
	private SQLiteDatabase			database;
	/**
	 * Context to get an instance of the database
	 */
	private Context					context;
	private final String	nullPointerExc				= "Cursor cannot be null";
	private final String[]	allColumnsAsig				=
																				{
			SqlLiteHelper.COLUMN_ASIG_ID, SqlLiteHelper.COLUMN_ASIG_UID,
			SqlLiteHelper.COLUMN_ASIG_WAIT_COEFF,
			SqlLiteHelper.COLUMN_ASIG_TEMPERATURE_COEFF_D,
			SqlLiteHelper.COLUMN_ASIG_TEMPERATURE_COEFF_K, SqlLiteHelper.COLUMN_ASIG_TYPE };

	private final String[]	allColumnsPatient			=
																				{
			SqlLiteHelper.COLUMN_PATIENT_ID, SqlLiteHelper.COLUMN_PATIENT_FIRSTNAME,
			SqlLiteHelper.COLUMN_PATIENT_LASTNAME,
			SqlLiteHelper.COLUMN_PATIENT_PATIENT_NUMBER			};

	private final String[]	allColumnsMeasurement	=
																				{
			SqlLiteHelper.COLUMN_MEASUREMENT_ID,
			SqlLiteHelper.COLUMN_MEASUREMENT_PATIENT_ID,
			SqlLiteHelper.COLUMN_MEASUREMENT_ASIG_ID,
			SqlLiteHelper.COLUMN_MEASUREMENT_START_TIME,
			SqlLiteHelper.COLUMN_MEASUREMENT_STOP_TIME,
			SqlLiteHelper.COLUMN_MEASUREMENT_INTERVAL			};

	private final String[]	allColumnsTemperature	=
																				{
			SqlLiteHelper.COLUMN_TEMPERATURE_ID,
			SqlLiteHelper.COLUMN_TEMPERATURE_MEASUREMENT_ID,
			SqlLiteHelper.COLUMN_TEMPERATURE_TEMPERATURE,
			SqlLiteHelper.COLUMN_TEMPERATURE_TIME					};

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public DbDatasource(Context context)
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
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
	}

	/**
	 * Opens database connection if not already opened.
	 */
	public void open()
	{
		try
		{
			database = SqlLiteHelper.getInstance(context).getWritableDatabase();
		}
		catch (Exception e)
		{
			SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
		}
	}

	/**
	 * Creates a new ASIG data set in database.
	 *
	 * @param asig
	 * 			the asig which should be created
	 */
	public Asig create(Asig asig)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();

			values.put(SqlLiteHelper.COLUMN_ASIG_UID, asig.getUId());
			values.put(SqlLiteHelper.COLUMN_ASIG_WAIT_COEFF, asig.getWaitCoeff());
			values.put(SqlLiteHelper.COLUMN_ASIG_TEMPERATURE_COEFF_D, asig.getTempCoeffD());
			values.put(SqlLiteHelper.COLUMN_ASIG_TEMPERATURE_COEFF_K, asig.getTempCoeffK());
			values.put(SqlLiteHelper.COLUMN_ASIG_TYPE, asig.getTypeIntVal());

			try
			{
				long id = database.insert(SqlLiteHelper.TABLE_ASIGS, null, values);
				Cursor cursor = database.query(SqlLiteHelper.TABLE_ASIGS, allColumnsAsig,
						SqlLiteHelper.COLUMN_ASIG_ID + " = " + String.valueOf(id), null, null,
						null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return new Asig("0x00", 0, 0, 0, Asig.AsigType.A11);
				}

				cursor.moveToFirst();
				Asig ic = cursorToAsig(cursor);
				cursor.close();
				return ic;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return new Asig("0x00", 0, 0, 0, Asig.AsigType.A11);
	}

	/**
	 * Creates a new Patient data set in database.
	 * 
	 * @param patient
	 * 			the patient which should be created
	 */
	public Patient create(Patient patient)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();

			values.put(SqlLiteHelper.COLUMN_PATIENT_FIRSTNAME, patient.getFirstName());
			values.put(SqlLiteHelper.COLUMN_PATIENT_LASTNAME, patient.getLastName());
			values.put(SqlLiteHelper.COLUMN_PATIENT_PATIENT_NUMBER,
					patient.getPatientNumber());

			try
			{
				long id = database.insert(SqlLiteHelper.TABLE_PATIENTS, null, values);
				Cursor cursor = database.query(SqlLiteHelper.TABLE_PATIENTS,
						allColumnsPatient,
						SqlLiteHelper.COLUMN_PATIENT_ID + " = " + String.valueOf(id), null,
						null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
				}

				cursor.moveToFirst();
				Patient patReturn = cursorToPatient(cursor);
				cursor.close();
				return patReturn;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return null;
	}

	/**
	 * Creates a new Measurement data set in database.
	 * 
	 * @param measurement
	 * 			the measurement which should be created
	 */
	public Measurement create(Measurement measurement)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();

			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_PATIENT_ID, measurement.getPId());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_ASIG_ID, measurement.getAId());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_START_TIME, measurement
					.getStarttime().getTimeInMillis());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_STOP_TIME, measurement.getStoptime()
					.getTimeInMillis());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_INTERVAL, measurement.getInterval());

			try
			{
				long id = database.insert(SqlLiteHelper.TABLE_MEASUREMENTS, null, values);
				Cursor cursor = database.query(SqlLiteHelper.TABLE_MEASUREMENTS,
						allColumnsMeasurement, SqlLiteHelper.COLUMN_MEASUREMENT_ID + " = "
								+ String.valueOf(id), null, null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return new Measurement(-1, -1, Calendar.getInstance(),
							Calendar.getInstance(), 0);
				}

				cursor.moveToFirst();
				Measurement patReturn = cursorToMeasurement(cursor);
				cursor.close();
				return patReturn;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return null;
	}

	/**
	 * Creates a new TemperaturePoint data set in database.
	 * 
	 * @param temperaturePoint
	 * 			the temperaturePoint which should be created
	 */
	private TemperaturePoint create(TemperaturePoint temperaturePoint)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();

			values.put(SqlLiteHelper.COLUMN_TEMPERATURE_MEASUREMENT_ID,
					temperaturePoint.getTmId());
			values.put(SqlLiteHelper.COLUMN_TEMPERATURE_TEMPERATURE,
					temperaturePoint.getTemperature());
			values.put(SqlLiteHelper.COLUMN_TEMPERATURE_TIME, temperaturePoint.getDate()
					.getTimeInMillis());

			try
			{
				long id = database.insert(SqlLiteHelper.TABLE_TEMPERATURES, null, values);
				Cursor cursor = database.query(SqlLiteHelper.TABLE_TEMPERATURES,
						allColumnsTemperature, SqlLiteHelper.COLUMN_TEMPERATURE_ID + " = "
								+ String.valueOf(id), null, null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return new TemperaturePoint(-1, 0, Calendar.getInstance());
				}

				cursor.moveToFirst();
				TemperaturePoint patReturn = cursorToTemperature(cursor);
				cursor.close();
				return patReturn;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return null;
	}

	/**
	 * Creates new Temperature data set in DB and adds them to the specified
	 * Measurement
	 * 
	 * @param measurement
	 * 			which should be associated with the added temperatures
	 * @param temperatures
	 * 			which are added to the newest measurement of an asig
	 * @return The measurement object with an updated stop time.		
	 */
	public Measurement addTemperaturesToMeasurement(Measurement measurement,
			ArrayList<Double> temperatures)
	{
		if (database != null)
		{
			long mId = measurement.getMId();
			int interval = measurement.getInterval();
			Calendar stopTime = measurement.getStoptime();

			// Check the number of previous temperature points for the measurement
			long numberOfPreviousTemperaturePoints = searchTemperaturePointsBy(mId,
					SqlLiteHelper.COLUMN_TEMPERATURE_MEASUREMENT_ID).size();

			for (int i = 0; i < temperatures.size(); i++)
			{
				// Check if the stop time needs to get increased
				if (i != 0 || numberOfPreviousTemperaturePoints != 0)
				{
					stopTime.setTimeInMillis(stopTime.getTimeInMillis() + interval * 1000);
				}
				create(new TemperaturePoint(mId, temperatures.get(i), stopTime));
			}
			// Set new stop time and update the measurement to write the stop time to the database
			measurement.setStoptime(stopTime);
			update(measurement);
			
			// Return the measurement object with updated stop time
			return measurement;
		}
		return null;
	}

	/**
	 * Updates an existing ASIG data set in database.
	 * 
	 * @param asig
	 * 			the new asig which "overwrites" the old one in DB
	 */
	public Asig update(Asig asig)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();
			values.put(SqlLiteHelper.COLUMN_ASIG_UID, asig.getUId());
			values.put(SqlLiteHelper.COLUMN_ASIG_WAIT_COEFF, asig.getWaitCoeff());
			values.put(SqlLiteHelper.COLUMN_ASIG_TEMPERATURE_COEFF_D, asig.getTempCoeffD());
			values.put(SqlLiteHelper.COLUMN_ASIG_TEMPERATURE_COEFF_K, asig.getTempCoeffK());
			values.put(SqlLiteHelper.COLUMN_ASIG_TYPE, asig.getTypeIntVal());

			try
			{
				database
						.update(
								SqlLiteHelper.TABLE_ASIGS,
								values,
								SqlLiteHelper.COLUMN_ASIG_UID + " = " + "'" + asig.getUId() + "'",
								null);
				Cursor cursor = database.query(SqlLiteHelper.TABLE_ASIGS, allColumnsAsig,
						SqlLiteHelper.COLUMN_ASIG_UID + " = " + String.valueOf(asig.getUId()),
						null, null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return new Asig("0x00", 0, 0, 0, Asig.AsigType.A11);
				}

				cursor.moveToFirst();
				Asig asigRet = cursorToAsig(cursor);
				cursor.close();
				return asigRet;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return null;
	}

	/**
	 * Updates an existing Patient data set in database.
	 * 
	 * @param patient
	 * 			the new patient which "overwrites" the old one in DB
	 */
	public Patient update(Patient patient)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();

			values.put(SqlLiteHelper.COLUMN_PATIENT_FIRSTNAME, patient.getFirstName());
			values.put(SqlLiteHelper.COLUMN_PATIENT_LASTNAME, patient.getLastName());
			values.put(SqlLiteHelper.COLUMN_PATIENT_PATIENT_NUMBER,
					patient.getPatientNumber());

			try
			{
				database.update(
						SqlLiteHelper.TABLE_PATIENTS,
						values,
						SqlLiteHelper.COLUMN_PATIENT_ID + " = "
								+ String.valueOf(patient.getPId()), null);

				Cursor cursor = database.query(
						SqlLiteHelper.TABLE_PATIENTS,
						allColumnsPatient,
						SqlLiteHelper.COLUMN_PATIENT_ID + " = "
								+ String.valueOf(patient.getPId()), null, null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return new Patient("", "", "");
				}

				cursor.moveToFirst();
				Patient patReturn = cursorToPatient(cursor);
				cursor.close();
				return patReturn;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		// Has to be handled
		return null;
	}

	/**
	 * Updates an existing Measurement data set in database.
	 * 
	 * @param measurement
	 * 			the new measurement which "overwrites" the old one in DB
	 */
	public Measurement update(Measurement measurement)
	{
		if (database != null)
		{
			ContentValues values = new ContentValues();

			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_PATIENT_ID, measurement.getPId());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_ASIG_ID, measurement.getAId());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_START_TIME, measurement
					.getStarttime().getTimeInMillis());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_STOP_TIME, measurement.getStoptime()
					.getTimeInMillis());
			values.put(SqlLiteHelper.COLUMN_MEASUREMENT_INTERVAL, measurement.getInterval());

			try
			{
				database.update(
						SqlLiteHelper.TABLE_MEASUREMENTS,
						values,
						SqlLiteHelper.COLUMN_MEASUREMENT_ID + " = "
								+ String.valueOf(measurement.getMId()), null);

				Cursor cursor = database.query(SqlLiteHelper.TABLE_MEASUREMENTS,
						allColumnsMeasurement, SqlLiteHelper.COLUMN_MEASUREMENT_ID + " = "
								+ String.valueOf(measurement.getMId()), null, null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return new Measurement(-1, -1, Calendar.getInstance(),
							Calendar.getInstance(), 0);
				}

				cursor.moveToFirst();
				Measurement measReturn = cursorToMeasurement(cursor);
				cursor.close();
				return measReturn;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		// Has to be handled
		return null;
	}

	/**
	 * Searches for ASIG data sets with specific values.
	 * 
	 * @param value
	 * 			the value the DB gets searched by
	 * @param column
	 * 			the column in which the value gets searched
	 */
	public ArrayList<Asig> searchAsigBy(String value, String column)
	{
		ArrayList<Asig> ics = new ArrayList<Asig>();
		if (database != null)
		{

			try
			{
				Cursor cursor = database.rawQuery(
						"Select * from " + SqlLiteHelper.TABLE_ASIGS + " where " + column
								+ " = '" + value + "'", null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return ics;
				}

				cursor.moveToFirst();

				while (!cursor.isAfterLast())
				{
					ics.add(cursorToAsig(cursor));
					cursor.moveToNext();
				}

				cursor.close();
				return ics;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return ics;
	}

	
	/**
	 * Searches for Patient data sets with specific values.
	 * 
	 * @param value
	 * 			the value the DB gets searched by
	 * @param column
	 * 			the column in which the value gets searched
	 */
	public ArrayList<Patient> searchPatientsBy(long value, String column)
	{
		ArrayList<Patient> patients = new ArrayList<Patient>();
		if (database != null)
		{

			try
			{
				Cursor cursor = database.rawQuery(
						"Select * from " + SqlLiteHelper.TABLE_PATIENTS + " where " + column
								+ " = " + String.valueOf(value), null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return patients;
				}

				cursor.moveToFirst();

				while (!cursor.isAfterLast())
				{
					patients.add(cursorToPatient(cursor));
					cursor.moveToNext();
				}

				cursor.close();
				return patients;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return patients;
	}

	/**
	 * Searches for Measurement data sets with specific values.
	 * 
	 * @param value
	 * 			the value the DB gets searched by
	 * @param column
	 * 			the column in which the value gets searched
	 */
	public ArrayList<Measurement> searchMeasurementsBy(long value, String column)
	{
		ArrayList<Measurement> measurements = new ArrayList<Measurement>();
		if (database != null)
		{

			try
			{
				Cursor cursor = database.rawQuery("Select * from "
						+ SqlLiteHelper.TABLE_MEASUREMENTS + " where " + column + " = "
						+ String.valueOf(value) + " order by "
						+ SqlLiteHelper.COLUMN_MEASUREMENT_START_TIME + " desc", null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return measurements;
				}

				cursor.moveToFirst();

				while (!cursor.isAfterLast())
				{
					measurements.add(cursorToMeasurement(cursor));
					cursor.moveToNext();
				}

				cursor.close();
				return measurements;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return measurements;
	}

	/**
	 * Searches for TemperaturePoint data sets with specific values.
	 * 
	 * @param value
	 * 			the value the DB gets searched by
	 * @param column
	 * 			the column in which the value gets searched
	 */
	public ArrayList<TemperaturePoint> searchTemperaturePointsBy(long value, String column)
	{
		ArrayList<TemperaturePoint> temperaturePoints = new ArrayList<TemperaturePoint>();
		if (database != null)
		{

			try
			{
				Cursor cursor = database.rawQuery("Select * from "
						+ SqlLiteHelper.TABLE_TEMPERATURES + " where " + column + " = "
						+ String.valueOf(value), null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return temperaturePoints;
				}

				cursor.moveToFirst();

				while (!cursor.isAfterLast())
				{
					temperaturePoints.add(cursorToTemperature(cursor));
					cursor.moveToNext();
				}

				cursor.close();
				return temperaturePoints;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return temperaturePoints;
	}


	/**
	 * Searches for TemperaturePoint data sets of a specific Measurement.
	 * 
	 * @param measurement
	 * 			the Measurement to look for
	 */
	public ArrayList<TemperaturePoint> searchTemperaturePointsByMeasurement(
			Measurement measurement)
	{
		ArrayList<TemperaturePoint> temperaturePoints = new ArrayList<TemperaturePoint>();

		if (database != null)
		{
			try
			{
				Cursor cursor = database.rawQuery(
						"Select * from " + SqlLiteHelper.TABLE_TEMPERATURES + " where "
								+ SqlLiteHelper.COLUMN_TEMPERATURE_MEASUREMENT_ID + " = "
								+ String.valueOf(measurement.getMId()) + " order by "
								+ SqlLiteHelper.COLUMN_TEMPERATURE_ID + " asc", null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return temperaturePoints;
				}

				cursor.moveToFirst();

				while (!cursor.isAfterLast())
				{
					temperaturePoints.add(cursorToTemperature(cursor));
					cursor.moveToNext();
				}
				cursor.close();
				return temperaturePoints;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return temperaturePoints;
	}

	/**
	 * Deletes an existing ASIG data set in database.
	 * 
	 * @param asig
	 * 			the Asig to be deleted
	 */
	public int delete(Asig asig)
	{
		if (database != null)
		{
			String id = asig.getUId();
			try
			{
				int cols = database.delete(SqlLiteHelper.TABLE_ASIGS,
						SqlLiteHelper.COLUMN_ASIG_UID + " = '" + id + "'", null);
				return cols;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return 0;
	}

	/**
	 * Deletes an existing Patient data set in database.
	 * 
	 * @param patient
	 * 			the Patient to be deleted
	 */
	public int delete(Patient patient)
	{
		if (database != null)
		{
			long id = patient.getPId();
			try
			{
				int cols = database.delete(SqlLiteHelper.TABLE_PATIENTS,
						SqlLiteHelper.COLUMN_PATIENT_ID + " = " + String.valueOf(id), null);
				return cols;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return 0;
	}

	/**
	 * Deletes an existing Measurement data set in database.
	 * 
	 * @param measurement
	 * 			the Measurement to be deleted
	 */
	public int delete(Measurement measurement)
	{
		if (database != null)
		{
			long id = measurement.getMId();
			try
			{
				int cols = database.delete(SqlLiteHelper.TABLE_MEASUREMENTS,
						SqlLiteHelper.COLUMN_MEASUREMENT_ID + " = " + String.valueOf(id), null);
				return cols;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return 0;
	}

	/**
	 * Gets all Patient data sets from database.
	 */
	public ArrayList<Patient> getAllPatients()
	{
		ArrayList<Patient> patients = new ArrayList<Patient>();
		if (database != null)
		{

			try
			{
				Cursor cursor = database.query(SqlLiteHelper.TABLE_PATIENTS,
						allColumnsPatient, null, null, null, null, null);

				if (cursor == null)
				{
					SqlLiteHelper.getInstance(context).getDBErrorHandler()
							.addError(new NullPointerException(nullPointerExc));
					return patients;
				}

				cursor.moveToFirst();

				while (!cursor.isAfterLast())
				{
					patients.add(cursorToPatient(cursor));
					cursor.moveToNext();
				}

				cursor.close();
				return patients;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return patients;
	}

	/**
	 * Returns an ASIG at specific cursor position.
	 * 
	 * @param cursor
	 * 			the cursor of a DB position
	 */
	private Asig cursorToAsig(Cursor cursor)
	{
		Asig asig = new Asig("0x00", 0, 0, 0, Asig.AsigType.A11);
		if (cursor != null)
		{
			try
			{
				asig.setAId(cursor.getLong(0));
				asig.setUId(cursor.getString(1));
				asig.setWaitCoeff(cursor.getInt(2));
				asig.setTempCoeffD(cursor.getDouble(3));
				asig.setTempCoeffK(cursor.getDouble(4));
				asig.setType(cursor.getInt(5));

				return asig;
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return asig;
	}

	/**
	 * Returns a Patient at specific cursor position.
	 * 
	 * @param cursor
	 * 			the cursor of a DB position
	 */
	private Patient cursorToPatient(Cursor cursor)
	{
		Patient patient = null;
		if (cursor != null)
		{
			try
			{
				patient = new Patient(cursor.getString(2), cursor.getString(1),
						cursor.getString(3));
				patient.setPId(cursor.getLong(0));
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return patient;
	}

	/**
	 * Returns a Measurement at specific cursor position.
	 * 
	 * @param cursor
	 * 			the cursor of a DB position
	 */
	private Measurement cursorToMeasurement(Cursor cursor)
	{
		Measurement measurement = null;
		if (cursor != null)
		{
			try
			{
				Calendar calStart = Calendar.getInstance();
				calStart.setTimeInMillis(cursor.getLong(3));
				Calendar calStop = Calendar.getInstance();
				calStop.setTimeInMillis(cursor.getLong(4));

				measurement = new Measurement(cursor.getLong(1), cursor.getLong(2), calStart,
						calStop, cursor.getInt(5));
				measurement.setMId(cursor.getLong(0));
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return measurement;
	}

	/**
	 * Returns a TemperaturePoint at specific cursor position.
	 * 
	 * @param cursor
	 * 			the cursor of a DB position
	 */
	private TemperaturePoint cursorToTemperature(Cursor cursor)
	{
		TemperaturePoint temperature = null;
		if (cursor != null)
		{
			try
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date(cursor.getLong(3)));

				temperature = new TemperaturePoint(cursor.getLong(0), cursor.getLong(1),
						cursor.getDouble(2), cal);
			}
			catch (Exception e)
			{
				SqlLiteHelper.getInstance(context).getDBErrorHandler().addError(e);
			}
		}
		return temperature;
	}
}