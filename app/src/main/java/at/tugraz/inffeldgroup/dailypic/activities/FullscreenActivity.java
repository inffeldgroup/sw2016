package at.tugraz.inffeldgroup.dailypic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.lang.reflect.Field;

import at.tugraz.inffeldgroup.dailypic.R;


public class FullscreenActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_image);

        SubsamplingScaleImageView imgDisplay;
        imgDisplay = (SubsamplingScaleImageView) findViewById(R.id.act_full_imgDisplay);
        imgDisplay.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        imgDisplay.setImage(ImageSource.uri(getIntent().getData()).tilingDisabled());
        Button btnClose;

        View iv = (View) imgDisplay;
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ExifInterface exif = null;
                String data = null;
                String current_tag = "";
                boolean ret = true;

                try {
                    exif = new ExifInterface(getIntent().getData().getPath());

                    data = "";
                    Field[] fields = ExifInterface.class.getFields();
                    Object o = null;

                    for (Field f : fields) {
                        current_tag = f.getName();
                        if (current_tag.startsWith("TAG")) {
                            f.setAccessible(true);
                            String str_rep = (String) f.get(o);
                            String dat = exif.getAttribute(str_rep);
                            if (dat != null) {
                                data += str_rep + ": " + dat + "\n";
                            }
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this);
                    builder.setMessage(data)
                            .setCancelable(false)
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (IOException | IllegalAccessException e) {
                    e.printStackTrace();
                    ret = false;
                    Toast.makeText(FullscreenActivity.this, "Error reading image.\nTag: " + current_tag, Toast.LENGTH_LONG).show();
                }

                return ret;
            }
        });

        btnClose = (Button) findViewById(R.id.act_full_btnClose);
        btnClose.setOnClickListener(new MyOnClickListener());
    }

    public void onFullScreenClick(View v) {
        FullscreenActivity.this.finish();
    }

    private class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            FullscreenActivity.this.finish();
        }
    }
}