# qs_print

A new flutter plugin project.

群索热敏打印机插件，适用型号：5801/5803/5806/8001

##初始化
建议在app启动时进行初始化以便后续连接打印机等操作
```dart
    QsPrint.init();
```
##扫描设备并连接
运行以下语句
```dart
    QsPrint.getDevices();
    QsPrint.connect(address);
```
getDevices 得到包含设备名称和地址的数组
##设置黑标
开始打印前请先开启黑标检测，否则会走纸多张
黑标只需设置一次，如果执行了设备初始化指令，需要再次设置黑标
###开启黑标
```dart
    QsPrint.openBlackMark();
```
###设置参数
```dart
    QsPrint.setBlackMark(height: 50, width: 50, start: 50, voltage: 2000)
```
heigth:黑标高度
width:黑标宽度
start:检测到黑标后，起始走纸距离
voltage:黑标检测参考电压

##打印安速面单
模板固定，暂不支持自定义
```dart
    QsPrint.printAsOrder();
```
##链接状态
```dart
    QsPrint.getStatus();
```
0：未连接
1：监听中
2：连接中
3：已连接
4：丢失连接
5：连接失败
6：连接成功
7：扫描中
8：扫描结束
##断开连接
```dart
    QsPrint.disconnect();
```
