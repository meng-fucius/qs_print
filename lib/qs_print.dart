import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:qs_print/models/device.dart';

class QsPrint {
  static const MethodChannel _channel = MethodChannel('qs_print');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> init() async {
    final result = await _channel.invokeMethod('initQsPrint');
    return result;
  }

  static Future<List<Device>> getDevices() async {
    final result = await _channel.invokeMethod('getDevices');
    if (result == null) {
      return [];
    }
    return (jsonDecode(result) as List)
        .map((e) => Device(name: e['deviceName'], address: e['deviceAddress']))
        .toList();
  }

  static Future connectDevice(String address) async {
    final result =
        await _channel.invokeMethod('connectDevice', {'address': address});
    return result;
  }

  static Future<int> getStatus() async {
    final result = await _channel.invokeMethod('getStatus');
    return result;
  }

  static Future disConnect() async {
    final result = await _channel.invokeMethod('disConnect');
    return result;
  }

  static Future printString(String text) async {
    final result = await _channel.invokeMethod('printString', {'text': text});
    return result;
  }

  static Future setBlackMark(
      {required int height,
      required int width,
      required int start,
      required int voltage}) async {
    final result = await _channel.invokeMethod('setBlackMark', {
      'height': height,
      'width': width,
      'start': start,
      'voltage': voltage,
    });
    return result;
  }

  static Future closeBlackMark() async {
    final result = await _channel.invokeMethod('closeBlackMark');
    return result;
  }

  static Future printAsOrder({
    required String code,
    String? fbaCode,
    required String channel,
    required String country,
    required int count,
    required int total,
  }) async {
    final result = await _channel.invokeMethod('printAsOrder', {
      'code': code,
      'fabCode': fbaCode,
      'channel': channel,
      'country': country,
      'count': count,
      'total': total
    });
    return result;
  }

  static Future openBlackMark() async {
    final result = await _channel.invokeMethod('openBlackMark');
    return result;
  }

  static Future resetPrint() async {
    final result = await _channel.invokeMethod('resetPrint');
    return result;
  }
}
