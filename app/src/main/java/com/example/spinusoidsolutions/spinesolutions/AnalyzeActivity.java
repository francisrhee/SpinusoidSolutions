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
        import java.time.LocalDate;
        import java.time.LocalDateTime;
        import java.util.Date;
        import java.util.List;

//Following imports are necessary for JSON parsing

public class AnalyzeActivity extends AppCompatActivity {
//    ArrayList<String> numberlist = new ArrayList<>();

//    @SuppressLint("SimpleDateFormat")
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        SimpleDateFormat neatDateFormatter= new SimpleDateFormat("MM/dd/yyyy");
        Date beginningFilterDate = new Date();
        Date endingFilterDate = new Date();

        try {
            beginningFilterDate = neatDateFormatter.parse("03/03/2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endingFilterDate = neatDateFormatter.parse("04/01/2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            createGraphs(beginningFilterDate, endingFilterDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createGraphs(Date beginningFilterDate, Date endingFilterDate) throws Exception {

        String fileName = "SpinusoidData.json";
//      reading json file, converting to string
        String json = loadJSONFromAsset(this);
        Moshi moshi = new Moshi.Builder().build();

        Type spineDataType = Types.newParameterizedType(List.class, SpineData.class);
        JsonAdapter<List<SpineData>> jsonAdapter = moshi.adapter(spineDataType);

//      creating list of objects
        List<SpineData> dataArray = jsonAdapter.fromJson(json);

        SimpleDateFormat dateFormatter=new SimpleDateFormat("MM/dd/yyyy");
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
//        Date currentDate = new Date();
        Date [] dates = new Date[dataArray.size()];
        for(int i =0; i < dataArray.size(); i++)
        {
            try {
                dates[i] = dateFormatter.parse(dataArray.get(i).date);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        for(int i =0; i < dataArray.size(); i++)
        {
            if(dates[i].after(beginningFilterDate) && dates[i].before(endingFilterDate))
            series.appendData(new DataPoint(dates[i], dataArray.get(i).difference), true, dataArray.size());
        }
        graph.addSeries(series);
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(dates.length); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(dates[0].getTime());
        graph.getViewport().setMaxX(dates[dates.length-1].getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(true);
//
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
