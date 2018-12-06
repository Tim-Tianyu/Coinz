package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

public class BankActivity extends AppCompatActivity {
    private Button transferGold, transferCurrency;
    private TextView valueGold, valueDolr, valuePenny, valueQuid, valueShil,
            titleGold, titleDolr, titlePenny, titleQuid, titleShil;
    private SeekBar seekGold, seekDolr, seekPenny, seekQuid, seekShil;
    private RadioButton radioDolr, radioPenny, radioQuid, radioShil;
    private Bank bank = Bank.theBank;
    private static final String TAG = "BANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        bindComponents();
        refreshLabels();
        bindEvents();

    }

    private void bindComponents(){
        transferGold = findViewById(R.id.activity_bank_btn_transfer_gold);
        transferCurrency = findViewById(R.id.activity_bank_btn_transfer_currency);
        valueDolr = findViewById(R.id.activity_bank_txt_dolr_value);
        valuePenny = findViewById(R.id.activity_bank_txt_penny_value);
        valueQuid = findViewById(R.id.activity_bank_txt_quid_value);
        valueShil = findViewById(R.id.activity_bank_txt_shil_value);
        valueGold = findViewById(R.id.activity_bank_txt_gold_value);
        titleGold = findViewById(R.id.activity_bank_txt_gold_title);
        titleDolr = findViewById(R.id.activity_bank_txt_dolr_title);
        titlePenny = findViewById(R.id.activity_bank_txt_penny_title);
        titleQuid = findViewById(R.id.activity_bank_txt_quid_title);
        titleShil = findViewById(R.id.activity_bank_txt_shil_title);
        seekGold = findViewById(R.id.activity_bank_sb_gold);
        seekDolr = findViewById(R.id.activity_bank_sb_dolr);
        seekPenny = findViewById(R.id.activity_bank_sb_penny);
        seekQuid = findViewById(R.id.activity_bank_sb_quid);
        seekShil = findViewById(R.id.activity_bank_sb_shil);
        radioDolr =findViewById(R.id.activity_bank_rb_dolr);
        radioPenny = findViewById(R.id.activity_bank_rb_penny);
        radioQuid = findViewById(R.id.activity_bank_rb_quid);
        radioShil = findViewById(R.id.activity_bank_rb_shil);
    }

    private void refreshLabels(){
        Map<Coin.Currency, Double> values = bank.getValues();
        valueDolr.setText(String.format(Locale.UK,"%1$.2f" ,values.get(Coin.Currency.DOLR)));
        valuePenny.setText(String.format(Locale.UK,"%1$.2f" ,values.get(Coin.Currency.PENY)));
        valueQuid.setText(String.format(Locale.UK,"%1$.2f" ,values.get(Coin.Currency.QUID)));
        valueShil.setText(String.format(Locale.UK,"%1$.2f" ,values.get(Coin.Currency.SHIL)));
        valueGold.setText(String.format(Locale.UK,"%1$.2f" , bank.getValueGold()));
    }

    private void bindEvents(){
        seekGold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = seekBar.getMax();
                titleGold.setText(String.format(Locale.UK,"Gold: %1$.2f percent" ,  100.0 * progress / max));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekDolr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = seekBar.getMax();
                titleDolr.setText(String.format(Locale.UK,"Dolr: %1$.2f percent" ,  100.0 * progress / max));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekPenny.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = seekBar.getMax();
                titlePenny.setText(String.format(Locale.UK,"Penny: %1$.2f percent" ,  100.0 * progress / max));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekQuid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = seekBar.getMax();
                titleQuid.setText(String.format(Locale.UK,"Quid: %1$.2f percent" ,  100.0 * progress / max));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekShil.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = seekBar.getMax();
                titleShil.setText(String.format(Locale.UK,"Shil: %1$.2f percent" ,  100.0 * progress / max));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        transferGold.setOnClickListener(v -> {
            Coin.Currency currency;
            if (radioDolr.isChecked()) {
                currency = Coin.Currency.DOLR;
            } else if (radioPenny.isChecked()){
                currency = Coin.Currency.PENY;
            } else if (radioQuid.isChecked()){
                currency = Coin.Currency.QUID;
            } else if (radioShil.isChecked()){
                currency = Coin.Currency.SHIL;
            } else{
                Toast.makeText(BankActivity.this,"Please select a currency", Toast.LENGTH_SHORT).show();
                return;
            }
            Double percentage = 1.0 * seekGold.getProgress() / seekGold.getMax();
            if (bank.exchangeGoldToCurrency(percentage * bank.getValueGold(),currency)){
                seekGold.setProgress(0);
                Toast.makeText(BankActivity.this,"Tansfer success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BankActivity.this,"Tansfer fail", Toast.LENGTH_SHORT).show();
            }
            refreshLabels();
        });

        transferCurrency.setOnClickListener(v -> {
            Double percentageDolr, percentagePenny, percentageQuid, percentageShil;
            percentageDolr = 1.0 * seekDolr.getProgress() / seekDolr.getMax();
            percentagePenny = 1.0 * seekPenny.getProgress() / seekPenny.getMax();
            percentageQuid = 1.0 * seekQuid.getProgress() / seekQuid.getMax();
            percentageShil = 1.0 * seekShil.getProgress() / seekShil.getMax();
            Map<Coin.Currency, Double> values = bank.getValues();
            try {
                if (bank.exchangeCurrenciesToGold(
                        percentageDolr * values.get(Coin.Currency.DOLR),
                        percentagePenny * values.get(Coin.Currency.PENY),
                        percentageShil * values.get(Coin.Currency.SHIL),
                        percentageQuid * values.get(Coin.Currency.QUID)))
                {
                    seekDolr.setProgress(0);
                    seekPenny.setProgress(0);
                    seekQuid.setProgress(0);
                    seekShil.setProgress(0);
                    Toast.makeText(BankActivity.this,"Tansfer success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(BankActivity.this,"Tansfer fail", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException ex){
                Log.d(TAG,"WARN: bank.getValues is an empty map");
                //Toast.makeText(BankActivity.this,ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            refreshLabels();
        });
    }
}
