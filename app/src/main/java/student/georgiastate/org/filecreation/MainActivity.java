package student.georgiastate.org.filecreation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    EditText textArray[] = new EditText[6];
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;
    EditText formName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = (EditText) findViewById(R.id.edittext1);
        editText2 = (EditText) findViewById(R.id.edittext2);
        editText3 = (EditText) findViewById(R.id.edittext3);
        editText4 = (EditText) findViewById(R.id.edittext4);
        editText5 = (EditText) findViewById(R.id.edittext5);
        editText6 = (EditText) findViewById(R.id.edittext6);
        formName = (EditText) findViewById(R.id.formName);

        textArray[0] = editText1;
        textArray[1] = editText2;
        textArray[2] = editText3;
        textArray[3] = editText4;
        textArray[4] = editText5;
        textArray[5] = editText6;

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
                    String newText = textArray[i].getText().toString();
                    if(newText.length() > 0) {
                        fileText = fileText + newText + "\n";
                    }
                }

                try {
                    File fileDirectory = new File(Environment.getExternalStorageDirectory(), "Files");
                    fileDirectory.mkdirs();
                    String fileName = formName.getText().toString() + ".txt";
                    File file = new File(fileDirectory, fileName);
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fileOutputStream);
                    myOutWriter.append(fileText);
                    myOutWriter.close();
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Form Successfully Saved", Toast.LENGTH_LONG).show();
                    for(int i = 0; i < textArray.length; i++) {
                        textArray[i].setText("");
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
