package com.qs.print.qs_print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.qs.helper.printer.BarcodeCreater;
import com.qs.helper.printer.PrintService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class QsPrintFunc {
    public static void openBlackMark() {
        //设置黑标电压
//        PrintService.pl.write(new byte[]{0x1b, 0x23, 0x23, 0x52, 0x42, 0x43, 0x56, (byte) 0xf4, 0x01});
        //打开增强模式
//        PrintService.pl.write(new byte[]{0x1b,0x23,0x23,0x46,0x42,0x45,0x48,0x31});

        PrintService.pl.write(new byte[]{0x1F, 0x1B, 0x1F, (byte) 0x80,
                0x04, 0x05, 0x06, 0x44});
        System.out.println(PrintService.pl.write(new byte[]{0x1d, 0x67, 0x34}));
        System.out.println(PrintService.pl.write(new byte[]{0x1d, 0x67, 0x35}));

    }

    public static void closeBlackMark() {
        PrintService.pl.write(new byte[]{0x1F, 0x1B, 0x1F, (byte) 0x80,
                0x04, 0x05, 0x06, 0x66});
    }

    public static void printString(String text) {
        try {
            byte[] send = text.getBytes("GBK");

            PrintService.pl.write(send);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PrintService.pl.printText("\n");
        PrintService.pl.write(new byte[]{0x1d, 0x0c});
    }

    public static void printAsOrder(Context context, String code, String fbaCode, String channel, String country, int count, int total) throws WriterException {
        byte[] btdata = null;
        try {
            btdata = code.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //设置纵向移动单位
        PrintService.pl.write(new byte[]{0x1d, 0x50, 0x10});
        //设置行间距
        PrintService.pl.write(new byte[]{0x1b, 0x32});
        //开启条码打印
        PrintService.pl.write(new byte[]{0x1d, 0x45, 0x43, 0x01});


        //Set the barcode height is 162
//					MainActivity.pl.write(new byte[]{0x1d,0x68,(byte) 0xa2});
        PrintService.pl.write(new byte[]{0x1d, 0x68, (byte) 0x82});


        //Set HRI character print location on bottom
        PrintService.pl.write(new byte[]{0x1d, 0x48, 0x00});


        PrintService.pl.write(new byte[]{0x1d, 0x77, 0x02});

        //Print the barcode use code128

        byte[] qrHead = new byte[]{0x1d, 0x6b, 0x49, (byte) btdata.length};
//					byte[] qrHead=new byte[]{0x1d,0x6b,0x44,(byte) btdata.length};

        byte[] barCodeData = new byte[qrHead.length + btdata.length];
        System.arraycopy(qrHead, 0, barCodeData, 0, qrHead.length);
        System.arraycopy(btdata, 0, barCodeData, qrHead.length, btdata.length);
        //居中指令
        PrintService.pl.write(new byte[]{0x1b, 0x61, 0x01});
        //打印条码
        PrintService.pl.write(barCodeData);
        PrintService.pl.printText("\n");
        //关闭条码打印
        PrintService.pl.write(new byte[]{0x1d, 0x45, 0x43, 0x00});
        //设置加粗
        PrintService.pl.write(new byte[]{0x1b, 0x45, 0x01});
        //打印条码内容
        PrintService.pl.printText(code);
        PrintService.pl.write(new byte[]{0x1b, 0x4a, 0x1a});
//        PrintService.pl.printText("\n");
        //判断是否fba订单
        if (fbaCode == null) {
            fbaCode = "非FBA订单";
        } else {
            fbaCode = "FBA单号：" + fbaCode;
        }
        //打印fba单号
        PrintService.pl.printText(fbaCode);
        PrintService.pl.write(new byte[]{0x1b, 0x4a, 0x0a});
//        PrintService.pl.printText("\n");
        printDivider();
//        PrintService.pl.printText("\n");
        //居左
        PrintService.pl.write(new byte[]{0x1b, 0x61, 0x00});
//        //设置左边距
//        PrintService.pl.write(new byte[]{0x1d, 0x4c, 0x2d, 0x00});
        //打印渠道
        channel = "\t渠道名称：" + channel;
        PrintService.pl.printText(channel);
//        PrintService.pl.printText("\n");
        PrintService.pl.write(new byte[]{0x1b, 0x4a, (byte) 0x2a});
        //打印目的地
        country = "\t目的国：" + country;
//        PrintService.pl.printText(country);
//        PrintService.pl.write(new byte[]{0x1b,0x4a,0x01});
//        PrintService.pl.printText("\b");
//        //居左
//        PrintService.pl.write(new byte[]{0x1b, 0x61, 0x02});
        //打印件数
        String countString = "件数：" + String.valueOf(count) + "/" + String.valueOf(total);
//        PrintService.pl.printText(countString);
        String bottom = country + "\t\t" + countString;
        //打印目的国和件数
        PrintService.pl.printText(bottom);
        PrintService.pl.printText("\n");
        //换页
        PrintService.pl.write(new byte[]{0x1d, 0x0c});
        //居左
        PrintService.pl.write(new byte[]{0x1b, 0x61, 0x00});

    }

    public static void printASBitMap(Context context, String code, String fbaCode, String channel, String country, int count, int total) {
        int width = 590;
        Bitmap asBitMap;
        TextPaint textPaint = new TextPaint();

        textPaint.setColor(Color.BLACK);

        textPaint.setTextSize(32);
//        textPaint.setFakeBoldText(true);

        TextPaint boldPaint = new TextPaint();
        boldPaint.setTextSize(40);
        boldPaint.setFakeBoldText(true);
        //生成条码
        Bitmap barCode = BarcodeCreater.creatBarcode(context, code, width, 130, false, 1);
        //居中指令
        PrintService.pl.write(new byte[]{0x1b, 0x61, 0x01});
        //打印条码
        PrintService.pl.write(draw2PxPoint(barCode));
        //换行
        PrintService.pl.write(new byte[]{0x1b, 0x0a});
//        //
//        PrintService.pl.write(new byte[]{0x1d, 0x48, 0x0a});
        //画as单号
        System.out.println("code:" + code);
        StaticLayout layout = new StaticLayout(code, boldPaint, width,
                Layout.Alignment.ALIGN_CENTER, 1.5f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
        layout.draw(canvas);
        //打印单号
        PrintService.pl.printImage(bitmap);
        //换行
        PrintService.pl.write(new byte[]{0x1b, 0x0a});
        //画fba单号
        if (fbaCode == null) {
            fbaCode = "非FBA订单";
        } else {
            fbaCode = "FBA单号：" + fbaCode;
        }
        ;
        System.out.println("fba:" + fbaCode);
        layout = new StaticLayout(fbaCode, textPaint, width, Layout.Alignment.ALIGN_CENTER, 1.5f, 0, true);
        bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
        layout.draw(canvas);
        //打印fba单号
        PrintService.pl.printImage(bitmap);
        //换行
        PrintService.pl.write(new byte[]{0x1b, 0x0a});
        //分割线
        String devider = "—————————————————————————";
        System.out.println(devider);
        layout = new StaticLayout(devider, boldPaint, width, Layout.Alignment.ALIGN_CENTER, 1.5f, 0, true);
        bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
        layout.draw(canvas);
        //打印分割线
        PrintService.pl.write(draw2PxPoint(bitmap));
        //换行
        PrintService.pl.write(new byte[]{0x1b, 0x0a});
        //渠道
        channel = "渠道名称：" + channel;
        System.out.println("channel：" + channel);
        layout = new StaticLayout(channel, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0, true);
        bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
        layout.draw(canvas);
        //打印渠道
        PrintService.pl.printImage(bitmap);
        //换行
        PrintService.pl.write(new byte[]{0x1b, 0x0a});
        //国家
        country = "目的国：" + country;
        System.out.println("country:" + country);
        String countString = "件数：" + String.valueOf(count) + "/" + String.valueOf(total);
        System.out.println("count" + countString);
        layout = new StaticLayout(country, textPaint, width / 2, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0, true);
        StaticLayout layout2 = new StaticLayout(countString, textPaint, width / 2, Layout.Alignment.ALIGN_OPPOSITE, 1.5f, 0, true);
        bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap2 = Bitmap.createBitmap(layout2.getWidth(),
                layout.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        Canvas canvas2 = new Canvas(bitmap2);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
        canvas2.drawColor(Color.WHITE);
        layout.draw(canvas);
        layout2.draw(canvas2);
        //合并图片
        asBitMap = mergeLeftRight(bitmap, bitmap2);
        //打印国家和件数
        PrintService.pl.printImage(asBitMap);
    }

//    //条形码加白边
//    public static Bitmap addEdge(Bitmap bitmap) {
//        Paint bgPaint = new Paint();
//        bgPaint.setAntiAlias(true);
//        int edge = (590 - bitmap.getWidth()) / 2;
//        Bitmap outBitmap = Bitmap.createBitmap(590, 130, bitmap.getConfig());
//        Canvas canvas = new Canvas(bitmap);
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        final RectF rectF = new RectF(rect);
//        bgPaint.setColor(Color.WHITE);
//        canvas.drawRect(rectF, bgPaint);
//        canvas.drawBitmap(bitmap, edge, 0, bgPaint);
//        return outBitmap;
//    }

    //打印分割线
    public static void printDivider() {
        int width = 450;
        int height = 3;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = 0xff000000;
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        PrintService.pl.write(draw2PxPoint(bitmap));
    }


    //图片上下合并
    public static Bitmap mergeUpDown(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(),
                bitmap1.getHeight() + bitmap2.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
        return bitmap3;
    }

    //  图片左右合并
    public static Bitmap mergeLeftRight(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth() + bitmap2.getWidth(),
                bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth(), 0, null);
        return bitmap3;
    }


    /**
     * 用于将给定的内容生成成一维码 注：目前生成内容为中文的话将直接报错，要修改底层jar包的内容
     *
     * @param content 将要生成一维码的内容
     * @return 返回生成好的一维码bitmap
     * @throws WriterException WriterException异常
     */
    public static Bitmap CreateOneDCode(String content) throws WriterException {
        //
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.CODE_128, 380, 100);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    //打印图片
    public static byte[] draw2PxPoint(Bitmap bmp) {
        // 用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        // 整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
        // 但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        // 所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 2000;
        byte[] data = new byte[size];
        int k = 0;
        // 设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            // 打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); // nL
            data[k++] = (byte) (bmp.getWidth() / 256); // nH
            // 对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                // 每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    // 每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;// 换行
        }
        return data;
    }

    //图片灰度化
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    //图片转黑白
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }


}

