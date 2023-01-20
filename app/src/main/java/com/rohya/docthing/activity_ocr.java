package com.rohya.docthing;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class activity_ocr extends AppCompatActivity {

    ImageView img_capture;

    EditText txt_result, filename;

    // TAG
    private static final String TAG = "MAIN_TAG";

    private Uri imageUri = null;

    private TextRecognizer textRecognizer;

    FloatingActionButton f_docx, f_pdf, f_add, f_share, btn_snap, btn_load, btn_img;

    private Spinner spinner;
    String saveDOCname, selectedValue;

    String[] items = { "Item 1", "Item 2", "Item 3"};

    boolean aBoolean = true, aBoolean2 = true;
    String recognized_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        Button btn_detect = findViewById(R.id.btn_detect);
        Button btn_copy = findViewById(R.id.btn_copy);

       // img_capture = findViewById(R.id.img_capture);

        img_capture = findViewById(R.id.img_capture);

        txt_result = findViewById(R.id.text_result);
        filename = findViewById(R.id.text_filename);

        f_add = findViewById(R.id.float_add);
        f_docx = findViewById(R.id.float_docx);
        f_pdf = findViewById(R.id.float_pdf);
        f_share = findViewById(R.id.float_share);
        btn_img = findViewById(R.id.btn_img);
        btn_load = findViewById(R.id.btn_load);
        btn_snap = findViewById(R.id.btn_snap);

        spinner = findViewById(R.id.spinner);

        String[] items = getResources().getStringArray(R.array.list);
        ArrayAdapter adapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,items);
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = parent.getItemAtPosition(position).toString();
                if(selectedValue.equals("Devanagari"))
                {
                    textRecognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                } else if (selectedValue.equals("Default"))
                {
                    textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                } else if (selectedValue.equals("Chinese"))
                {
                    textRecognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                } else if (selectedValue.equals(("Japanese")))
                {
                    textRecognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                } else if (selectedValue.equals("Korean"))
                {
                    textRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        f_add.setOnClickListener(view -> {
            if(aBoolean){
                f_docx.show();
                f_pdf.show();
                f_share.show();
                aBoolean = false;
            }
            else
            {
                f_docx.hide();
                f_pdf.hide();
                f_share.hide();
                aBoolean = true;
            }
        });
        btn_img.setOnClickListener(view -> {
            if(aBoolean2){
                btn_load.show();
                btn_snap.show();
                aBoolean2 = false;
            }
            else
            {
                btn_load.hide();
                btn_snap.hide();
                aBoolean2 = true;
            }
        });

        f_pdf.setOnClickListener(v -> {

            String savePDFname = filename.getText().toString();

            if(savePDFname.equals(""))
            {
                Toast.makeText(activity_ocr.this, "Enter file name", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/"+savePDFname+".pdf";

                File file = new File(path);

                if(!file.exists())
                {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }
                document.open();

                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph(recognized_text, font));

                try {
                    document.add(paragraph);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                document.close();
                Toast.makeText(activity_ocr.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }
        });

        f_docx.setOnClickListener(v -> {

            saveDOCname = filename.getText().toString();
            if(saveDOCname.equals(""))
            {
                Toast.makeText(activity_ocr.this, "Enter file name", Toast.LENGTH_SHORT).show();
            }
            else {
                createDocFile();
            }
        });
        f_share.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,txt_result.getText().toString());
            startActivity(intent);
        });

        btn_load.setOnClickListener(v -> {

            btn_load.hide();
            btn_snap.hide();
            aBoolean2 = true;
            pickImageGallery();
        });

        btn_snap.setOnClickListener(v -> {

            btn_load.hide();
            btn_snap.hide();
            aBoolean2 = true;
            showInputImageDialog();
        });

        btn_detect.setOnClickListener(v -> {
            if(imageUri == null)
            {
                Toast.makeText(activity_ocr.this, "Pick image first", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    recognizeTextFromImage();
                } catch (IOException e) {
                    Toast.makeText(activity_ocr.this, "Failed preparing Image"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        btn_copy.setOnClickListener(v -> {
            String text = txt_result.getText().toString();
            ClipboardManager clipboard;
            clipboard = (ClipboardManager) getSystemService(activity_ocr.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Text", text);
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();

        });

    }

    private void recognizeTextFromImage() throws IOException {
        Log.d(TAG, "recognizeTextFromImage: ");


        InputImage inputImage = InputImage.fromFilePath(this,imageUri);

        Task<Text> textTaskResult = textRecognizer.process(inputImage).addOnSuccessListener(text -> {

            recognized_text = text.getText();

            for (Text.TextBlock block : text.getTextBlocks()) {
                String blockText = block.getText();
                Point[] blockCornerPoints = block.getCornerPoints();
                Rect blockFrame = block.getBoundingBox();
                for (Text.Line line : block.getLines()) {
                    String lineText = line.getText();
                    Point[] lineCornerPoints = line.getCornerPoints();
                    Rect lineFrame = line.getBoundingBox();
                    for (Text.Element element : line.getElements()) {
                        String elementText = element.getText();
                        Point[] elementCornerPoints = element.getCornerPoints();
                        Rect elementFrame = element.getBoundingBox();
                        for (Text.Symbol symbol : element.getSymbols()) {
                            String symbolText = symbol.getText();
                            Point[] symbolCornerPoints = symbol.getCornerPoints();
                            Rect symbolFrame = symbol.getBoundingBox();
                        }
                    }
                }
            }
            Log.d(TAG, "onSuccess: recognizedText : "+ recognized_text);

            txt_result.setText(recognized_text);
        }).addOnFailureListener(e -> {

            Log.d(TAG, "onFailure: "+ e);
            Toast.makeText(activity_ocr.this, "Failed to recognising text : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void showInputImageDialog()
    {
        pickImageCamera();
    }

    private void pickImageGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK)
                    {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        img_capture.setImageURI(imageUri);

                    }
                }
            }
    );

    private void pickImageCamera()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== RESULT_OK)
                    {
                        img_capture.setImageURI(imageUri);
                    }
                }
            }
    );

    public void createDocFile()
    {
        String text = txt_result.getText().toString();
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        // Write the text to the .docx file
        run.setText(text);

        try {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/"+saveDOCname+".docx";

            File file = new File(path);

            FileOutputStream fos = new FileOutputStream(file);
            document.write(fos);
            fos.close();
            Toast.makeText(this, "DOCx Created", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file"+e, Toast.LENGTH_SHORT).show();
        }
    }
}