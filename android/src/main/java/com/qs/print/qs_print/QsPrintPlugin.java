package com.qs.print.qs_print;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.zxing.WriterException;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

/**
 * QsPrintPlugin
 */
public class QsPrintPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private Context context;
    private Application mApplication;
    private WeakReference<Activity> mActivity;
    private static final String channelName = "qs_print";

    private static void setup(QsPrintPlugin plugin, BinaryMessenger binaryMessenger) {
        plugin.channel = new MethodChannel(binaryMessenger, channelName);
        plugin.channel.setMethodCallHandler(plugin);

    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        setup(this, flutterPluginBinding.getBinaryMessenger());
        context = flutterPluginBinding.getApplicationContext();
        mApplication = (Application) flutterPluginBinding.getApplicationContext();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
            case ("getDevices"):
                result.success(QsBlueFunc.getDevices(context));
                break;
            case ("initQsPrint"):
                QsPrintInit.init(context);
                result.success(true);
                break;
            case ("connectDevice"):
                QsBlueFunc.connectDevice((String) call.argument("address"));
                result.success(true);
                break;
            case ("getStatus"):
                result.success(QsBlueFunc.getStatus());
                break;
            case ("disConnect"):
                QsBlueFunc.disConnect();
                result.success(true);
                break;
            case ("printString"):
                QsPrintFunc
                        .printString((String) call.argument("text"));
                result.success(true);
                break;
            case ("setBlackMark"):
                QsPrintInit.setBlackMark(call.argument("height"), call.argument("width"), call.argument("start"), call.argument("voltage"));
                result.success(true);
                break;
            case ("closeBlackMark"):
                QsPrintFunc.closeBlackMark();
                result.success(true);
                break;
            case ("printAsOrder"):
                try {
                    QsPrintFunc.printAsOrder(context, call.argument("code"), call.argument("fabCode"), call.argument("channel"), call.argument("country"), call.argument("count"), call.argument("total"));
                    result.success(true);
                } catch (WriterException e) {
                    e.printStackTrace();
                    result.error("-1", e.getMessage(), e.getLocalizedMessage());
                }
                break;
            case ("openBlackMark"):
                QsPrintFunc.openBlackMark();
                result.success(true);
                break;
            case ("resetPrint"):
                QsPrintInit.resetPrint();
                result.success(true);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = new WeakReference<>(binding.getActivity());
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {
        mActivity.clear();
    }

}
