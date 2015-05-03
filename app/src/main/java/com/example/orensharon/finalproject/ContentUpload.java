package com.example.orensharon.finalproject;

import android.os.AsyncTask;
import android.util.Log;

import com.example.orensharon.finalproject.service.objects.Photo.MyPhoto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by orensharon on 12/11/14.
 *
 * This class is representing an upload task
 * By given IP address and a path to a file / data structure -
 * It will be uploading the file to the remote pc according to IP address
 *
 */
public class ContentUpload extends AsyncTask<Object, Void, Void> {

    public static final String ApplicationJson = "application/json";

    private final String BOUNDARY = "*****";
    private final String LINE_END = "\r\n";
    private final String TWO_HYPHENS = "--";

    private final int REMOTE_IP_ADDRESS = 0;
    private final int FILE_PATH = 1;
    @Override
    protected Void doInBackground(Object... params) {

        HttpURLConnection connection;
        DataOutputStream outputStream;
        DataInputStream inputStream;
        String pathToFile, urlServer;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;

        connection = null;
        Object obj = params[FILE_PATH];

        pathToFile = "";
        if (obj instanceof MyPhoto) {
            pathToFile = MyPhoto.class.cast(obj).getFile().getPath();
        }


        urlServer = "http://" + params[REMOTE_IP_ADDRESS].toString() + ":9003/StreamService/Upload/";


        // Init maximum buffer size
        int maxBufferSize = 1*1024*1024;

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(pathToFile) );
            String fileName = pathToFile.split("/")[pathToFile.split("/").length-1];

            //urlServer; //+= fileName;
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs abd Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            // Adding request message mHeaders
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/octet");
            connection.setChunkedStreamingMode(1024);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);

            // Attaching file
            outputStream.writeBytes("Content-Disposition: attachment; filename=\"" + fileName + "\"" + LINE_END);

            // Getting MIME type of the attached file
            String mime = URLConnection.guessContentTypeFromName(pathToFile);
            outputStream.writeBytes("Content-Type: " + mime + LINE_END);
            outputStream.writeBytes(LINE_END);

            // Stat with the streaming

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);


            // Streaming the file
            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // Add suffix to the stream message
            outputStream.writeBytes(LINE_END);
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

            //Log.e("request",connection.getReq);
            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

           // Log.d("Server response after upload: (code: " + serverResponseCode + "): ",serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

        }
        catch (MalformedURLException ex)
        {
            Log.d("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe)
        {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        catch (Exception ex)
        {
            Log.e("Debug","error" + ex.getMessage(), ex);
        }


        // Read the SERVER RESPONSE
        try {
            if (connection != null) {
                inputStream = new DataInputStream(connection.getInputStream());
                String str;

                while ((str = inputStream.readLine()) != null) {
                    Log.e("Debug", "Server Response " + str);
                }
                inputStream.close();
            }

        }
        catch (IOException ioex){
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);

        }

        return null;
    }


}
