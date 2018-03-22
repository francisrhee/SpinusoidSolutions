package com.example.spinusoidsolutions.spinesolutions;
//package com.javacreed.examples.gson.part2;

        import android.content.Context;
        import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;


        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
import java.io.Reader;
        import java.lang.reflect.Type;
        import java.nio.file.Files;
        import java.nio.file.Paths;
        import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
        import java.util.List;



//Following imports are necessary for JSON parsing

public class AnalyzeActivity extends AppCompatActivity {
//    ArrayList<String> numberlist = new ArrayList<>();

//    @SuppressLint("SimpleDateFormat")
    @Override




    protected void onCreate(Bundle savedInstanceState) {




// try {
//            new ReadJsonList().run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try{
//            InputStream is = getAssets().open("SpinusoidData.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read();
//            is.close();
//
//            String json = new String(buffer,"UTF-8");
//
//
//            Moshi moshi = new Moshi.Builder().build();
//
//            Type spineDataType = Types.newParameterizedType(List.class, SpineData.class);
//            JsonAdapter<List<SpineData>> jsonAdapter = moshi.adapter(spineDataType);
//
//            List<SpineData> dataArray = jsonAdapter.fromJson(json);
//            System.out.println(dataArray);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String [] stringDates = new String[5];
        stringDates[0] = "03/01/2018";
        stringDates[1] = "03/02/2018";
        stringDates[2] = "03/03/2018";
        stringDates[3] = "03/04/2018";
        stringDates[4] = "03/05/2018";

        int[] data = new int[5];
        data[0] = 5;
        data[1] = 4;
        data[2] = 5;
        data[3] = 3;
        data[4] = 8;

        SimpleDateFormat dateFormatter=new SimpleDateFormat("MM/dd/yyyy");
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
//        Date currentDate = new Date();
        Date [] dates = new Date[5];
        for(int i =0; i < stringDates.length; i++)
        {
            try {
                dates[i] = dateFormatter.parse(stringDates[i]);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        for(int i =0; i < data.length; i++)
        {
            series.appendData(new DataPoint(dates[i], data[i]), true, data.length);
        }
        graph.addSeries(series);
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(dates.length); // only 4 because of the space

// set manual x bounds to have nice steps
        graph.getViewport().setMinX(dates[0].getTime());
        graph.getViewport().setMaxX(dates[dates.length-1].getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(true);
    }

    public void run() throws Exception {

        String fileName = "SpinusoidData.json";

        String json = loadJSONFromAsset(this);
        Moshi moshi = new Moshi.Builder().build();

        Type spineDataType = Types.newParameterizedType(List.class, SpineData.class);
        JsonAdapter<List<SpineData>> jsonAdapter = moshi.adapter(spineDataType);

        List<SpineData> dataArray = jsonAdapter.fromJson(json);
        System.out.println(dataArray);
    }
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("SpinusoidData.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

}
//    public static void main(String[] args) throws IOException {
//        try(Reader reader = new InputStreamReader(JsonToJava.class.getResourceAsStream("/Server1.json"), "UTF-8")){
//            Gson gson = new GsonBuilder().create();
//            Person p = gson.fromJson(reader, Person.class);
//            System.out.println(p);
//        }
//    }


//    public final class ReadJsonList {
//        public void run() throws Exception {
////
//            InputStream is = getAssets().open("SpinusoidData.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read();
//            is.close();
//
//            String json = new String(buffer,"UTF-8");
//
//
//            Moshi moshi = new Moshi.Builder().build();
//
//            Type spineDataType = Types.newParameterizedType(List.class, SpineData.class);
//            JsonAdapter<List<SpineData>> jsonAdapter = moshi.adapter(spineDataType);
//
//            List<SpineData> dataArray = jsonAdapter.fromJson(json);
//            System.out.println(dataArray);
//            }
//        }


//    public void get_json()
//    {
//        String json;
//        try
//        {
//            InputStream is = getAssets().open("SpinusoidData.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read();
//            is.close();
//
//            json = new String(buffer,"UTF-8");
//            JSONArray jsonArray = new JSONArray(json);
//
//            for(int i =0; i < jsonArray.length();i++){
//                JSONObject obj = jsonArray.getJSONObject(i);
//                if(obj.getString(""))
//            }
//
//        }catch (IOException e)
//        {
//            e.printStackTrace();
//        }

//    public JSONObject parseJSONData() {
//        String JSONString = null;
//        JSONObject JSONObject = null;
//        try {
//
//            //open the inputStream to the file
//            InputStream inputStream = getAssets().open("SpinusoidData.json");
//
//            int sizeOfJSONFile = inputStream.available();
//
//            //array that will store all the data
//            byte[] bytes = new byte[sizeOfJSONFile];
//
//            //reading data into the array from the file
//            inputStream.read(bytes);
//
//            //close the input stream
//            inputStream.close();
//
//            JSONString = new String(bytes, "UTF-8");
//            JSONObject = new JSONObject(JSONString);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        catch (JSONException x) {
//            x.printStackTrace();
//            return null;
//        }
//        return JSONObject;
//    }
//}


