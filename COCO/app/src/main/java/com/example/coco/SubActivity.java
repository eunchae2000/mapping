package com.example.coco;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class SubActivity extends AppCompatActivity {

    TextView textview;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        textview = (TextView) findViewById(R.id.textview1);

        // 쓰레드를 생성하여 돌리는 구간

        new Thread(new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override

            public void run() {

                data = getData(); // 하단의 getData 메소드를 통해 데이터를 파싱

                runOnUiThread(new Runnable() {


                    @Override

                    public void run() {

                        textview.setText(data);

                    }

                });


            }

        }).start();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String getData() {

        StringBuffer buffer = new StringBuffer();


//        String str = editText.getText().toString();
//        String location = URLEncoder.encode(str);


        String queryUrl =
                "http://apis.data.go.kr/1240000/bpp_openapi/getPriceInfo?serviceKey=" +
                        "16rmtxvKbYPywpIkaCtxDZasHid%2F5g6JRFXIraUTJJ0FJbiXAcghpPcXOn5AtrybVLlKV32WYE6dWruDISlFMw%3D%3D" +
                        "&pageNo=1" +
                        "&numOfRows=10" +
                        "&itemCode=" + getIntent().getStringExtra("key") +
                        "&startDate=" + "20210220" +
                        "&endDate=" + "20210220";


        try {

            URL url = new URL(queryUrl); // 문자열로 된 요청 url을 URL 객체로 생성.

//            InputStream is = url.openStream(); // url 위치로 인풋스트림 연결
            URLConnection t_connection = url.openConnection();
            t_connection.setReadTimeout(8000);
            t_connection.setConnectTimeout(8000);
            InputStream is = t_connection.getInputStream();


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser xpp = factory.newPullParser();

            // inputstream 으로부터 xml 입력받기

            xpp.setInput(new InputStreamReader(is, StandardCharsets.UTF_8));

            String tag;

            xpp.next();

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:

                        buffer.append("파싱 시작 단계 \n\n");

                        break;


                    case XmlPullParser.START_TAG:

                        tag = xpp.getName(); // 태그 이름 얻어오기


                        if (tag.equals("item")) ;

                        else if (tag.equals("pi")) {

                            buffer.append("상품 아이디 : ");

                            xpp.next();

                            // addr 요소의 TEXT 읽어와서 문자열버퍼에 추가

                            buffer.append(xpp.getText());

                            buffer.append("\n"); // 줄바꿈 문자 추가

                        } else if (tag.equals("pn")) {

                            buffer.append("상품명 : ");

                            xpp.next();

                            buffer.append(xpp.getText());

                            buffer.append("\n");

                        } else if (tag.equals("sp")) {

                            buffer.append("판매 가격 :");

                            xpp.next();

                            buffer.append(xpp.getText());

                            buffer.append("\n");

                        } else if (tag.equals("dp")) {

                            buffer.append("할인가격 :");

                            xpp.next();

                            buffer.append(xpp.getText());

                            buffer.append("\n");

                        } else if (tag.equals("bp")) {

                            buffer.append("혜택 가격 :");

                            xpp.next();

                            buffer.append(xpp.getText());//

                            buffer.append("\n");

                        } else if (tag.equals("sd")) {

                            buffer.append("가격일자 :");

                            xpp.next();

                            buffer.append(xpp.getText());

                            buffer.append("  ,  ");

                            buffer.append("\n");
                        }


                        break;


                    case XmlPullParser.TEXT:

                        break;


                    case XmlPullParser.END_TAG:

                        tag = xpp.getName(); // 태그 이름 얻어오기

                        if (tag.equals("item")) buffer.append("\n"); // 첫번째 검색결과종료 후 줄바꿈

                        break;

                }

                eventType = xpp.next();

            }

        } catch (Exception e) {

            e.printStackTrace();
            buffer.append("실패!! \n");

        }

        buffer.append("파싱 종료 단계 \n");

        return buffer.toString(); // 파싱 다 종료 후 StringBuffer 문자열 객체 반환

    }

}
