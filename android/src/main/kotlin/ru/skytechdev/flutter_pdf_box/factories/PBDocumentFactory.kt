package ru.skytechdev.flutter_pdf_box.factories

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import java.io.File

class PBDocumentFactory private constructor(private val pdDocument: PDDocument?, private val path: String?, private val flutterAssets: FlutterPlugin.FlutterAssets) {


    companion object {
        fun create(call: MethodCall, flutterAssets: FlutterPlugin.FlutterAssets): PDDocument {
            val doc = PDDocument();
            val factory = PBDocumentFactory(doc, call.argument<String>("path"), flutterAssets);
//            println(call.argument<ArrayList<Map<String, Any>>>("pages"));
//            return doc;
////            return;
            factory.addPages(call.argument<ArrayList<Map<String, Any>>>("pages"))
            return doc;
        }

        fun modify(call: MethodCall, flutterAssets: FlutterPlugin.FlutterAssets): PDDocument {
            val path: String? = call.argument<String>("path");
            val doc: PDDocument = PDDocument.load(File(path))
            val factory = PBDocumentFactory(doc, path, flutterAssets);
            factory.modifyPages(call.argument<ArrayList<Map<String, Any>>>("modifyPages"))
            factory.addPages(call.argument<ArrayList<Map<String, Any>>>("pages"))
            return doc;
        }

        fun write(pdDocument: PDDocument?, path: String?): String? {
            pdDocument?.save(path)
            pdDocument?.close()
            return path;
        }
    }

    private fun addPages(map: ArrayList<Map<String, Any>>?) {
        map?.forEach {
            val page: PDPage? = PBPageFactory.create(pdDocument, it, flutterAssets);
            pdDocument?.addPage(page);
        }
    }

    private fun modifyPages(map: ArrayList<Map<String, Any>>?) {
        map?.forEach {
            PBPageFactory.modify(pdDocument, it, flutterAssets);
        }
    }
}