package student.georgiastate.org.filecreation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText textArray[] = new EditText[6];
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;
    EditText formName;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText2 = (EditText) findViewById(R.id.edittext2);
        editText3 = (EditText) findViewById(R.id.edittext3);
        editText4 = (EditText) findViewById(R.id.edittext4);
        editText5 = (EditText) findViewById(R.id.edittext5);
        editText6 = (EditText) findViewById(R.id.edittext6);
        formName = (EditText) findViewById(R.id.formName);

        textArray[0] = editText2;
        textArray[1] = editText3;
        textArray[2] = editText4;
        textArray[3] = editText5;
        textArray[4] = editText6;

    }

    public void selectImage(View view) {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent,120);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 120 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePath,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePath[0]);
            String myPath = cursor.getString(columnIndex);
            cursor.close();

            image = BitmapFactory.decodeFile(myPath);
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void saveForm(View view) {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if( isExternalStorageWritable() ) {
                String fileText = "";

                for(int i = 0; i < textArray.length; i++) {
                    if(textArray[i] != null) {
                        String newText = textArray[i].getText().toString();
                        if(newText.length() > 0) {
                            fileText = fileText + newText + "\n";
                        }
                    }
                }
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(100,200,1).create();
                PdfDocument.Page page = pdfDocument.startPage(pi);
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawPaint(paint);

                if(image != null) {
                    image = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
                    paint.setColor(Color.BLACK);
                    canvas.drawBitmap(image,0,0,null);
                    canvas.drawText(fileText, 0, image.getHeight(), paint);
                    pdfDocument.finishPage(page);
                } else {
                    canvas.drawText(fileText, 0, 0, paint);
                    pdfDocument.finishPage(page);
                }

                try {
                    File fileDirectory = new File(Environment.getExternalStorageDirectory(), "Files");
                    if(!fileDirectory.exists()) {
                        fileDirectory.mkdir();
                    }

                    String fileName = formName.getText().toString() + ".pdf";
                    File file = new File(fileDirectory, fileName);
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();

                    Toast.makeText(getApplicationContext(), "Form Successfully Saved", Toast.LENGTH_LONG).show();
                    for(int i = 0; i < textArray.length; i++) {
                        if(textArray[i] != null) {
                            textArray[i].setText("");
                        }
                    }
                    formName.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this,"ERROR - Form could't be saved",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this,"ERROR - Storage is unavailable",Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

    }
}
