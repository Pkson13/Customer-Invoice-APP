package com.example.customersystem;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;


public class MainActivity extends AppCompatActivity {
    EditText nameInput, phoneInput, emailInput, productName, quantity, price;
    TextView invoiceOutput;
    Button calculateBtn, loadDataBtn;
    TableLayout tableLayout;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        SwitchCompat themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        productName = findViewById(R.id.productName);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        calculateBtn = findViewById(R.id.calculateBtn);
        invoiceOutput = findViewById(R.id.invoiceOutput);
         loadDataBtn = findViewById(R.id.loadDataBtn);
        tableLayout= findViewById(R.id.tableLayout);



        calculateBtn.setOnClickListener(v -> calculateInvoice());
        loadDataBtn.setOnClickListener(v -> loadCustomerTable());

        Button savePdfBtn = findViewById(R.id.savePdfBtn);
        savePdfBtn.setOnClickListener(v -> {
            String content = invoiceOutput.getText().toString();
            if (!content.isEmpty()) {
                saveInvoiceToPDF(content);
            } else {
                Toast.makeText(this, "Calculate invoice first", Toast.LENGTH_SHORT).show();
            }
        });


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

    private void saveInvoiceToPDF(String content) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);

        int y = 25;
        for (String line : content.split("\n")) {
            canvas.drawText(line, 10, y, paint);
            y += 20;
        }

        document.finishPage(page);

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Invoices");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "invoice_" + System.currentTimeMillis() + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Invoice saved to " + file, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
//            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
        document.close();
        invoiceOutput.setText("");
    }


    private void loadCustomerTable() {
        tableLayout.removeAllViews(); // Clear old data
        Cursor cursor = db.getAllCustomers();

        if (cursor.getCount() > 0) {
            // Add table header
            TableRow header = new TableRow(this);
            header.addView(createCell("ID"));
            header.addView(createCell("Name"));
            header.addView(createCell("Phone"));
            header.addView(createCell("Email"));
            tableLayout.addView(header);

            while (cursor.moveToNext()) {
                TableRow row = new TableRow(this);
                row.addView(createCell(cursor.getString(0))); // ID
                row.addView(createCell(cursor.getString(1))); // Name
                row.addView(createCell(cursor.getString(2))); // Phone
                row.addView(createCell(cursor.getString(3))); // Email
                tableLayout.addView(row);
            }
        } else {
            Toast.makeText(this, "No customer records", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }


}
