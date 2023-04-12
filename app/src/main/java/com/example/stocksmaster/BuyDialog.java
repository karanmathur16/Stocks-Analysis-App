package com.example.stocksmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * The type Buy dialog.
 */
public class BuyDialog extends Activity {

    /**
     * The Text view.
     */
    TextView textView;
    /**
     * The Quantity.
     */
    String quantity;
    /**
     * The Symbol.
     */
    String symbol = null;
    /**
     * The Extras.
     */
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_dialog);

        textView = findViewById(R.id.editTextNumber);
         extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        symbol = extras.getString("symbol");

    }

    /**
     * On buy.
     *
     * @param v the v
     */
    public void onBuy(View v){
        Intent i = new Intent(this, Portfolio.class);
        quantity = (String) textView.getText().toString();
        i.putExtra("symbol", symbol);
        i.putExtra("quantity", quantity);
        i.putExtra("watchList",extras.getStringArrayList("watchList"));
        i.putExtra("watchListPrices",extras.getStringArrayList("watchListPrices"));
        startActivityForResult(i, 5);
    }
}