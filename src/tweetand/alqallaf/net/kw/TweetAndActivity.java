package tweetand.alqallaf.net.kw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;



import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import services.tweetand.alqallaf.net.kw.User;
import services.tweetand.alqallaf.net.kw.sqldb;





public class TweetAndActivity extends Activity 
{
	String ThePreviousTweet="";
	String TheCommingTweet="";
	
	public TimerTask mTimerTask;
	final Handler handler = new Handler();
	final Handler handler2 = new Handler();
	Button hButton, hButtonStop;
	EditText TextName;
	boolean firsttime=true;
	public Timer t = new Timer();
	
	String url=""; // =  "https://twitter.com/statuses/user_timeline/ALQallaf.json?screen_name=ALQallaf_Dev&count=1";
	//private TextView textView;
	
	private int nCounter = 0;

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextName = (EditText) findViewById(R.id.editTextName);
        //textView = (TextView) findViewById(R.id.TextView01);
        hButton = (Button)findViewById(R.id.idButton);
        hButton.setOnClickListener(mButtonStartListener);
        hButtonStop = (Button)findViewById(R.id.idButtonStop);
        hButtonStop.setOnClickListener(mButtonStopListener);
        
        //TextName.setText("Hello");
        //take name from db and put it on the textbox
        GetUserFromDb();
        //DeleteFromDB();
        //SetIntoDb();
     
        if (! TextName.getText().toString().isEmpty())
        {
        	url =  "https://twitter.com/statuses/user_timeline/"+TextName.getText().toString()+".json?screen_name="+TextName.getText().toString()+"&count=1";
        Log.d("TIMER", "The url is "+url);
        GetFirstActivationTweet GetpreviousTweet = new GetFirstActivationTweet();
        GetpreviousTweet.execute(new String[] { url });
        }
    }
    
	View.OnClickListener mButtonStartListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if (TextName.getText().length()!=0)
			{
				Log.d("TIMER", "in [onClick()]");
				//TextName.setText("ALQallaf_dev");
				TextName.setEnabled(false);
				UpdateDb();
				url =  "https://twitter.com/statuses/user_timeline/"+TextName.getText().toString()+".json?screen_name="+TextName.getText().toString()+"&count=1";
		        GetFirstActivationTweet GetpreviousTweet = new GetFirstActivationTweet();
		        GetpreviousTweet.execute(new String[] { url });
				doTimerTask();
			}
		}
	};
 
	View.OnClickListener mButtonStopListener = new OnClickListener() 
	{
    	public void onClick(View v) 
    	{
     		stopTask();
    	}
    };
    
    
    public void doTimerTask()
    {
    	//hButton.setClickable(false);
    	//hButton.setActivated(false);
    
    	hButton.setEnabled(false);
    	mTimerTask = new TimerTask() 
    	{
    	        public void run() {
    	        		AsyncTask.execute(new Runnable(){
    	                 
    	                        public void run() 
    	                        {    	            
    	                        	GetTweetTask task = new GetTweetTask();
	                        		task.execute(new String[] { url });
	                        		Log.d("TIMER", "in run()1");	
								}
														});
    	                        
    	                        	nCounter++;                                	                        	
    	                        	Log.d("TIMER", "in run()2");                       	
    	                        }
    	};
 
            // public void schedule (TimerTask task, long delay, long period) 
    	    t.schedule(mTimerTask, 5000, 6000);  // 
        	
    }
    
    public void stopTask()
    {
    		  //hButton.setClickable(true);
    		  //hButton.setActivated(true);
    	hButton.setEnabled(true);
    	TextName.setEnabled(true);
    	if(mTimerTask!=null)
    	  {
    	    //hTextView.setText("Timer canceled: " + nCounter);
 
    	    Log.d("TIMER", "timer canceled");
    	    mTimerTask.cancel();
    	  }
 
    }
    
    
	private class GetTweetTask extends AsyncTask<String, Void, Reader> 
	{
		@Override
		protected Reader doInBackground(String... urls) 
		{
			//String response = "";
	        GetGsonString data = new GetGsonString();
	        InputStream source = data.retrieveStream(url);
	        Reader reader = new InputStreamReader(source);

	        return reader;
		}

		@Override
		protected void onPostExecute(Reader reader) 
		{
			//textView.setText(result); result here
			Gson gson = new Gson();
			User[] response = gson.fromJson(reader, User[].class); 

			//textView.setText(response[0].user.TheScreenName +"  "+ response[0].TheText);
			//textView.setText(response[0].user.TheScreenName +"  "+ response[0].TheText);
			TheCommingTweet=response[0].TheText.toString();
			if (!ThePreviousTweet.toString().matches(TheCommingTweet.toString()))
			{
				Log.d("TIMER", "In onPostExecute [GetTweetTask] inside first IF ");
				String[] words = TheCommingTweet.split (" ");
				if (words[0].toString().equals("android"))
				{
					//for (int i=0;i<3;i++)
					//{
					PlayTheSound PalyTweetSound = new PlayTheSound();
					PalyTweetSound.execute(new String[] { url });
					Log.d("TIMER", "In onPostExecute [GetTweetTask] inside second IF ");			
			
					//}
					ThePreviousTweet = TheCommingTweet.toString();
				}
				ThePreviousTweet = TheCommingTweet.toString();				
			}
			Log.d("TIMER", "In onPostExecute [GetTweetTask]");
		}
		
	}
	
	private class GetFirstActivationTweet extends AsyncTask<String, Void, Reader> 
	{
		@Override
		protected Reader doInBackground(String... urls) 
		{
			//String response = "";
	        GetGsonString data = new GetGsonString();
	        InputStream source = data.retrieveStream(url);
	        Reader reader = new InputStreamReader(source);
	        
	        return reader;
		}

		@Override
		protected void onPostExecute(Reader reader) 
		{
			Gson gson = new Gson();
			User[] response = gson.fromJson(reader, User[].class); 
			//textView.setText(response[0].user.TheScreenName +"  "+ response[0].TheText);
			ThePreviousTweet=response[0].TheText.toString();
			Log.d("TIMER", "In onPostExecute [GetFirstActivationTweet]");
		}
	}
	
	private class PlayTheSound extends AsyncTask<String, Void, Reader> 
	{
		@Override
		protected Reader doInBackground(String... urls) 
		{		
            MediaPlayer mp = MediaPlayer.create(TweetAndActivity.this, R.raw.birdtweet);
            mp.start();
			return null;
		}

		@Override
		protected void onPostExecute(Reader reader) 
		{

			Log.d("TIMER", "In onPostExecute [PlayTheSound]");
		}
	}
	
	
	public void GetUserFromDb()
	{
		try {
			//String s = EditsqlRow.getText().toString();
			long l = Long.parseLong("1");
			sqldb hon = new sqldb(this);
			hon.open();
			String returnedName = hon.getNameFromDB(l);
			//String returnedUnit = hon.getUnitFromDB(l);
			//String returnedPrice = hon.getPriceFromDB(l);
			//String returnedLPrice = hon.getLPriceFromDB(l);
			hon.close();
			TextName.setText(returnedName.toString());
			} 
		catch (Exception e) 
		{
			
			
	        AlertDialog.Builder alert = new AlertDialog.Builder(this);  

	        alert.setTitle("TweetAnd");  
	        alert.setMessage("Twitter User name, Please");  

	        // Set an EditText view to get user input   
	        final EditText inputName = new EditText(this);  
	        alert.setView(inputName);  

	        alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {  
	        public void onClick(DialogInterface dialog, int whichButton) {  
	            TextName.setText(inputName.getText().toString());
	            SetIntoDb();
	          }  
	        }); 
	        alert.show();
		}
	}
	
	public void SetIntoDb()
	{
		boolean Worked = true;
		try {
			String name = TextName.getText().toString();
			sqldb entry = new sqldb(TweetAndActivity.this);
			entry.open();
			
			entry.createEntry(name);
			entry.close();

		} catch (Exception e) {
			Worked = false;
			String whatistheerror = e.toString();
			Dialog TheD = new Dialog(this);
			TheD.setTitle("NoOoOo");
			TextView tv = new TextView(this);
			tv.setText(whatistheerror);
			TheD.setContentView(tv);
			TheD.show();
		} finally {
			if (Worked) {
				Dialog TheGoodD = new Dialog(this);
				TheGoodD.setTitle("Perfect");
				TextView tv = new TextView(this);
				tv.setText("Success");
				TheGoodD.setContentView(tv);
				TheGoodD.show();
			}
		}
		
	}
	
	public void DeleteFromDB()
	{
		try 
		{
			String sRow1 = "1";
			long lRow1 = Long.parseLong(sRow1);
			sqldb ex1 = new sqldb(this);
			ex1.open();
			ex1.deleteEntryInDB(lRow1);
			ex1.close();
		} 
		catch (Exception e) 
		{	
			String TheBaderror = e.toString();
			Dialog TheD = new Dialog(this);
			TheD.setTitle("Error When Deleting");
			TextView tv = new TextView(this);
			tv.setText(TheBaderror);
			TheD.setContentView(tv);
			TheD.show();
		}
	}
	
	public void UpdateDb()
	{
	try {
			String mName = TextName.getText().toString();
			long lRow = Long.parseLong("1");
			sqldb ex = new sqldb(this);
			ex.open();
			ex.updateEntryInDB(lRow, mName);
			ex.close();
		} 
		catch (Exception e) 
		{
			String TheBaderror = e.toString();
			Dialog TheD = new Dialog(this);
			TheD.setTitle(e.getLocalizedMessage());
			TextView tv = new TextView(this);
			tv.setText(TheBaderror);
			TheD.setContentView(tv);
			TheD.show();
		}	
	}
	
}
    
