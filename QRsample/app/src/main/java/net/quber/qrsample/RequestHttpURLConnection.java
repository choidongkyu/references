package net.quber.qrsample;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static net.quber.qrsample.ActiveScannerActivity.TAG;

public class RequestHttpURLConnection {
    public String request(String _url, HashMap<String, String> param) {
        HttpURLConnection urlConnection = null;
//        StringBuffer sbParam = new StringBuffer();


        try {
            Log.d(TAG, "request start!");
            URL url = new URL(_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(param)); // 출력 스트림에 출력.
            writer.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
            writer.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "http 연결 실패");
                return null;
            }

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;
        } catch (MalformedURLException e) {
            Log.d(TAG,"MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG,"IOException");
            e.printStackTrace();
        }
        Log.d(TAG,"mistake params");
        return null;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        Log.d(TAG,"getPostDataString start!");
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
