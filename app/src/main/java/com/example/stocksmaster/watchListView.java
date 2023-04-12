package com.example.stocksmaster;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * The type Watch list view.
 */
public class watchListView extends ArrayAdapter<String> {

    private final Activity context;
    private View.OnClickListener callback;
    /**
     * The D button.
     */
    deleteButton dButton;
    /**
     * The Stock symbol list.
     */
    ArrayList<String>  stockSymbolList = new ArrayList<String>();
    /**
     * The Stock names list.
     */
    ArrayList<String> stockNamesList = new ArrayList<String>();
    /**
     * The Stock prices list.
     */
    ArrayList<String>  stockPricesList =new ArrayList<String>();
    /**
     * The Stock prices change percent list.
     */
    ArrayList<String>  stockPricesChangePercentList = new ArrayList<String>();
    /**
     * The Stock prices previous list.
     */
    ArrayList<String>  stockPricesPreviousList = new ArrayList<String>();

    /**
     * Instantiates a new Watch list view.
     *
     * @param context         the context
     * @param stockSymbolName the stock symbol name
     * @param stockPrices     the stock prices
     */
    public watchListView(Activity context, String[] stockSymbolName, String[] stockPrices) {
        super(context, R.layout.watch_list, stockSymbolName);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.dButton = (MainActivity) context;
        splitSymbolName(stockSymbolName);
        splitStockPrices(stockPrices);
    }

    private void splitSymbolName(String[] list){
        if(list.length > 0) {
            for (int i = 0; i < list.length; i++) {
                String[] tempArr = list[i].split(":");
                stockNamesList.add(tempArr[0]);
                stockSymbolList.add(tempArr[1]);
            }
        }
    }

    private void splitStockPrices(String[] list){
        if(list.length > 0) {
            for (int i = 0; i < list.length; i++) {
                String[] tempArr = list[i].split("&");
                stockPricesList.add(tempArr[0]);
                stockPricesChangePercentList.add(tempArr[2]);
                stockPricesPreviousList.add(tempArr[1]);
            }
        }
    }

    public View getView(int position,View view,ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.watch_list, null, true);

        if(stockSymbolList.size() > 0 && stockPricesList.size() > 0 && stockPricesChangePercentList.size() > 0 && stockNamesList.size() > 0) {

            TextView stockSymbolText = (TextView) rowView.findViewById(R.id.title);
            TextView stockNameText = (TextView) rowView.findViewById(R.id.subtitle);
            TextView stockPrice = (TextView) rowView.findViewById(R.id.stockPrices);
            TextView stockPriceChange = (TextView) rowView.findViewById(R.id.stockPricesChange);
            TextView stockPricePrevious = (TextView) rowView.findViewById(R.id.stockPricesPrevious);


            String[] stockNames = new String[stockNamesList.size()];
            stockNames = stockNamesList.toArray(stockNames);
            String[] stockSymbol = new String[stockSymbolList.size()];
            stockSymbol = stockSymbolList.toArray(stockSymbol);
            String[] stockPrices = new String[stockPricesList.size()];
            stockPrices = stockPricesList.toArray(stockPrices);
            String[] stockPricesChangePercent = new String[stockPricesChangePercentList.size()];
            stockPricesChangePercent = stockPricesChangePercentList.toArray(stockPricesChangePercent);
            String[] stockPricesPrevious = new String[stockPricesPreviousList.size()];
            stockPricesPrevious = stockPricesPreviousList.toArray(stockPricesPrevious);

            stockSymbolText.setText(stockSymbol[position]);
            stockPrice.setText(stockPrices[position]);
            stockPriceChange.setText("("+ stockPricesChangePercent[position] + ")");
            stockNameText.setText(stockNames[position]);
            stockPricePrevious.setText(stockPricesPrevious[position]);

            if(stockPricesChangePercent[position].contains("-")) {
                stockPriceChange.setTextColor(Color.RED);
                stockPrice.setTextColor(Color.RED);
            }
            else {
                stockPriceChange.setTextColor(Color.parseColor("#59c639"));
                stockPrice.setTextColor(Color.parseColor("#59c639"));
            }

            ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dButton.onButtonClick(position);
                        }
                    }
            );
        }


        return rowView;

    }

}
