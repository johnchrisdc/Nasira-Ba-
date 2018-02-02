package xyz.jcdc.nasiraba;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import xyz.jcdc.nasiraba.mowdel.Broken;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Context context;

    private FetchFetch fetchFetch;
    private TrainAvailabilityTask trainAvailabilityTask;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress)
    MaterialProgressBar materialProgressBar;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.sub_status)
    TextView sub_status;

    @BindView(R.id.train_availability)
    TextView train_availability;

    @BindView(R.id.lenny)
    TextView lenny;

    @BindView(R.id.train_availability_container)
    LinearLayout train_availability_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (fetchFetch != null)
            fetchFetch.cancel(true);

        if (trainAvailabilityTask != null)
            trainAvailabilityTask.cancel(true);


        fetchFetch = new FetchFetch();
        trainAvailabilityTask = new TrainAvailabilityTask();

        fetchFetch.execute();
        trainAvailabilityTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fetchFetch != null)
            fetchFetch.cancel(true);

        if (trainAvailabilityTask != null)
            trainAvailabilityTask.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            Toast.makeText(context, "Hello World!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchFetch extends AsyncTask<Void, Void, Document> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: " + "Fetching");
            materialProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Document doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Variables.URL).get();

                return doc;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);

            materialProgressBar.setVisibility(View.GONE);

            if (document == null) {
                lenny.setVisibility(View.VISIBLE);
                sub_status.setText("Something went wrong, maybe yer internet connection");
                return;
            }

            Log.d(TAG, "onCreate: " + document.title());

            Elements news_main_item = document.getElementsByAttributeValueContaining("class", "news-main-item");

            if (news_main_item.first() != null) {
                String p_text = news_main_item.first().getElementsByClass("news-main-title").text();
                String date_string = Helpah.getCurrentDateString();

                Log.d(TAG, "onPostExecute: " + date_string);

                if (p_text.equalsIgnoreCase(date_string)) {
                    status.setText("OO");

                    Element table = news_main_item.first().select("table").get(0); //select the first table.
                    Elements rows = table.select("tr");

                    Broken broken = new Broken();

                    Element row = rows.get(1);
                    Elements cols = row.select("td");

                    broken.setTime(cols.get(0).text());
                    broken.setDescription(cols.get(1).text());
                    broken.setStatus(cols.get(2).text());
                    broken.setStation(cols.get(3).text());
                    broken.setBound(cols.get(4).text());

                    sub_status.setText(broken.getStation() + ", " + broken.getBound() + " " + broken.getTime() + "\n" + broken.getDescription());

                    lenny.setVisibility(View.VISIBLE);
                } else {
                    status.setText("HINDI PA");
                    sub_status.setText("Last na nabroken </3 " + news_main_item.first().getElementsByClass("news-main-title").text());

                    lenny.setVisibility(View.GONE);
                }
            }
        }
    }

    private class TrainAvailabilityTask extends AsyncTask<Void, Void, Document> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: " + "Fetching");
        }

        @Override
        protected Document doInBackground(Void... voids) {
            try {
                return Jsoup.connect(Variables.TRAIN_AVAILABILITY_URL).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);

            if (document == null)
                return;

            Log.d(TAG, "onCreate: " + document.title());

            Elements news_main_item = document.getElementsByAttributeValueContaining("class", "page-preview-content");

            if (news_main_item.first() != null) {
                Log.d(TAG, "by tag: " + news_main_item.first().getElementsByTag("p"));
                String p_text = news_main_item.first().getElementsByTag("p").text();

                train_availability.setText(p_text);
            }

            train_availability_container.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(context)
                .title("Exit")
                .content("Are you sure you want to exit?")
                .negativeText("Dismiss")
                .positiveText("Yes")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

}
