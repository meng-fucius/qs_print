import 'package:flutter/material.dart';
import 'package:qs_print/qs_print.dart';

class PrintPage extends StatefulWidget {
  const PrintPage({Key? key}) : super(key: key);

  @override
  _PrintPageState createState() => _PrintPageState();
}

class _PrintPageState extends State<PrintPage> {
  final TextEditingController _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('打印'),
      ),
      body: Center(
        child: Column(
          children: [
            TextField(
              controller: _controller,
              decoration: const InputDecoration(border: OutlineInputBorder()),
            ),
            TextButton(
              onPressed: () {
                QsPrint.openBlackMark();
              },
              child: const Text('开启黑标检测'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () {
                QsPrint.closeBlackMark();
              },
              child: const Text('关闭黑标检测'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () {
                QsPrint.resetPrint();
              },
              child: const Text('初始化打印机'),
            ),
            const SizedBox(
              height: 10,
            ),
            TextButton(
              onPressed: () {
                QsPrint.printString(_controller.text);
              },
              child: const Text('打印'),
            ),
            TextButton(
              onPressed: () async {
                await QsPrint.printAsOrder(
                    count: 3,
                    code: ' AS12891837841-001',
                    country: '法国',
                    fbaCode: null,
                    channel: '欧洲特快',
                    total: 10);
              },
              child: const Text('打印安速面单'),
            ),
          ],
        ),
      ),
    );
  }
}
