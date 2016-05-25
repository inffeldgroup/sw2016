package at.tugraz.inffeldgroup.dailypic.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import at.tugraz.inffeldgroup.dailypic.R;


public class HelpScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        InputStream is = getResources().openRawResource(R.raw.helpscreentext);
        String helpScreenText = "";
        try
        {
           helpScreenText  = IOUtils.toString(is);
            IOUtils.closeQuietly(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        TextView htmlTextView = (TextView)findViewById(R.id.help_text);
        htmlTextView.setText(Html.fromHtml(helpScreenText,null, null));
    }
}
