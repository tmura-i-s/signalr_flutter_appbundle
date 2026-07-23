import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:signalr_flutter_appbundle/signalr_flutter_appbundle.dart';

void main() async {
  const MethodChannel channel = MethodChannel('signalR');
  final SignalR signalR = SignalR('Url', "hubName");

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      switch (methodCall.method) {
        case "connectToServer":
          return true;
        case "invokeServerMethod":
          return <String, dynamic>{
            'baseUrl': "123",
            "hubName": "456",
          };
        default:
          return PlatformException(
              code: "Error",
              message:
                  "No implementation found for method ${methodCall.method}");
      }
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  group('SignalR Test', () {
    test('Connect SignalR', () async {
      final result = await signalR.connect();
      expect(result, true);
    });

    test('Invoke Server Method', () async {
      final result = await signalR.invokeMethod("methodName", arguments: null);
      expect(result, <String, dynamic>{
        'baseUrl': "123",
        "hubName": "456",
      });
    });
  });
}
