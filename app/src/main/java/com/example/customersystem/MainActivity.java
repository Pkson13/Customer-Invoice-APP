package com.example.customersystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    EditText nameInput, phoneInput, emailInput, productName, quantity, price;
    TextView invoiceOutput;
    Button calculateBtn;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        productName = findViewById(R.id.productName);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        calculateBtn = findViewById(R.id.calculateBtn);
        invoiceOutput = findViewById(R.id.invoiceOutput);

        calculateBtn.setOnClickListener(v -> calculateInvoice());
    }

    private void calculateInvoice() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();
        String product = productName.getText().toString();
        int qty = Integer.parseInt(quantity.getText().toString());
        double unitPrice = Double.parseDouble(price.getText().toString());

        db.insertCustomer(name, phone, email);

        double subtotal = qty * unitPrice;
        double tax = subtotal * 0.1;
        double discount = subtotal * 0.05;
        double total = subtotal + tax - discount;

        String invoice = "Customer: " + name + "\n"
                + "Product: " + product + "\n"
                + "Qty: " + qty + ", Price: " + unitPrice + "\n"
                + "Subtotal: " + subtotal + "\n"
                + "Tax (10%): " + tax + "\n"
                + "Discount (5%): " + discount + "\n"
                + "Total: " + total;

        invoiceOutput.setText(invoice);
    }

}
