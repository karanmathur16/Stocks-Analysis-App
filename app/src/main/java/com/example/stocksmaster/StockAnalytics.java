package com.example.stocksmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;

/**
 * The type Stock analytics.
 */
public class StockAnalytics extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CandleStickChart mChart;
    /**
     * The Symbol textview.
     */
    TextView symbolTextview;
    /**
     * The Url.
     */
    String url, /**
     * The Input line.
     */
    inputLine, /**
     * The Result.
     */
    result;
    /**
     * The X axis values.
     */
    ArrayList<String> xAxisValues;
    /**
     * The X axis values day.
     */
    ArrayList<String> xAxisValuesDay;
    /**
     * The X axis values week.
     */
    ArrayList<String> xAxisValuesWeek;
    /**
     * The Entries.
     */
    ArrayList<CandleEntry> entries;
    /**
     * The Lines.
     */
    ArrayList<CandleDataSet> lines;
    /**
     * The Close rate.
     */
    float closeRate;
    /**
     * The Open rate.
     */
    float openRate;
    /**
     * The High rate.
     */
    float highRate;
    /**
     * The Low rate.
     */
    float lowRate;
    /**
     * The Ind.
     */
    int ind;
    /**
     * The Set 1.
     */
    CandleDataSet set1;
    /**
     * The Symbol.
     */
    String symbol = null;
    /**
     * The Apikey.
     */
    String apikey = "WOC6ZP683U6ZI228";
    /**
     * The Filters.
     */
    String[] filters = {"Daily","Weekly","1 Day"};
    /**
     * The Allowed sell.
     */
    String[] allowedSell = {"AAPL","IBM","TSCDF","TCEHY"};
    /**
     * The Sell button.
     */
    Button sellButton;
    /**
     * The Buy button.
     */
    Button buyButton;
    /**
     * The Extras.
     */
    Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_analytics);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,filters);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        symbolTextview = findViewById((R.id.symbolTextView));

        mChart = findViewById(R.id.chart1);

        extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

//        Intent main = new Intent(this,MainActivity.class);
//        main.putExtra("watchList",extras.getStringArrayList("watchList"));
//        main.putExtra("watchListPrices",extras.getStringArrayList("watchListPrices"));
        symbol = extras.getString("symbol");
        symbolTextview.setText(symbol);

        sellButton = findViewById(R.id.sellButton);
        buyButton = findViewById(R.id.buyButton);
        for(int i = 0 ; i<allowedSell.length ; i++){
            if(Objects.equals(symbol, allowedSell[i])){
                sellButton.setVisibility(View.VISIBLE);
                buyButton.setVisibility(View.VISIBLE);
                break;
            }
            else {
                sellButton.setVisibility(View.INVISIBLE);
                buyButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * On selecting item from Spinner
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        mChart.notifyDataSetChanged();
        mChart.invalidate();

        String item = filters[pos];
        if(Objects.equals(item, "Daily")) getStocksDataDaily(symbol);
        if(Objects.equals(item, "Weekly")) getStocksDataWeekly(symbol);
        if(Objects.equals(item, "1 Day")) getStocksDataDay(symbol);

        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    /**
     * Get daily stock data API call
     * @param symbol
     */
    private void getStocksDataDaily(String symbol) {
        Thread thread = new Thread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                boolean successful = false;
                while (!successful) {
                    try {
                        url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=" + apikey;
                        URL api = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
                        connection.connect();

                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((inputLine = reader.readLine()) != null) {
                            stringBuilder.append(inputLine);
                        }
                        reader.close();
                        streamReader.close();
                        successful = true;

                        result = stringBuilder.toString();

                        JSONObject dailyDataJsonObj = new JSONObject(result);

                        final JSONObject timeSeriesDailyJsonObj = dailyDataJsonObj.getJSONObject("Time Series (Daily)");

                        Iterator<String> iterator = timeSeriesDailyJsonObj.keys();

                        xAxisValues = new ArrayList<String>();
                        entries = new ArrayList<>();
                        mChart.resetTracking();
                        ind = 0;
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            xAxisValues.add(key);
                            if (key != null) {
                                JSONObject finalObj = timeSeriesDailyJsonObj.optJSONObject(key);
                                closeRate = Float.parseFloat(finalObj.optString("4. close"));
                                openRate = Float.parseFloat(finalObj.optString("1. open"));
                                highRate = Float.parseFloat(finalObj.optString("2. high"));
                                lowRate = Float.parseFloat(finalObj.optString("3. low"));
                                entries.add(new CandleEntry(ind, highRate,lowRate,openRate,closeRate,getResources().getDrawable(R.drawable.ic_launcher_background)));
                                ind  = ind+1;
                                if(ind == 60) break;
                            }
                        }

                        XAxis xAxis = mChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        YAxis leftAxis = mChart.getAxisLeft();
                        leftAxis.setDrawGridLines(false);
                        leftAxis.setDrawAxisLine(false);
                        xAxis.setAxisMaximum(xAxisValues.size());
                        mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
                        set1 = new CandleDataSet(entries, "Daily");
                        set1.setDrawIcons(false);
                        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set1.setShadowColor(Color.DKGRAY);
                        set1.setShadowWidth(0.7f);
                        set1.setDecreasingColor(Color.RED);
                        set1.setDecreasingPaintStyle(Paint.Style.FILL);
                        set1.setIncreasingColor(Color.parseColor("#59c639"));
                        set1.setIncreasingPaintStyle(Paint.Style.FILL);
                        set1.setNeutralColor(Color.BLUE);
                        set1.setDrawValues(false);

                        CandleData data = new CandleData(set1);

                        mChart.setData(data);
                        mChart.getDescription().setText("");
                        mChart.getDescription().setTextColor(Color.BLUE);
                        mChart.animateY(1400, Easing.EaseInOutBounce);
                        mChart.invalidate();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get intra-day stocks data API
     * @param symbol
     */

    private void getStocksDataDay(String symbol) {
        Thread thread = new Thread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                boolean successful = false;
                while (!successful) {
                    try {
                        url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&interval=30min&symbol=" + symbol + "&apikey=" + apikey;
                        URL api = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
                        connection.connect();

                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((inputLine = reader.readLine()) != null) {
                            stringBuilder.append(inputLine);
                        }
                        reader.close();
                        streamReader.close();
                        successful = true;

                        result = stringBuilder.toString();

                        JSONObject dailyDataJsonObj = new JSONObject(result);

                        final JSONObject timeSeriesDailyJsonObj = dailyDataJsonObj.getJSONObject("Time Series (30min)");

                        Iterator<String> iterator = timeSeriesDailyJsonObj.keys();

                        final JSONObject metaData = dailyDataJsonObj.getJSONObject("Meta Data");
                        String lastRefresh = metaData.getString("3. Last Refreshed");
                        Date tempdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastRefresh);
                        String maxDate = new SimpleDateFormat("yyyy-MM-dd").format(tempdate);

                        xAxisValuesDay = new ArrayList<String>();
                        entries = new ArrayList<>();
                        mChart.resetTracking();
                        ind = 0;
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            xAxisValuesDay.add(key);
                            if (key != null) {
                                JSONObject finalObj = timeSeriesDailyJsonObj.optJSONObject(key);
                                closeRate = Float.parseFloat(finalObj.optString("4. close"));
                                openRate = Float.parseFloat(finalObj.optString("1. open"));
                                highRate = Float.parseFloat(finalObj.optString("2. high"));
                                lowRate = Float.parseFloat(finalObj.optString("3. low"));
                                entries.add(new CandleEntry(ind, highRate,lowRate,openRate,closeRate,getResources().getDrawable(R.drawable.ic_launcher_background)));
                                ind  = ind+1;
                            }
                        }

                        ListIterator<String> iteratorDates = xAxisValuesDay.listIterator();
                        while (iteratorDates.hasNext()) {
                            String datetime = iteratorDates.next();
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
                            String newDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                            if(newDate.equals(maxDate)){
                                String newTime = new SimpleDateFormat("HH:mm").format(date);
                                iteratorDates.set(newTime);
                            }
                            else iteratorDates.remove();
                        }

                        XAxis xAxis = mChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        YAxis leftAxis = mChart.getAxisLeft();
                        leftAxis.setDrawGridLines(false);
                        leftAxis.setDrawAxisLine(false);
                        //Collections.reverse(xAxisValuesDay);
                        xAxis.setAxisMaximum(xAxisValuesDay.size());
                        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValuesDay));
                        set1 = new CandleDataSet(entries, "1 Day" + " (" +maxDate + " )");
                        set1.setDrawIcons(false);
                        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set1.setShadowColor(Color.DKGRAY);
                        set1.setShadowWidth(0.7f);
                        set1.setDecreasingColor(Color.RED);
                        set1.setDecreasingPaintStyle(Paint.Style.FILL);
                        set1.setIncreasingColor(Color.parseColor("#59c639"));
                        set1.setIncreasingPaintStyle(Paint.Style.FILL);
                        set1.setNeutralColor(Color.BLUE);
                        set1.setDrawValues(false);

                        CandleData data = new CandleData(set1);

                        mChart.setData(data);
                        mChart.getDescription().setText("");
                        mChart.getDescription().setTextColor(Color.BLUE);
                        mChart.animateY(1400, Easing.EaseInOutBounce);
                        mChart.invalidate();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get Weekly stocks data API
     * @param symbol
     */
    private void getStocksDataWeekly(String symbol) {
        Thread thread = new Thread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                boolean successful = false;
                while (!successful) {
                    try {
                        url = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=" + symbol + "&apikey=" + apikey;
                        URL api = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
                        connection.connect();

                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((inputLine = reader.readLine()) != null) {
                            stringBuilder.append(inputLine);
                        }
                        reader.close();
                        streamReader.close();
                        successful = true;

                        result = stringBuilder.toString();

                        JSONObject dailyDataJsonObj = new JSONObject(result);

                        final JSONObject timeSeriesDailyJsonObj = dailyDataJsonObj.getJSONObject("Weekly Time Series");

                        Iterator<String> iterator = timeSeriesDailyJsonObj.keys();

                        xAxisValuesWeek = new ArrayList<String>();
                        entries = new ArrayList<>();
                        mChart.resetTracking();
                        ind = 0;
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            xAxisValuesWeek.add(key);
                            if (key != null) {
                                JSONObject finalObj = timeSeriesDailyJsonObj.optJSONObject(key);
                                closeRate = Float.parseFloat(finalObj.optString("4. close"));
                                openRate = Float.parseFloat(finalObj.optString("1. open"));
                                highRate = Float.parseFloat(finalObj.optString("2. high"));
                                lowRate = Float.parseFloat(finalObj.optString("3. low"));
                                entries.add(new CandleEntry(ind, highRate,lowRate,openRate,closeRate,getResources().getDrawable(R.drawable.ic_launcher_background)));
                                ind  = ind+1;
                                if(ind == 30) break;
                            }
                        }

                        XAxis xAxis = mChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        YAxis leftAxis = mChart.getAxisLeft();
                        leftAxis.setDrawGridLines(false);
                        leftAxis.setDrawAxisLine(false);
                        //Collections.reverse(xAxisValuesWeek);
                        xAxis.setAxisMaximum(xAxisValuesWeek.size());
                        mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValuesWeek));
                        set1 = new CandleDataSet(entries, "Weekly");
                        set1.setDrawIcons(false);
                        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set1.setShadowColor(Color.DKGRAY);
                        set1.setShadowWidth(0.7f);
                        set1.setDecreasingColor(Color.RED);
                        set1.setDecreasingPaintStyle(Paint.Style.FILL);
                        set1.setIncreasingColor(Color.parseColor("#59c639"));
                        set1.setIncreasingPaintStyle(Paint.Style.FILL);
                        set1.setNeutralColor(Color.BLUE);
                        set1.setDrawValues(false);

                        CandleData data = new CandleData(set1);

                        mChart.setData(data);
                        mChart.getDescription().setText("");
                        mChart.getDescription().setTextColor(Color.BLUE);
                        mChart.animateY(1400, Easing.EaseInOutBounce);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Launch buy.
     *
     * @param v the v
     */
    public void launchBuy (View v){
        Intent i = new Intent(this, BuyDialog.class);
        i.putExtra("symbol", symbol);
        i.putExtra("watchList",extras.getStringArrayList("watchList"));
        i.putExtra("watchListPrices",extras.getStringArrayList("watchListPrices"));
        startActivityForResult(i, 5);
    }

    /**
     * Launch sell.
     *
     * @param v the v
     */
    public void launchSell (View v){
        Intent i = new Intent(this, SellActivity.class);
        i.putExtra("symbol", symbol);
        i.putExtra("watchList",extras.getStringArrayList("watchList"));
        i.putExtra("watchListPrices",extras.getStringArrayList("watchListPrices"));
        startActivityForResult(i, 5);
    }

}