package com.qs.print.qs_print;

import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QsBlueFunc {

    private static List<Device> deviceList = new ArrayList<Device>();

    //    private static ArrayAdapter<String> mNewDevicesArrayAdapter = null;
//扫描附近设备
    public static String getDevices(Context context) {
        if (deviceList != null) {
            deviceList.clear();
        }
        if (!PrintService.pl.IsOpen()) {
            PrintService.pl.open(context);
        }
//        mNewDevicesArrayAdapter.clear();
        PrintService.pl.scan();
        deviceList = PrintService.pl.getDeviceList();
        Gson gson = new Gson();
        String jsonString = gson.toJson(deviceList);
        return jsonString;
    }

    //连接设备
    public static void connectDevice(String address) {
        PrintService.pl.connect(address);
    }

    //获取连接状态
    //0：未连接
    //1：监听中
    //2：连接中
    //3：已连接
    //4：丢失连接
    //5：连接失败
    //6：连接成功
    //7：扫描中
    //8：扫描结束
    public static int getStatus() {
        return PrintService.pl.getState();
    }

    public static void disConnect() {
        PrintService.pl.disconnect();
    }
}
