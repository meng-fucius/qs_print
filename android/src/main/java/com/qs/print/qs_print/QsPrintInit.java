package com.qs.print.qs_print;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.qs.helper.printer.bt.BtService;

import java.util.ArrayList;

public class QsPrintInit {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private static Handler mhandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readBuf[0] == 0x13) {
                        PrintService.isFUll = true;

                    } else if (readBuf[0] == 0x11) {
                        PrintService.isFUll = false;

                    } else if (readBuf[0] == 0x08) {

                    } else if (readBuf[0] == 0x01) {
                        //ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                    } else if (readBuf[0] == 0x04) {

                    } else if (readBuf[0] == 0x02) {
//                        ShowMsg(getResourcesrces().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_lowpower));
                    } else {
                        if (readMessage.contains("800"))// 80mm paper
                        {
                            PrintService.imageWidth = 72;
//                            Toast.makeText(getApplicationContext(), "80mm",
//                                    Toast.LENGTH_SHORT).show();
                        } else if (readMessage.contains("580"))// 58mm paper
                        {
                            PrintService.imageWidth = 48;
//                            Toast.makeText(getApplicationContext(), "58mm",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case PrinterClass.STATE_CONNECTED:
                            break;
                        case PrinterClass.STATE_CONNECTING:
//                            Toast.makeText(getApplicationContext(),
//                                    "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.STATE_LISTEN:
                        case PrinterClass.STATE_NONE:
                            break;
                        case PrinterClass.SUCCESS_CONNECT:
                            ////PrintService.pl.write(new byte[] { 0x1b, 0x2b });
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            PrintService.pl.write(new byte[]{0x1d, 0x67, 0x33});
//                            Toast.makeText(getApplicationContext(),
//                                    "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.FAILED_CONNECT:
//                            Toast.makeText(getApplicationContext(),
//                                    "FAILED_CONNECT", Toast.LENGTH_SHORT).show();

                            break;
                        case PrinterClass.LOSE_CONNECT:
//                            Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
//                                    Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
            }
            super.handleMessage(msg);
        }
    };
    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    Device d = (Device) msg.obj;
                    if (d != null) {
//                        if (PrintSettingActivity.deviceList == null) {
//                            PrintSettingActivity.deviceList = new ArrayList<Device>();
//                        }
//                        if (!checkData(PrintSettingActivity.deviceList, d)) {
//                            PrintSettingActivity.deviceList.add(d);
//                        }
                    }
                    break;
                case 2:
                    break;
            }
        }
    };

    public static void init(Context context) {
        PrintService.pl = new BtService(context, mhandler, handler);

    }

    public static void resetPrint() {
        //初始化打印机
        PrintService.pl.write(new byte[]{0x1b, 0x40});
    }

    public static void setBlackMark(int height, int width, int start, int voltage) {
        //设置黑标 高度
        PrintService.pl.write(twoToOne(new byte[]{0x1F, 0x1B, 0x1F, (byte) 0x81, 0x04, 0x05, 0x06}, toLH(height)));

        // 宽度
        PrintService.pl.write(twoToOne(new byte[]{0x1F, 0x1B, 0x1F, (byte) 0x82, 0x04, 0x05, 0x06}, toLH(width)));
        //起始
        PrintService.pl.write(twoToOne(new byte[]{0x1D, 0x54, 0x1D, 0x28, 0x46, 0x04, 0x00, 0x01, 0x00},
                toLH(start)));
        //  电压
        PrintService.pl.write(twoToOne(new byte[]{0x1B, 0x23, 0x23, 0X53, 0X42, 0X43, 0x56}, toLH(voltage))
        );

        System.out.println(PrintService.pl.write(new byte[]{0x1d, 0x67, 0x34}));
        System.out.println(PrintService.pl.write(new byte[]{0x1d, 0x67, 0x35}));
    }

    /**
     * 小端模式 将int转为低字节在前，高字节在后的byte数组
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] toLH(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
//		b[2] = (byte) (n >> 16 & 0xff);
//		b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 将int转为高字节在前，低字节在后的byte数组
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] toHH(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n & 0xff);
        return b;
    }


    /**
     * 合并数组
     *
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] twoToOne(byte[] data1, byte[] data2) {

        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }

}
