package com.example.gidevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView checkInQRCodeImageView, eventQRCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        checkInQRCodeImageView = findViewById(R.id.checkInQRCodeImageView);
        eventQRCodeImageView = findViewById(R.id.eventQRCodeImageView);

        Intent intent = getIntent();
        String checkInQRCodeContent = intent.getStringExtra("checkInQRCodeContent");
        String eventQRCodeContent = intent.getStringExtra("eventQRCodeContent");

        if (checkInQRCodeContent != null && !checkInQRCodeContent.isEmpty()) {
            Bitmap checkInQRCodeBitmap = generateQRCodeBitmap(checkInQRCodeContent);
            checkInQRCodeImageView.setImageBitmap(checkInQRCodeBitmap);
        } else {
            checkInQRCodeImageView.setImageResource(R.drawable.my_event_icon);
        }

        if (eventQRCodeContent != null && !eventQRCodeContent.isEmpty()) {
            Bitmap eventQRCodeBitmap = generateQRCodeBitmap(eventQRCodeContent);
            eventQRCodeImageView.setImageBitmap(eventQRCodeBitmap);
        } else {
            eventQRCodeImageView.setImageResource(R.drawable.my_event_icon);
        }
    }

    private Bitmap generateQRCodeBitmap(String content) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}