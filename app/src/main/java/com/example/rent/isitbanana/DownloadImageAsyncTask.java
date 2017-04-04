package com.example.rent.isitbanana;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by User on 2017-02-02.
 */
public class DownloadImageAsyncTask extends AsyncTask<Void,Void,Bitmap>{

    RetrofitImageApi retrofitImageApiClient;
    Context context;
    ImageDownloadListener imageDownloadListener;
    String url;


    public DownloadImageAsyncTask(String url, Context context,ImageDownloadListener imageDownloadListener) {

        this.url=url;
        this.context= context;
        this.imageDownloadListener = imageDownloadListener;
        RetrofitImageApiFactory factory = new RetrofitImageApiFactory();
        retrofitImageApiClient = factory.create(url);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {

        Call<ResponseBody> call = retrofitImageApiClient.getImageDetails();


        ///Change to this!!!!!!!!!!!!!!!!
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        try {
//            client.newCall(request).execute();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            Response<ResponseBody> response = call.execute();

            if(response.isSuccessful()) { //http 200+
                Log.d("DownloadImageAsyncTask", "doInBackground: response succesful");
                return getImageFromBody(response.body());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bananaman);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageDownloadListener.onDownloaded(bitmap);
        super.onPostExecute(bitmap);
    }

    private Bitmap getImageFromBody(ResponseBody body) {

        try {
            Log.d("DownloadImage", "Reading and writing file");
            InputStream in = null;
            FileOutputStream out = null;


            try {
                in = body.byteStream();
                out = new FileOutputStream(context.getExternalFilesDir(null) + File.separator + "IsItBananaDownloadedPhoto.jpg");
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            catch (IOException e) {
                Log.d("DownloadImage",e.toString());
            }
            finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            int width, height;
            Bitmap bMap = BitmapFactory.decodeFile(context.getExternalFilesDir(null) + File.separator + "IsItBananaDownloadedPhoto.jpg");
//            width = 2*bMap.getWidth();
//            height = 6*bMap.getHeight();
//            Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);

            return bMap;

        } catch (IOException e) {
            Log.d("DownloadImage",e.toString());
        }
        return null;
    }


    public interface ImageDownloadListener {
        void onDownloaded(Bitmap bitmap);
    }
}

