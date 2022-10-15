package lk.sliit.assignment2partb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ImageRetrievalTask implements Callable<ArrayList<Bitmap>> {
    private Activity uiActivity;
    private String data;
    private RemoteUtilities remoteUtilities;
    public ImageRetrievalTask(Activity uiActivity) {
        remoteUtilities = RemoteUtilities.getInstance(uiActivity);
        this.uiActivity=uiActivity;
        this.data = null;
    }
    @Override
    public ArrayList<Bitmap> call() throws Exception {
        Bitmap image = null;
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        String endpoint = getEndpoint(this.data);
        ArrayList<String> endpointList = getEndpoints(this.data);
        //System.out.println("\n\n\nPrinting endpoint list\n"+endpointList);
        if(endpoint==null){
            uiActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiActivity,"No image found",Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            image = getImageFromUrl(endpoint);
            bitmaps = getImagesFromUrls(endpointList);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
            }

        }
        assert image != null;
        //System.out.println("\n\n\nBitmap image"+image.toString());
        //System.out.println("\n\n\nBitmap list"+bitmaps.toString());
        return bitmaps;
    }

    private String getEndpoint(String data){
        String imageUrl = null;
        try {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");
            if(jHits.length()>0){
                JSONObject jHitsItem = jHits.getJSONObject(0);
                imageUrl = jHitsItem.getString("webformatURL");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    private ArrayList<String> getEndpoints(String data){
        ArrayList<String> imageUrls = new ArrayList<>();
        try {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");
            if(jHits.length()>0){
                for (int i = 0; i < 15; i++) {
                    JSONObject jHitsItem = jHits.getJSONObject(i);
                    imageUrls.add(jHitsItem.getString("largeImageURL"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }

    private Bitmap getImageFromUrl(String imageUrl){
        Bitmap image = null;
        Uri.Builder url = Uri.parse(imageUrl).buildUpon();
        String urlString = url.build().toString();
        HttpURLConnection connection = remoteUtilities.openConnection(urlString);
        if(connection!=null){
            if(remoteUtilities.isConnectionOkay(connection)==true){
                image = getBitmapFromConnection(connection);
                connection.disconnect();
            }
        }
        return image;
    }

    private ArrayList<Bitmap> getImagesFromUrls(ArrayList<String> imageUrls){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        String imageUrl;
        for (int i = 0; i < imageUrls.size(); i++) {
            imageUrl = imageUrls.get(i);
            Uri.Builder url = Uri.parse(imageUrl).buildUpon();
            String urlString = url.build().toString();
            HttpURLConnection connection = remoteUtilities.openConnection(urlString);
            if(connection!=null){
                if(remoteUtilities.isConnectionOkay(connection)==true){
                    bitmaps.add(getBitmapFromConnection(connection));
                    connection.disconnect();
                }
            }
        }
        return bitmaps;
    }


    public Bitmap getBitmapFromConnection(HttpURLConnection conn){
        //System.out.println("\n\n\nPrinting connection\n"+conn);
        Bitmap data = null;
        try {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);
            data = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public void setData(String data) {
        this.data = data;
    }
}
