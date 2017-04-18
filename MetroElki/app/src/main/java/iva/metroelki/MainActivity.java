package iva.metroelki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import java.sql.SQLException;
import java.util.ArrayList;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Calendar;



public class MainActivity extends AppCompatActivity {
    //Глобальные переменные
    TextView textJson;
    Button btnSend;

    SuburbanAsyncTask sat;

    String statioFrom = "s9601862";
    String stationTo = "s2000002";

    Calendar calendar = Calendar.getInstance();
    int curHours = calendar.get(calendar.HOUR_OF_DAY) + 3;
    int curMinutes = calendar.get(calendar.MINUTE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlitedbapp sqlHelper = new sqlitedbapp(getApplicationContext());
       MetroElkiMap map=new MetroElkiMap(sqlHelper);
        System.out.println("before result");
        Way res=map.find_ways(sqlHelper,"строгино","комсомольская");
        System.out.println(res.steps.size());
        for(int s=0;s<res.steps.size();s++){System.out.println(res.steps.get(s).id_first+" "+res.steps.get(s).id_line+" "+res.steps.get(s).id_second);}
        btnSend = (Button) findViewById(R.id.button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sat = new SuburbanAsyncTask();
                sat.execute();
            }
        });

System.out.println("result");
    }




    class SuburbanAsyncTask extends AsyncTask<Void, ArrayList<String>, JSONArray> {

        // фоновая работа
        @Override
        protected JSONArray doInBackground(Void... params) {


            JSONObject resultResponse = TrainSchedule.getHTML(TrainSchedule.makingURL(statioFrom,stationTo));
            JSONArray threads = (JSONArray) resultResponse.get("threads");
            return threads;

        }
        // выполняется после doInBackground, имеет доступ к UI
        protected void onPostExecute(JSONArray result) {

            super.onPostExecute(result);

            String curTime = TrainSchedule.getTime(curHours, curMinutes);
            Log.w("res",curTime);
            ArrayList<String> nearestDepTime = new ArrayList<String>(TrainSchedule.getNearestDepTime(result, curTime));
            Log.w("res", nearestDepTime.toString());

            ArrayList<String> listOfArrivaleTime = new ArrayList<String>(TrainSchedule.getArrivalTime(result, nearestDepTime));
            Log.w("res", listOfArrivaleTime.toString());
            textJson.setText(nearestDepTime.toString() + "\n" + listOfArrivaleTime.toString() + "\n");
        }

    }
}
