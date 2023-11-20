package com.aguadeoro.threads;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.telecom.Call;
import android.util.Log;

import com.aguadeoro.activity.StockActivity;
import com.reader.bluetooth.lib.builder.vh.vh88.VH88IBluetoothRFIDReader;
import com.reader.bluetooth.lib.listener.BluetoothReadListener;
import com.reader.bluetooth.lib.listener.ConnectCallback;
import com.reader.bluetooth.lib.model.TagInfo;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ConnectThread {

    VH88IBluetoothRFIDReader bluetoothRFIDReader;
    StockActivity.MyRunnable onRead;

    Callable onConnection;
    Callable onDisconnection;


    public ConnectThread(
            Context context,
            BluetoothDevice device,
            StockActivity.MyRunnable onRead,
            Callable onConnection,
            Callable onDisconnection
    ) {
        this.bluetoothRFIDReader = new VH88IBluetoothRFIDReader(context, device);
        this.onRead = onRead;
        this.onConnection = onConnection;
        this.onDisconnection = onDisconnection;


    }

    public void run() {
        ConnectCallback callback = new ConnectCallback() {
            @Override
            public void onConnect() {
                Log.e("connected !", "connected with RFID reader");
                try {
                    onConnection.call();
                } catch (Exception e) {

                }
                bluetoothRFIDReader.addBluetoothReadTagListener(new BluetoothReadListener() {
                    @Override
                    public void onBluetoothReadTag(TagInfo tagInfo) {
                        Log.e("rfid read !", tagInfo.getEpc());
                        onRead.tag = tagInfo.getEpc();
                        onRead.run();

                    }
                });
            }

            @Override
            public void onDisconnect() {
                Log.e("disconnected", "adios");
                try {
                    onDisconnection.call();
                } catch (Exception e) {
                }
            }

            @Override
            public void onError() {
                Log.e("error", "error with reader");
                try {
                    onDisconnection.call();
                } catch (Exception e) {
                }
            }
        };
        Log.e("co", "hein");
        bluetoothRFIDReader.connect(callback);
    }

    public boolean isConnected() {
        try {
            BluetoothDevice device = bluetoothRFIDReader.getDevice();
            Method m =
                    bluetoothRFIDReader.getDevice().getClass().getMethod("isConnected");
            m.invoke(device);
            return true;
        } catch (Exception e) {
            //                throw IllegalStateException(e)
            return false;
        }
    }

    public void closeConnection() {
        bluetoothRFIDReader.close();
    }
}
