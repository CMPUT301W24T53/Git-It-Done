package com.example.gidevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OrganizerReuseQRAdapter extends ArrayAdapter<String[]> {
    private List<String[]> data;
    private Context context;
    public OrganizerReuseQRAdapter(Context context, ArrayList<String[]> options) {
        super(context, 0, (List<String[]>) options);
        data = options;
        this.context = context;
    }

    public int getCount(){
        return data.size();
    }

    @NotNull
    @Override
    public View getView(int pos, @Nullable View convertView, @NonNull ViewGroup parent){
        View item = convertView;
        if (item ==null){
            item = LayoutInflater.from(context).inflate(R.layout.qr_list_item, parent, false);

        }

        String[] currentStrings = data.get(pos);
        ImageView imageView = item.findViewById(R.id.qrImageView);
        TextView textView = item.findViewById(R.id.qrTextView);

        Bitmap qrCode = QRCodeBitmap.generateQRCodeBitmap(currentStrings[0]); // get Bitmap of QRCode image

        imageView.setImageBitmap(qrCode);
        textView.setText(currentStrings[1]);

        return item;
    }

}
