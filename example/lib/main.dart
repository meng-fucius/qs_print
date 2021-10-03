import 'dart:async';

import 'package:flutter/material.dart';
import 'package:qs_print/models/device.dart';
import 'package:qs_print/qs_print.dart';
import 'package:qs_print_example/print_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    QsPrint.init();
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  List<Device> _devices = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
          child: Column(
        children: [
          TextButton(
              onPressed: () async {
                _devices = await QsPrint.getDevices();
                if (_devices.isEmpty) {
                  showDialog(
                      context: context,
                      builder: (context) {
                        return const Text('无设备');
                      });
                }
                setState(() {});
              },
              child: const Text('扫描蓝牙')),
          const SizedBox(
            height: 10,
          ),
          Expanded(
              child: ListView(
            children: _devices
                .map(
                  (e) => GestureDetector(
                    onTap: () async {
                      await QsPrint.connectDevice(e.address);
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (BuildContext context) => const PrintPage(),
                        ),
                      );
                    },
                    child: Row(
                      children: [
                        Text(e.name),
                        const SizedBox(
                          width: 10,
                        ),
                        Text(e.address),
                      ],
                    ),
                  ),
                )
                .toList(),
          ))
        ],
      )),
    );
  }
}
