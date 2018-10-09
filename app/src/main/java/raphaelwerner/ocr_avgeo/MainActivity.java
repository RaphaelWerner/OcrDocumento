package raphaelwerner.ocr_avgeo;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class MainActivity extends AppCompatActivity {
    static final int CAMERA_REQUEST = 1;
    static final int UPLOAD_REQUEST = 0;
    private int documentoFoto = 0;
    private EditText rg, nome, cpf, datanasc;
    private ImageView docPicture;
    ResultOCR resultOCR = new ResultOCR();
    Switch switchDoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nome = findViewById(R.id.text_NOME);
        rg = findViewById(R.id.text_RG);
        cpf = findViewById(R.id.text_CPF);
        datanasc = findViewById(R.id.text_DN);
        docPicture = findViewById(R.id.imageDoc);





        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.button_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onScanPress(view);
                InstanciarCamera();
            }
        });

        FloatingActionButton upload = (FloatingActionButton) findViewById(R.id.button_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, UPLOAD_REQUEST);

            }
        });

        switchDoc = findViewById(R.id.switch1);
        switchDoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        documentoFoto = 1;
                } else {
                       documentoFoto = 0;
                }

            }
        });

    }

    private void InstanciarCamera(){
        Intent camera = new Intent(this, CameraActivity.class);
        camera.putExtra("doc",documentoFoto);
        startActivityForResult(camera,CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

                if(requestCode == CAMERA_REQUEST){
                    DadosPessoa dadosPessoa = (DadosPessoa) data.getSerializableExtra("resultado");
                    nome.setText(dadosPessoa.Nome);
                    rg.setText(dadosPessoa.RG);
                    cpf.setText(dadosPessoa.CPF);
                    datanasc.setText(dadosPessoa.DataNascimento);
                    Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/OCR/temp.jpg");
                    docPicture.setImageBitmap(bitmap);

                }

                else if(requestCode == UPLOAD_REQUEST) {
                    inspect(data.getData());
                }
        }
        else super.onActivityResult(requestCode, resultCode, data);

    }



    private void inspect(Uri uri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);

            setTextOCR(resultOCR.inspectFromBitmap(bitmap,this,1), uri);

        } catch (FileNotFoundException e) {

        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private void setTextOCR(DadosPessoa ocr, Uri uri){
        nome.setText(ocr.Nome);
        rg.setText(ocr.Nome);
        cpf.setText(ocr.Nome);
        datanasc.setText(ocr.Nome);

        docPicture.setImageURI(uri);

    }
}
