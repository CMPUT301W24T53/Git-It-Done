package com.example.gidevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView checkInQRCodeImageView, eventQRCodeImageView;
    private Bitmap checkInQRCodeBitmap, eventQRCodeBitmap;

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
            checkInQRCodeBitmap = QRCodeBitmap.generateQRCodeBitmap(checkInQRCodeContent);
            checkInQRCodeImageView.setImageBitmap(checkInQRCodeBitmap);
        } else {
            checkInQRCodeImageView.setImageResource(R.drawable.my_event_icon);
        }

        if (eventQRCodeContent != null && !eventQRCodeContent.isEmpty()) {
            eventQRCodeBitmap = QRCodeBitmap.generateQRCodeBitmap(eventQRCodeContent);
            eventQRCodeImageView.setImageBitmap(eventQRCodeBitmap);
        } else {
            eventQRCodeImageView.setImageResource(R.drawable.my_event_icon);
        }

        Button shareCheckInQRCodeButton = findViewById(R.id.shareCheckInQRCodeButton);
        shareCheckInQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCheckInQRCode(v);
            }
        });

        Button shareEventQRCodeButton = findViewById(R.id.shareEventQRCodeButton);
        shareEventQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEventQRCode(v);
            }
        });
    }

    public void shareCheckInQRCode(View view) {
        shareQRCode(checkInQRCodeBitmap, "check_in_qr_code.png");
    }

    public void shareEventQRCode(View view) {
        shareQRCode(eventQRCodeBitmap, "event_qr_code.png");
    }

    private void shareQRCode(Bitmap qrCodeBitmap, String fileName) {
        // Save the bitmap to a file
        File file = new File(getExternalCacheDir(), fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a URI for the file
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

        // Create an intent for sharing the QR code image
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start the share intent
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }
}