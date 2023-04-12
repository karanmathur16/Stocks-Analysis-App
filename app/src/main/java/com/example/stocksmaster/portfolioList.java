package com.example.stocksmaster;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * The type Portfolio list.
 */
public class portfolioList extends ArrayAdapter<String> {

    private final Activity context;
    /**
     * The Stock symbol list.
     */
    String[] stockSymbolList;
    /**
     * The Stock prices list.
     */
    String[] stockPricesList;
    /**
     * The Stock prices change percent list.
     */
    String[] stockPricesChangePercentList;
    /**
     * The Stock prices previous list.
     */
    String[] stockPricesPreviousList;
    /**
     * The Quantites.
     */
    Integer[] quantites;


    /**
     * Instantiates a new Portfolio list.
     *
     * @param context                      the context
     * @param stockSymbolList              the stock symbol list
     * @param stockPricesList              the stock prices list
     * @param stockPricesChangePercentList the stock prices change percent list
     * @param stockPricesPreviousList      the stock prices previous list
     * @param quantities                   the quantities
     */
    public portfolioList(Activity context, String[] stockSymbolList, String[] stockPricesList, String[] stockPricesChangePercentList, String[] stockPricesPreviousList, Integer[] quantities) {
        super(context, R.layout.portfolio_list,stockSymbolList);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.stockSymbolList = stockSymbolList;
        this.stockPricesList = stockPricesList;
        this.stockPricesChangePercentList = stockPricesChangePercentList;
        this.stockPricesPreviousList = stockPricesPreviousList;
        this.quantites = quantities;

    }

    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.portfolio_list, null, true);

        if(stockSymbolList.length > 0 && stockPricesList.length > 0 && stockPricesChangePercentList.length > 0) {

            TextView stockSymbolText = (TextView) rowView.findViewById(R.id.title);
            TextView quantity = (TextView) rowView.findViewById(R.id.subtitle);
            TextView stockPrice = (TextView) rowView.findViewById(R.id.stockPrices);
            TextView stockPriceChange = (TextView) rowView.findViewById(R.id.stockChangePercent);
            TextView stockPricePrevious = (TextView) rowView.findViewById(R.id.stockChangeAmount);

            Float totalValue = quantites[position] * Float.parseFloat(stockPricesList[position]);

            stockSymbolText.setText(stockSymbolList[position]);
            quantity.append(quantites[position].toString());
            stockPrice.setText(totalValue.toString());
            stockPriceChange.setText(stockPricesChangePercentList[position]);
            stockPricePrevious.setText(stockPricesPreviousList[position]);

            if(stockPricesChangePercentList[position].contains("-")) {
                stockPriceChange.setTextColor(Color.RED);
                stockPrice.setTextColor(Color.RED);
            }
            else {
                stockPriceChange.setTextColor(Color.parseColor("#59c639"));
                stockPrice.setTextColor(Color.parseColor("#59c639"));
            }

        }

        return rowView;

    }

}
