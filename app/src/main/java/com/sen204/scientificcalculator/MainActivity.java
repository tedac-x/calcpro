package com.sen204.calcpro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    EditText etNumber1, etNumber2;
    TextView tvResult;
    Button btnAdd, btnSubtract, btnMultiply, btnDivide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNumber1 = findViewById(R.id.etNumber1);
        etNumber2 = findViewById(R.id.etNumber2);
        tvResult = findViewById(R.id.tvResult);

        btnAdd = findViewById(R.id.btnAdd);
        btnSubtract = findViewById(R.id.btnSubtract);
        btnMultiply = findViewById(R.id.btnMultiply);
        btnDivide = findViewById(R.id.btnDivide);

        btnAdd.setOnClickListener(v -> calculate('+'));
        btnSubtract.setOnClickListener(v -> calculate('-'));
        btnMultiply.setOnClickListener(v -> calculate('*'));
        btnDivide.setOnClickListener(v -> calculate('/'));
    }

    private void calculate(char operator) {
        double num1, num2, result = 0;
        try {
            num1 = Double.parseDouble(etNumber1.getText().toString());
            num2 = Double.parseDouble(etNumber2.getText().toString());

            switch (operator) {
                case '+': result = num1 + num2; break;
                case '-': result = num1 - num2; break;
                case '*': result = num1 * num2; break;
                case '/':
                    if (num2 != 0) result = num1 / num2;
                    else {
                        tvResult.setText("Error: Division by zero");
                        return;
                    }
                    break;
            }
            tvResult.setText("Result: " + result);
        } catch (Exception e) {
            tvResult.setText("Please enter valid numbers");
        }
    }
}
