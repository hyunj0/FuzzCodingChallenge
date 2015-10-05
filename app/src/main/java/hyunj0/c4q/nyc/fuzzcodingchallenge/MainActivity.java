package hyunj0.c4q.nyc.fuzzcodingchallenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListView content_list;
    ContentAdapter adapter;

    List<Content> contents;
    List<Content> filteredContents;

    ParseJSONTask parseJSONTask;

    SharedPreferences preferences;
    int preferredContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        content_list = (ListView) findViewById(R.id.content_list);

        contents = new ArrayList<>();
        filteredContents = new ArrayList<>();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        parseJSONTask = new ParseJSONTask();
        parseJSONTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        content_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Content contentClicked = contents.get(i);
                if (contentClicked.getType().equalsIgnoreCase("text")) {
                    Uri uri = (Uri.parse("https://fuzzproductions.com/"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else if (contentClicked.getType().equalsIgnoreCase("image")) {
                    Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                    intent.putExtra("image", contentClicked.getData());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public class ParseJSONTask extends AsyncTask<Void, Void, List<Content>> {

        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        @Override
        protected List<Content> doInBackground(Void... voids) {
            String endpoint = "http://quizzes.fuzzstaging.com/quizzes/mobile/1/data.json";
            try {
                JSONArray jsonArray = new JSONArray(run(endpoint));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Content content = new Content();

                    if (jsonObject.isNull("id")) {
                        content.setId("N/A");
                    } else {
                        content.setId(jsonObject.getString("id"));
                    }

                    if (jsonObject.isNull("type")) {
                        content.setType("N/A");
                    } else {
                        content.setType(jsonObject.getString("type"));
                    }

                    if (jsonObject.isNull("date")) {
                        content.setDate("N/A");
                    } else {
                        content.setDate(jsonObject.getString("date"));
                    }

                    if (jsonObject.isNull("data")) {
                        content.setData("N/A");
                    } else {
                        content.setData(jsonObject.getString("data"));
                    }

                    Log.d("content parsed", content.toString());

                    contents.add(content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return contents;
        }

        @Override
        protected void onPostExecute(List<Content> contents) {
            super.onPostExecute(contents);
            preferredContent = Integer.parseInt(preferences.getString("filterType", "4"));
            filteredContents = filteredContent(contents, preferredContent);
            adapter = new ContentAdapter(getApplicationContext(), R.layout.content_row_item, filteredContents);
            content_list.setAdapter(adapter);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        preferredContent = Integer.parseInt(preferences.getString("filterType", "4"));
        refreshContent(preferredContent);
    }

    public List<Content> filteredContent(List<Content> contents, int preferredContent) {
        switch (preferredContent) {
            case 1:
                for (Content content : contents) {
                    if (content.getType().equalsIgnoreCase("text")) {
                        filteredContents.add(content);
                    }
                }
                break;
            case 2:
                for (Content content : contents) {
                    if (content.getType().equalsIgnoreCase("image")) {
                        filteredContents.add(content);
                    }
                }
                break;
            case 3:
                for (Content content : contents) {
                    if (content.getType().equalsIgnoreCase("other")) {
                        filteredContents.add(content);
                    }
                }
                break;
            case 4:
                filteredContents.addAll(contents);
                break;
        }
        return filteredContents;
    }

    public void refreshContent(int preferredContent) {
        filteredContents.clear();
        filteredContent(contents, preferredContent);
        adapter.notifyDataSetChanged();
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}