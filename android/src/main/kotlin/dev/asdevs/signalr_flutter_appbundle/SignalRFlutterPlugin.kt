package dev.asdevs.signalr_flutter_appbundle

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** SignalRFlutterPlugin */
public class SignalRFlutterPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "signalR")
        channel.setMethodCallHandler(this)
        SignalR.channel = channel
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            CallMethod.ConnectToServer.value -> {
                val arguments = call.arguments as Map<*, *>
                @Suppress("UNCHECKED_CAST")
                SignalR.connectToServer(
                        arguments["baseUrl"] as String,
                        arguments["hubName"] as String,
                        arguments["queryString"] as String,
                        arguments["headers"] as? Map<String, String> ?: emptyMap(),
                        arguments["transport"] as Int,
                        arguments["hubMethods"] as? List<String> ?: emptyList(),
                        result)
            }
            CallMethod.Reconnect.value -> {
                SignalR.reconnect(result)
            }
            CallMethod.Stop.value -> {
                SignalR.stop(result)
            }
            CallMethod.IsConnected.value -> {
                SignalR.isConnected(result)
            }
            CallMethod.ListenToHubMethod.value -> {
                if (call.arguments is String) {
                    val methodName = call.arguments as String
                    SignalR.listenToHubMethod(methodName, result)
                } else {
                    result.error("Error", "Cast to String Failed", "")
                }
            }
            CallMethod.InvokeServerMethod.value -> {
                val arguments = call.arguments as Map<*, *>
                @Suppress("UNCHECKED_CAST")
                SignalR.invokeServerMethod(arguments["methodName"] as String, arguments["arguments"] as? List<Any>
                        ?: emptyList(), result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
