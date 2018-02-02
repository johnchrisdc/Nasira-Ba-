package xyz.jcdc.nasiraba;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            Log.d(TAG, "onCreate: " + document.title());

            Elements news_main_item = document.getElementsByAttributeValueContaining("class", "news-main-item");

            if (news_main_item.first() != null) {
                String p_text = news_main_item.first().getElementsByClass("news-main-title").text();
                if (p_text.equalsIgnoreCase(Helpah.getCurrentDateString())) {
                    status.setText("OO");
                } else {
                    status.setText("DEHINDS PA!");
                    sub_status.setText("Last na nabroken </3 " + news_main_item.first().getElementsByClass("news-main-title").text());
                }
            }

            for (Element element : news_main_item) {
                Log.d(TAG, "onPostExecute: " + element.getElementsByClass("news-main-title").text());

                String p_text = element.getElementsByClass("news-main-title").text();
                if (p_text.equalsIgnoreCase(Helpah.getCurrentDateString())) {
                    Toast.makeText(context, "OO!", Toast.LENGTH_LONG).show();
                }
            }


            materialProgressBar.setVisibility(View.GONE);
        }
    }

    private class TrainAvailabilityTask extends AsyncTask<Void, Void, Document> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: " + "Fetching");
            materialProgressBar.setVisibility(View.VISIBLE);
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
            Log.d(TAG, "onCreate: " + document.title());

            Elements news_main_item = document.getElementsByAttributeValueContaining("class", "page-preview-content");

            if (news_main_item.first() != null) {
                Log.d(TAG, "by tag: " + news_main_item.first().getElementsByTag("p"));
                String p_text = news_main_item.first().getElementsByTag("p").text();

                train_availability.setText(p_text);
            }

            materialProgressBar.setVisibility(View.GONE);
        }
    }
}
