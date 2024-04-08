package com.example.gidevents;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * QRCodeBitmap class provides a utility method to generate QR code bitmaps.
 * It uses the ZXing library to encode a given content into a QR code bitmap.
 */
public class QRCodeBitmap {
    /**
     * Generates a QR code bitmap from the given content.
     *
     * @param content The content to be encoded in the QR code.
     * @return The generated QR code bitmap, or null if an error occurs.
     */
    public static Bitmap generateQRCodeBitmap(String content) {
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