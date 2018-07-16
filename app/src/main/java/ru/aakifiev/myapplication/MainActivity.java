package ru.aakifiev.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import ru.aakifiev.myapplication.model.LikadaProtocol;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private Button ping;
    private TextView output;
    private OkHttpClient client;
    private LocationManager locationManager;
    LocationListener locationListener;
    WebSocket webSocket;
    //private WebSocket ws;

    class firstTask extends TimerTask {

        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
            startActivity(intent);
        }
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        ping = (Button) findViewById(R.id.ping);
        output = (TextView) findViewById(R.id.output);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                output(location.getLatitude() + " : " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        client = new OkHttpClient();
        start.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, locationListener);
                //start();
            }
        });

        /*ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ping();
            }
        });*/


        /*Toast toast = Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, locationListener);
    }

    private void ping() {
        webSocket.send(ByteString.EMPTY);
    }

    private void start() {

        /*final StompedClient client =
                new StompedClient.StompedClientBuilder().build("ws://192.168.1.38:8080/gs-guide-websocket/websocket");

        client.subscribe("/topic/greetings", new StompedListener() {
            @Override
            public void onNotify(StompedFrame stompedFrame) {
                output(stompedFrame.getStompedBody());
                //client.disconnect();
            }
        });

        client.send("/hello", "hello username");*/

        Request request = new Request.Builder().url("http://192.168.1.38:8081/gs-guide-websocket/websocket")
                .addHeader("deviceId", "461")
                .build();

        EchoWebSocketListener listener = new EchoWebSocketListener();
        webSocket = client.newWebSocket(request, listener);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ping();
            }
        }, 10000, 10000);
        //client.dispatcher().executorService().shutdown();
    }

    private void output(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + text);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            /*webSocket.send("Hello, it's SSaurel !");
            webSocket.send("What's up ?");*/
            //webSocket.send(ByteString.decodeHex("deadbeef"));
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
            Gson gson = new Gson();
            LikadaProtocol likadaProtocol = new LikadaProtocol();
            likadaProtocol.setMessageId("ddd");
            likadaProtocol.setMessage("461:Доставлено");
            webSocket.send(gson.toJson(likadaProtocol));
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }
}
