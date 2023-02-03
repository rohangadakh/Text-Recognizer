package com.rohya.docthing;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class sub_module extends AppCompatActivity {

    final String[] links = { "https://www.youtube.com/@textrecognizer?feature=shares", "https://github.com/rohangadakh/Text-Recognizer.git", "https://www.youtube.com/@textrecognizer?feature=shares" };
    ImageSlider imageSlider;
    ImageView img_ocr;
    File[] files;

    int position;

    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_module);

        imageSlider = findViewById(R.id.slider);
        img_ocr = findViewById(R.id.img_ocr);
        ListView listView = findViewById(R.id.listView);

        swipeRefreshLayout = findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                listView.setOnItemClickListener((adapterView, view, position, id) -> openFile(position));
            }
        });

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.image_2,  ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.image_3,  ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.image_1,  ScaleTypes.CENTER_INSIDE));
        imageSlider.setImageList(slideModels);

        imageSlider.setItemClickListener(position -> {
            String url = links[position];
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        img_ocr.setOnClickListener(view -> {
            Intent intent = new Intent(sub_module.this, activity_ocr.class);
            startActivity(intent);
        });

        files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/").listFiles();

        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, id) -> openFile(position));

    }

    private void openFile(int position) {
        File file = files[position];
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = getMimeType(file.getPath());
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        intent.setDataAndType(uri, type);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    public String getMimeType(String path) {
        // Get MIME type for the file
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}

