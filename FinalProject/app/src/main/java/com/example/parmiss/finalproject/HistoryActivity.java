package com.example.parmiss.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import databaseubm.Constants;
import databaseubm.MyDatabase;

public class HistoryActivity extends Activity implements AdapterView.OnItemClickListener{
    ListView myList;
    MyDatabase db;
    SimpleCursorAdapter myAdapter;
    List<String> actionArray = new ArrayList<String>();
    List<String> dateArray = new ArrayList<String>();
    List<String> timeEndArray = new ArrayList<String>();
    List<String> timeElapsedArray = new ArrayList<String>();
    List<String> SummaryArray = new ArrayList<String>();
    List<Integer> Seconds = new ArrayList<Integer>();
    SharedPreferences sharedPrefs;
    TextView Stats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        String user = sharedPrefs.getString("name", "N");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_rel);
        myList = (ListView) findViewById(R.id.listView);
        db = new MyDatabase(this);
        // myList = (ListView) findViewById(R.id.listView);

        Stats = (TextView) findViewById(R.id.statavg);

        // For the cursor adapter, specify which columns go into which views
        String[] columns = {Constants.NAME, Constants.ACTION,Constants.DATE,Constants.TIMEEND,Constants.TIMEELAPSED};


        Cursor cursor = db.getLogData(user);

        String [] dne = {};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dne);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()) {

                int index1 = cursor.getColumnIndex(Constants.NAME);
                int index2 = cursor.getColumnIndex(Constants.ACTION);
                int index3 = cursor.getColumnIndex(Constants.DATE);
                int index4 = cursor.getColumnIndex(Constants.TIMEEND);
                int index5 = cursor.getColumnIndex(Constants.TIMEELAPSED);

                actionArray.add(cursor.getString(index2));
                dateArray.add(cursor.getString(index3));
                timeEndArray.add(cursor.getString(index4));
                timeElapsedArray.add(cursor.getString(index5));
                SummaryArray.add(cursor.getString(index2) + " on " + cursor.getString(index3));

                // Calculate number of elapsed seconds
                String[] parts= cursor.getString(index5).split(",");
                int hours = Integer.parseInt(parts[0].replace("HR: ", "").trim());
                int min =   Integer.parseInt(parts[1].replace("Min: ", "").trim());
                int sec =   Integer.parseInt(parts[2].replace("Secs: ", "").trim());

                int secondselapsed = 60*60*hours + 60*min + sec;
                Seconds.add(secondselapsed);

                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SummaryArray);
            }
        }else {

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dne);


        }

        myList.setAdapter(adapter);
        myList.setOnItemClickListener(this);

        Stats.setText(timeAverage(average(Seconds)));


    }


    public double average(List<Integer> data) {
        int sum = 0;
        double average;

        for(int i=0; i < data.size(); i++){
            sum = sum + data.get(i);
        }
        average = (double)sum/data.size();
        return average;
    }

    public String timeAverage(double sec){
        double hours = sec / 3600;
        double minutes = (sec % 3600) / 60;
        double seconds = sec % 60;
        return "Average Sleep Time:\n \t "+String.valueOf(Math.round(hours))+" Hours, "+ String.valueOf(Math.round(minutes))+" Mins, and " + String.valueOf(Math.round(seconds)+" Secs");
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView clickedTextView = (TextView) view;
        Toast.makeText(this,"Summary: \n \n"+
                "Action: "+ actionArray.get(position)+"\n"+
                "Time of Alert: "+ timeEndArray.get(position)+"\n"+
                "Time Elapsed: "+timeElapsedArray.get(position) ,Toast.LENGTH_LONG).show();

        // Toast.makeText(this, "row " +position+":  "+clickedTextView.getText(), Toast.LENGTH_SHORT).show();
    }


}

//802.5
//802.3
//802.2
//802.11