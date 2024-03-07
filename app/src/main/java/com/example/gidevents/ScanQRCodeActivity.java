package com.example.gidevents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScanQRCodeActivity extends AppCompatActivity  {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        if (hasCameraPermission()) {
            // If permission is already granted, start the scan
            ScanQRCode();
        } else {
            // Else request permission
            requestCameraPermission();
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, continue with camera-related tasks
                ScanQRCode();
            } else {
                // Permission was denied, handle the case where the user denies permission
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void ScanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            String scannedData = result.getContents();
            if (scannedData != null) {
                // Check if the QRCode matches any QR code's data present in firebase.
                checkQRCode(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkQRCode(String scannedData){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("qrcodes");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isCodeValid = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String validCode = snapshot.getValue(String.class);
                    if (scannedData.equals(validCode)) {
                        isCodeValid = true;
                        break;
                    }
                }
                if (isCodeValid) {
                    // The QR code is valid, jump to my activity page and pass the QR code data
                    Intent myEventIntent = new Intent(ScanQRCodeActivity.this, MyEventActivity.class);
                    myEventIntent.putExtra("qrcode_data", scannedData);
                    startActivity(myEventIntent);
                } else {
                    //The QR code is invalid and an error message is displayed.
                    Toast.makeText(ScanQRCodeActivity.this, "Invalid QR code", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ScanQRCodeActivity.this, "DataBase Error:" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


