class Device {
  String name;
  String address;

  Device({
    required this.name,
    required this.address,
  });

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'address': address,
    };
  }

  factory Device.fromMap(Map<String, dynamic> map) {
    return Device(
      name: map['name'] as String,
      address: map['address'] as String,
    );
  }
}
