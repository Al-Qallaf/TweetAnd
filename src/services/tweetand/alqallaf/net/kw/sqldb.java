package services.tweetand.alqallaf.net.kw;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class sqldb {
	
	public static final String ROWID = "theid";
	public static final String CO_NAME = "com_name";
	private static final String MYDB_NAME = "ttuser";
	private static final String MYDB_TABLE = "tttable";
	private static final int MYDB_VERSION = 1;
	private DbHelper TheHelper;
	private final Context TheContext;
	private SQLiteDatabase TheDB;
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, MYDB_NAME, null, MYDB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MYDB_TABLE + " (" +
					ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					CO_NAME + " TEXT NOT NULL);"					
			);	
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MYDB_TABLE);
			onCreate(db);
		}		
	}
	
	public sqldb(Context c){
		TheContext = c;
	}

	public sqldb open() throws SQLException{
		TheHelper = new DbHelper(TheContext);
		TheDB = TheHelper.getWritableDatabase();
		return this;
	}
	 public void close(){
		 TheHelper.close();
	 }
	
	//twi
	public long createEntry(String name) {
		ContentValues cv = new ContentValues();
		cv.put(CO_NAME, name);
		return TheDB.insert(MYDB_TABLE, null, cv);
	}

	//twi
	public String getNameFromDB(long l) throws SQLException{
		String[] columns = new String[]{ ROWID, CO_NAME};
		Cursor c = TheDB.query(MYDB_TABLE, columns, ROWID + "=" + l, null, null, null, null);
		if (c != null){
			c.moveToFirst();
			String name = c.getString(1);
			return name;
		}
		return null;
	}
	
	//twi
	public void updateEntryInDB(long lRow, String coName) throws SQLException{
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(CO_NAME, coName);
		TheDB.update(MYDB_TABLE, cvUpdate, ROWID + "=" + lRow, null);	
	}
	
	//twi
	public void deleteEntryInDB(long lRow1) throws SQLException
	{
		TheDB.delete(MYDB_TABLE, ROWID + "=" + lRow1, null);
	}
}