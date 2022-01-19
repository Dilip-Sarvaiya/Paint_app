package com.dilip_sarvaiya_700.imagecompressor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    int defaulColor;
    SignatureView signatureView;
    ImageButton imgEraser,imgColor,imgSave;
    SeekBar seekBar;
    TextView txtPenSize;

    int eraserSize;
    private Paint paint = new Paint();

    private static String filename;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myPaintings");

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Paint app");
        setContentView(R.layout.activity_main);

        signatureView = findViewById(R.id.signature_view);
        seekBar = findViewById(R.id.penSize);
        txtPenSize = findViewById(R.id.txtPenSize);

        imgColor = findViewById(R.id.btnColor);
        imgSave = findViewById(R.id.btnSave);
        imgEraser = findViewById(R.id.btnEraser);

        askPermission();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = simpleDateFormat.format(new Date());
        filename = path + "/" + date + ".png";

        if(!path.exists())
        {
            path.mkdirs();
        }

        defaulColor = ContextCompat.getColor(MainActivity.this,R.color.black);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPenSize.setText(progress+ " dp");
                eraserSize = progress;
                signatureView.setPenSize(progress);
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!signatureView.isBitmapEmpty())
                {
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Couldn't Save", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void saveImage() throws IOException {
        File file = new File(filename);
        Bitmap bitmap = signatureView.getSignatureBitmap();

        ByteArrayOutputStream bos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);

        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        Toast.makeText(this, "Painting Saved!", Toast.LENGTH_SHORT).show();

    }

    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaulColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaulColor = color;
                signatureView.setPenColor(color);
            }
        });
        ambilWarnaDialog.show();

    }

    private void askPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this, "Granted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}