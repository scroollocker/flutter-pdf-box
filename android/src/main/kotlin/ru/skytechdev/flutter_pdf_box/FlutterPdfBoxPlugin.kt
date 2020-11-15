package ru.skytechdev.flutter_pdf_box

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import ru.skytechdev.flutter_pdf_box.factories.PBDocumentFactory

/** FlutterPdfBoxPlugin */
class FlutterPdfBoxPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private lateinit var flutterAssets: FlutterPlugin.FlutterAssets;


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_pdf_box")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
      flutterAssets = flutterPluginBinding.flutterAssets;

//    PDFBoxResourceLoader.init(context);
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
        "init" -> {
            PDFBoxResourceLoader.init(context);
            result.success(true);
        }
        "createPdf" -> {
            val doc: PDDocument = PBDocumentFactory.create(call, flutterAssets);
            PBDocumentFactory.write(doc, call.argument("path"));

            result.success(call.argument("path"));

        }
        "modifyPdf" -> {
            val doc: PDDocument = PBDocumentFactory.modify(call, flutterAssets);
            PBDocumentFactory.write(doc, call.argument("path"));
            result.success(call.argument("path"));

        }
        "getPlatformVersion" -> {
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
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
