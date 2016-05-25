package at.tugraz.inffeldgroup.dailypic.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;


import at.tugraz.inffeldgroup.dailypic.R;



public class HelpScreenActivity extends AppCompatActivity {


    private final String help_text = "<body><h1>Heading Text</h1><p>This tutorial " +
            "explains how to display " +
            "<strong>HTML </strong>text in android text view.&nbsp;</p>" +
            "<img src=\"hughjackman.jpg\">" +
            "<blockquote>Example from <a href=\"www.javatechig.com\">" +
            "Javatechig.com<a></blockquote></body>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        TextView htmlTextView = (TextView)findViewById(R.id.help_text);
        htmlTextView.setText(Html.fromHtml(help_text,null, null));
    }



}
