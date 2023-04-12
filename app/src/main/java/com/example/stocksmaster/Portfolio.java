package com.example.stocksmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The type Portfolio.
 */
public class Portfolio extends AppCompatActivity {

    /**
     * The Portfolio list.
     */
    ListView portfolioList;
    /**
     * The Stock symbol list.
     */
    String[]  stockSymbolList = {"AAPL","IBM","TSCDF","TCEHY"};
    /**
     * The Stock prices list.
     */
    String[]  stockPricesList ={"138.3800","136.9600","2.4900","28.7300"};
    /**
     * The Stock prices change percent list.
     */
    String[]  stockPricesChangePercentList = {"-3.60%","18.51%","2.892%","1.83%"};
    /**
     * The Stock prices previous list.
     */
    String[]  stockPricesPreviousList = {"-1,131.25","2,513.48","328.10","2,600.00"};
    /**
     * The Quantities.
     */
    Integer[] quantities = {100,200,500,700};

    /**
     * The Sharedpreferences.
     */
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio2);

        sharedpreferences = getSharedPreferences("Myquants", Context.MODE_PRIVATE);
        String quantString = sharedpreferences.getString("quantities",null);
        if (quantString != null && quantString != "") {
            String quantStringArr[] = quantString.split(",");
            for(int i = 0 ; i<quantities.length;i++){
                quantities[i] = Integer.parseInt(quantStringArr[i]);
            }
        }

        Bundle extras = getIntent().getExtras();
        if (extras.getString("symbol") != null) {
            String tempsymbol = extras.getString("symbol");
            String tempquant = extras.getString("quantity");
            Boolean sell = extras.getBoolean("sell");
            Integer intquant = Integer.parseInt(tempquant);

            if(sell){
                for (int i = 0; i < stockSymbolList.length; i++) {
                    if (Objects.equals(stockSymbolList[i], tempsymbol)) {
                        if(intquant > quantities[i]){
                            Toast.makeText(this, "You cannot sell more than the available quantity!", Toast.LENGTH_LONG).show();
                            (new Handler())
                                    .postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    finish();
                                                }
                                            }, 100);
                        }
                        else quantities[i] = quantities[i] - intquant;
                    }
                }
            }
            else {
                for (int i = 0; i < stockSymbolList.length; i++) {
                    if (Objects.equals(stockSymbolList[i], tempsymbol)) {
                        quantities[i] = quantities[i] + intquant;
                    }
                }
            }
            SharedPreferences.Editor editor = sharedpreferences.edit();
            String[] strArray = new String[quantities.length];
            for (int i = 0; i < quantities.length; i++) {
                strArray[i] = String.valueOf(quantities[i]);
            }
            String listString = String.join(",",strArray);
            editor.putString("quantities", listString);
            editor.apply();
        }

        Intent main = new Intent(this,MainActivity.class);
        main.putExtra("watchList",extras.getStringArrayList("watchList"));
        main.putExtra("watchListPrices",extras.getStringArrayList("watchListPrices"));

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.portfolio);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.portfolio:
                        return true;
                    case R.id.dashboard:
                        startActivityForResult(main,5);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        portfolioList=(ListView)findViewById(R.id.portfolioList);
        portfolioList adapter=new portfolioList(this, stockSymbolList, stockPricesList,stockPricesChangePercentList,stockPricesPreviousList,quantities);
        portfolioList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}