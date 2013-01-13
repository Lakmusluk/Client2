package com.example.client2;

import java.util.Vector;
import java.util.regex.Pattern;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class MainActivity extends Activity {
	
    EditText editText;
    TextView textIn;
    Button send;
    Button testen;
//    CheckBox cb1;
//    CheckBox cb2;
    TCPClient myTcpClient;
    TCPClient2 TCP2;
    
    
    //current question & answers
    String question;
     Vector<String> answers = new Vector<String>();
    
    //define the question field
    Dialog questionDialog;
    static final int ID_QUESTION_DIALOG = 1;
    
    //define the selected answer
    private String currentAnswer;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editText = (EditText)findViewById(R.id.editText);
        textIn = (TextView)findViewById(R.id.textin);
        send = (Button)findViewById(R.id.send_button);
        //cb1 = (CheckBox)findViewById(R.id.checkBoxEins);
        //cb2 = (CheckBox)findViewById(R.id.checkBoxZwei);

        new ConnectTask(this.getApplicationContext()).execute("");
        
        new ConnectTask2(this.getApplicationContext()).execute("");
        
        send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				String message = editText.getText().toString();
				
				//sends the message to the server
				if (myTcpClient != null) {
					myTcpClient.sendMessage(message);
				}
			}
        }); 
        
	}

	/*
	 * Build the dialog to select the answer for a given
	 * question
	 */
	protected Dialog onCreateDialog(int id) {
		//decide which dialog should open (currently there is just on!)  
		switch (id) {
		    case ID_QUESTION_DIALOG:
		    // Create the AlertDialog
		    Builder builder = new AlertDialog.Builder(this);
		    //builder.setView(findViewById(R.id.questiondialog));
		   
		    //set the parsed question
		    builder.setTitle(question);
		    
		    //set answer items
		    builder.setSingleChoiceItems(buildStaticItems(answers), 0, new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int item)
	            {
	                //can be implemented if additional features 
	            	// are needed when a items gets clicked
	            	currentAnswer = answers.get(item);
	        }});
		    
		    //create the "send button"
		    builder.setPositiveButton("send",  new OkOnClickListener());

		    //maybe this should be avoided!!!!, now canceling an answer with
		    // with the return button is possible
		    builder.setCancelable(true);
		    
		    //create an show the Dialog
		    AlertDialog dialog = builder.create();
		    dialog.show();
		   }
		   return super.onCreateDialog(id);
		}

	private final class OkOnClickListener implements

	DialogInterface.OnClickListener {
	  public void onClick(DialogInterface dialog, int which) {
		  //TODO:
		  /*
		   * now the selected answer need to be send to the server
		   */
		  
		  try {
		  if (myTcpClient != null) {
			  //this.myTcpClient.out.println("Hallo.");
			  String message = currentAnswer;
			 myTcpClient.sendMessage(message);
		  }
		  }catch (Exception e) {
			  Toast.makeText(getApplicationContext(), "Exception Info " + e.getCause(), Toast.LENGTH_LONG).show();
			  e.printStackTrace();
		  }
	  }
	} 
	 
	//maybe this is needed later to reuse a dialog
	protected void onPrepareDialog(int id, Dialog dialog) {
	 // TODO Auto-generated method stub
	 switch(id){
	 case(ID_QUESTION_DIALOG):
	  break;
	 }
	}
	
	public class ConnectTask extends AsyncTask<String, String, TCPClient> {
    	
        private Context context;

        public ConnectTask(Context context) {
            this.context = context;
        }

        @Override
        protected TCPClient doInBackground(String... params) {
        	//we create a TCPClient object and
    		myTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
    			
    			@Override
    			//here the messageReceived method is implemented
    			public void messageReceived(String message) {
    				//this method calls the onProgressUpdate
    				publishProgress(message);
    			}
    		});
    		myTcpClient.run();
    		
    		return null;   
        }

        @Override
        protected void onProgressUpdate(String... values) {
        	super.onProgressUpdate(values);
            
        	
        	String s1 = values [0];
        	String [] us = s1.split(";");
        	
        	for (int a=0; a<us.length; a++) {
        		if (Pattern.matches(".*\\{", us[a])) {
        			String sneu = us[a].replace("{", "");
        			//textIn.setText(sneu);
        			question=sneu;
        		}
        		if (Pattern.matches("=.*", us[a])){
        			String sneu = us[a].replace("=", "");
        			//cb1.setText(sneu);
        			answers.add(sneu);
        		}
        		if (Pattern.matches("~1.*", us[a])){
        			String sneu = us[a].replace("~1", "");
        			//cb2.setText(sneu);
        			answers.add(sneu);
        		}
        	}
        	showDialog(ID_QUESTION_DIALOG);
    		//context.addView(rg);
        	
        }
        
    }

	/*
	 * method to build an static array of strings for the
	 * dialog (the dialog demands this) out of the
	 * dynamic vector
	 */
	private CharSequence[] buildStaticItems(Vector<String> vec){
		int size = vec.size();
		CharSequence[] charSequence  = new CharSequence[size];
		for (int i=0;i<size;i++)
			charSequence[i]=vec.get(i);
        return charSequence;
	}
	
public class ConnectTask2 extends AsyncTask<String, String, TCPClient2> {
    	
        private Context context;

        public ConnectTask2(Context context) {
            this.context = context;
        }

        @Override
        protected TCPClient2 doInBackground(String... params) {
        	//we create a TCPClient object and
    		TCP2 = new TCPClient2(new TCPClient2.OnMessageReceived() {
    			
    			@Override
    			//here the messageReceived method is implemented
    			public void messageReceived(String message) {
    				//this method calls the onProgressUpdate
    				publishProgress(message);
    			}
    		});
    		TCP2.run();
    		
    		return null;   
        }

        @Override
        protected void onProgressUpdate(String... values) {
        	super.onProgressUpdate(values);
            
        	
//        	String s1 = values [0];
//        	String [] us = s1.split(";");
//        	
//        	for (int a=0; a<us.length; a++) {
//        		if (Pattern.matches(".*\\{", us[a])) {
//        			String sneu = us[a].replace("{", "");
//        			//textIn.setText(sneu);
//        			question=sneu;
//        		}
//        		if (Pattern.matches("=.*", us[a])){
//        			String sneu = us[a].replace("=", "");
//        			//cb1.setText(sneu);
//        			answers.add(sneu);
//        		}
//        		if (Pattern.matches("~1.*", us[a])){
//        			String sneu = us[a].replace("~1", "");
//        			//cb2.setText(sneu);
//        			answers.add(sneu);
//        		}
//        	}
//        	showDialog(ID_QUESTION_DIALOG);
//    		//context.addView(rg);
        	
        }
        
    }
	
}
