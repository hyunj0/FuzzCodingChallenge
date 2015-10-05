package hyunj0.c4q.nyc.fuzzcodingchallenge;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageActivity extends Activity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        image = (ImageView) findViewById(R.id.image);

        DownloadImageTask downloadImageTask = new DownloadImageTask();
        String url = getIntent().getExtras().getString("image");
        downloadImageTask.execute(url);
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmap;

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2500);
                int responseCode = connection.getResponseCode();
                Log.d("responseCode", strings[0] + " " + responseCode);
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return bitmap;
                }
                inputStream = connection.getInputStream();
                if (inputStream != null) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeStream(inputStream, new Rect(1,1,1,1), options);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) {
                image.setImageResource(R.drawable.not_applicable);
            } else {
                image.setImageBitmap(bitmap);
            }
        }
    }
}