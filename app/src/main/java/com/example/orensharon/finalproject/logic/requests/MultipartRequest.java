package com.example.orensharon.finalproject.logic.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.utils.MD5Checksum;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by albert on 14-3-21.
 */
public class MultipartRequest<T> extends Request<T> {

    private static final String FILE_PART_NAME = "file";

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Response.Listener<T> mListener;
    private final File mImageFile;
    private String mToken, mTypeOfContent;

    public MultipartRequest(String url,
                                 Response.ErrorListener errorListener,
                                 Response.Listener<T> listener,
                                 File imageFile,
                                 String token,
                                 String typeOfContent)
    {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mImageFile = imageFile;
        mToken = token;
        mTypeOfContent = typeOfContent;


        buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        String localFileHash = MD5Checksum.getMd5Hash(mImageFile);

        //headers.put("Accept", "application/json");
        headers.put(ApplicationConstants.HEADER_CONTENT_MD5,localFileHash);
        //headers.put(ApplicationConstants.HEADER_AUTHORIZATION, mToken);

        return headers;
    }

    private void buildMultipartEntity()
    {

        mBuilder.addTextBody(ApplicationConstants.AUTH_TOKEN_KEY,mToken);
        mBuilder.addTextBody(ApplicationConstants.CONTENT_TYPE_OF_CONTENT_KEY, mTypeOfContent);
        mBuilder.addBinaryBody(
                FILE_PART_NAME,
                mImageFile,
                ContentType.APPLICATION_OCTET_STREAM, //ContentType.create(URLConnection.guessContentTypeFromName(mImageFile.getPath())),
                mImageFile.getName());

        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary(ApplicationConstants.POST_BODY_BOUNDARY);//.setCharset(Charset.forName("UTF-8"));
    }

    @Override
    public String getBodyContentType()
    {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            mBuilder.build().writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        String uploadResult = null;
        JSONObject json;
        try {
            uploadResult = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            json = new JSONObject();
            json.put(ApplicationConstants.CONTENT_STREAM_UPLOAD_RESULT_KEY,uploadResult);

            return (Response<T>)Response.success( json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch  (JSONException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(T response)
    {
        mListener.onResponse(response);
    }
}