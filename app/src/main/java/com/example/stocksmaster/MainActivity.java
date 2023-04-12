package com.example.stocksmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements deleteButton {

    /**
     * The Rl.
     */
    RelativeLayout rl ;
    /**
     * The Stock names.
     */
    ArrayList<String> stockNames = new ArrayList<String>();
    /**
     * The Watch list.
     */
    ArrayList<String> watchList = new ArrayList<String>();
    /**
     * The Watch list prices.
     */
    ArrayList<String> watchListPrices = new ArrayList<String>();
    /**
     * The Watch list adapter.
     */
    watchListView watchListAdapter;
    /**
     * The Watch list view 1.
     */
    watchListView watchListView1;
    private ListView myListView;
    private Button mButton;

    private static final int TRIGGER_AUTO_COMPLETE = 400;
    private static final long AUTO_COMPLETE_DELAY = 800;
    private Handler handler;
    private autoSuggestAdapter autoSuggestAdapter;

    /**
     * The Base url.
     */
    String baseUrl = "https://www.alphavantage.co/query?function=";
    /**
     * The Url.
     */
    String url;
    /**
     * The Url prices.
     */
    String urlPrices;
    /**
     * The Apikey.
     */
    String apikey = "WOC6ZP683U6ZI228";
    /**
     * The Prices.
     */
    String prices;
    /**
     * The Counter.
     */
    Integer counter = 0;
    /**
     * The Counter response.
     */
    Integer counterResponse = 0;

    /**
     * The Request queue.
     */
    RequestQueue requestQueue;
    private static final int request_code = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);

        requestQueue = Volley.newRequestQueue(this);

        autoSuggestAdapter = new autoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        rl = (RelativeLayout) findViewById(R.id.containerMain);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);
                        addListItem(autoSuggestAdapter.getObject(position));
                        autoCompleteTextView.setText("");
                    }
                });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if ((!TextUtils.isEmpty(autoCompleteTextView.getText())) && (autoCompleteTextView.length() >= 3)) {
                        getStocksName(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        myListView = (ListView) findViewById(R.id.watchList);
        TextView textView = new TextView(this);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textView.setText("Your Watch List : \n");
        myListView.addHeaderView(textView);

        if(savedInstanceState == null && extras.getStringArrayList("watchList") == null && extras.getStringArrayList("watchListPrices") == null) {
            getFavourites(new Favourites() {
                @Override
                public void onCompletion() {

                    String[] watchListNameSymbol = new String[watchList.size()];
                    watchListNameSymbol = watchList.toArray(watchListNameSymbol);

                    String[] watchListPrices1 = new String[watchListPrices.size()];
                    watchListPrices1 = watchListPrices.toArray(watchListPrices1);

                    Log.e("watchListPrices1", watchListPrices.toString());

                    watchListAdapter = new watchListView(MainActivity.this,
                            watchListNameSymbol,
                            watchListPrices1);
                    myListView.setAdapter(watchListAdapter);
                    watchListAdapter.notifyDataSetChanged();
                }
            });

        }

        else if(extras != null){
            watchList = extras.getStringArrayList("watchList");
            watchListPrices = extras.getStringArrayList("watchListPrices");

            String[] watchListNameSymbol = new String[watchList.size()];
            watchListNameSymbol = watchList.toArray(watchListNameSymbol);

            String[] watchListPrices1 = new String[watchListPrices.size()];
            watchListPrices1 = watchListPrices.toArray(watchListPrices1);

            watchListAdapter = new watchListView(MainActivity.this,
                    watchListNameSymbol,
                    watchListPrices1);
            myListView.setAdapter(watchListAdapter);
            watchListAdapter.notifyDataSetChanged();

        }

        else if(savedInstanceState != null){
            watchList = savedInstanceState.getStringArrayList("watchList");
            watchListPrices = savedInstanceState.getStringArrayList("watchListPrices");

            String[] watchListNameSymbol = new String[watchList.size()];
            watchListNameSymbol = watchList.toArray(watchListNameSymbol);

            String[] watchListPrices1 = new String[watchListPrices.size()];
            watchListPrices1 = watchListPrices.toArray(watchListPrices1);

            watchListAdapter = new watchListView(MainActivity.this,
                    watchListNameSymbol,
                    watchListPrices1);
            myListView.setAdapter(watchListAdapter);
            watchListAdapter.notifyDataSetChanged();

        }

        Intent i = new Intent(this, StockAnalytics.class);
        i.putExtra("watchList", watchList);
        i.putExtra("watchListPrices", watchListPrices);
        Intent iPortfolio = new Intent(this, Portfolio.class);
        iPortfolio.putExtra("watchList", watchList);
        iPortfolio.putExtra("watchListPrices", watchListPrices);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempName = watchList.get(position-1);
                String [] tempArr = tempName.split(":");
                String symbol = tempArr[1].trim();
                i.putExtra("symbol", symbol);
                startActivityForResult(i, request_code);
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.dashboard:
                        return true;
                    case R.id.portfolio:
                        startActivityForResult(iPortfolio,request_code);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

}

    /**
     * The Undo on click listener.
     */
    View.OnClickListener undoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            watchList.remove(watchList.size() -1);
            watchListAdapter.notifyDataSetChanged();

        }
    };


    /**
     * On click.
     *
     * @param view the view
     */
    public void onClick ( View view ) {
        // view is the row view returned by getView
        // The position is stored as tag, so it can be retrieved using getTag ()
        CharSequence text = "Item clicked : " + view.getTag () ;
    }

    //Override the interface method
    @Override
    public void onButtonClick(int position){
        deleteitems(position);
    }

    /**
     * Add items to favourites list
     * @param item
     */

    private void addListItem(String item) {
        watchList.add(item);
        String[] temparr = (item.split(":"));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getStocksPrices(temparr[1].trim(), new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        watchListPrices.add(prices);
                        String[] watchListNameSymbol = new String[watchList.size()];
                        watchListNameSymbol = watchList.toArray(watchListNameSymbol);

                        String[] watchListPrices1 = new String[watchListPrices.size()];
                        watchListPrices1 = watchListPrices.toArray(watchListPrices1);

                        watchListAdapter = new watchListView(MainActivity.this,
                                watchListNameSymbol,
                                watchListPrices1);
                        myListView.setAdapter(watchListAdapter);
                        watchListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        saveFavourites();
    }

    /**
     * Auto-Complete Search API call
     * @param searchTerm
     */

    private void getStocksName(String searchTerm) {
        this.url = this.baseUrl + "SYMBOL_SEARCH&keywords=" + searchTerm + "&apikey=" + apikey;
        Log.e("URL Volley", this.url);
        JsonObjectRequest arrReq = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                List<String> stringList = new ArrayList<>();
                try {
                    JSONArray array = response.getJSONArray("bestMatches");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        if(row.getString("4. region").equals("United States")){
                            stringList.add(row.getString("2. name") + " : " + (row.getString("1. symbol")));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addToSearchBox(stringList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stockNames.clear();
                stockNames.add("Error while calling REST API");
                Log.e("Volley", error.toString());
            }
        });
        requestQueue.add(arrReq);
    }

    private void addToSearchBox(List<String> list) {
         autoSuggestAdapter.setData(list);
         autoSuggestAdapter.notifyDataSetChanged();
    }

    /**
     * Save favourites.
     */
    public void saveFavourites(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        String listString = String.join(",",watchList);
        Log.e("watchList:", String.valueOf(watchList));
        editor.putString(getString(R.string.favourites), listString);
        editor.apply();
    }

    /**
     * Get favourites from sharesPreferences and get prices
     * @param callBack
     */

    private void getFavourites(final Favourites callBack){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String watchListString = sharedPref.getString(getString(R.string.favourites),null);
        if (watchListString != null && watchListString != "") {
            String watchListArr[] = watchListString.split(",");
            watchList.addAll(Arrays.asList(watchListArr));
            Thread thread = new Thread(new Runnable() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void run() {
                    counter = 0;
                    for (int i = 0; i < watchList.size(); i++) {
                        String[] temparr = (watchList.get(i)).split(":");
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getStocksPrices(temparr[1].trim(), new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                watchListPrices.add(prices);
                                counter = counter + 1;
                                if (counter == watchList.size()) {
                                    callBack.onCompletion();
                                }
                            }
                        });
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
    }

    /**
     * Deleteitems.
     *
     * @param position the position
     */
    public void deleteitems(int position){
        watchList.remove(position);
        watchListPrices.remove(position);
        String[] watchListNameSymbol = new String[watchList.size()];
        watchListNameSymbol = watchList.toArray(watchListNameSymbol);

        String[] watchListPrices1 = new String[watchListPrices.size()];
        watchListPrices1 = watchListPrices.toArray(watchListPrices1);

        watchListAdapter = new watchListView(MainActivity.this,
                watchListNameSymbol,
                watchListPrices1);
        myListView.setAdapter(watchListAdapter);
        watchListAdapter.notifyDataSetChanged();
        saveFavourites();
    }

    /**
     * Get stock prices API call
     * @param symbol
     * @param callBack
     */

    private void getStocksPrices(String symbol,final VolleyCallBack callBack) {
        this.urlPrices = this.baseUrl + "GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apikey;
        JsonObjectRequest arrReq = new JsonObjectRequest (Request.Method.GET, urlPrices, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject details = response.getJSONObject("Global Quote");
                    prices = details.getString("05. price") + "&" + details.getString("08. previous close") + "&" + details.getString("10. change percent");
                    callBack.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        requestQueue.add(arrReq);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putStringArrayList("watchList",watchList);
        outState.putStringArrayList("watchListPrices",watchListPrices);

    }

    /**
     * Refresh list.
     *
     * @param v the v
     */
    public void refreshList(View v)
    {
        Toast.makeText(this, "Refreshing, Please Wait...", Toast.LENGTH_SHORT).show();
        (new Handler())
                .postDelayed(
                        new Runnable() {
                            public void run() {
                               watchList.clear();
                               watchListPrices.clear();
                                getFavourites(new Favourites() {
                                    @Override
                                    public void onCompletion() {

                                        String[] watchListNameSymbol = new String[watchList.size()];
                                        watchListNameSymbol = watchList.toArray(watchListNameSymbol);

                                        String[] watchListPrices1 = new String[watchListPrices.size()];
                                        watchListPrices1 = watchListPrices.toArray(watchListPrices1);

                                        Log.e("watchListPrices1", watchListPrices.toString());

                                        watchListAdapter = new watchListView(MainActivity.this,
                                                watchListNameSymbol,
                                                watchListPrices1);
                                        myListView.setAdapter(watchListAdapter);
                                        watchListAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }, 100);

    }
}