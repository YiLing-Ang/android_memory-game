package iss.ca.memgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ImageDownload {
    public static Bitmap downloadImg(List<String> list, int i)
    {
        try
        {
            URL url = new URL(list.get(i - 1));
            URLConnection urlConn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.connect();

            //raw data from image URL
            InputStream in = httpConn.getInputStream();

            //converted to bitmap
            return BitmapFactory.decodeStream(in);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}