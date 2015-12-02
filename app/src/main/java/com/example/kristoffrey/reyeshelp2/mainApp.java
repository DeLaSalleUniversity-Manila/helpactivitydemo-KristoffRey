package com.example.kristoffrey.reyeshelp2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class mainApp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        solver();
    }

    private void solver() {
        Button solveIt = (Button)findViewById(R.id.buttonSolve);
        solveIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num1, num2;
                int x,y,ans;
                EditText firstNumber = (EditText)findViewById(R.id.num1);
                EditText secondNumber = (EditText)findViewById(R.id.num2);
                num1 = firstNumber.getText().toString();
                num2 = secondNumber.getText().toString();
                x = Integer.parseInt(num1);
                y = Integer.parseInt(num2);
                RadioButton add = (RadioButton)findViewById(R.id.radioAdd);
                RadioButton subtract = (RadioButton)findViewById(R.id.radioSubtract);
                RadioButton multiply = (RadioButton)findViewById(R.id.radioMultiply);
                RadioButton divide = (RadioButton)findViewById(R.id.radioDivide);

                add.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                    }
                });

                subtract.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                    }
                });

                multiply.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                    }
                });

                divide.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                    }
                });

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
