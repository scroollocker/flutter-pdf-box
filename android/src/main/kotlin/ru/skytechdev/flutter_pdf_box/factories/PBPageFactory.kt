package ru.skytechdev.flutter_pdf_box.factories

import android.graphics.BitmapFactory
import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


data class Coords(val x: Int?, val y: Int?)
data class Dims(val height: Int?, val width: Int?)

class PBPageFactory {
    private val pdDocument: PDDocument;
    private val pdPage: PDPage;
    private val stream: PDPageContentStream;
    private lateinit var flutterAssets: FlutterPlugin.FlutterAssets

    private constructor(document: PDDocument, page: PDPage, appendContent: Boolean, flutterAssets: FlutterPlugin.FlutterAssets) {
        pdDocument = document;
        pdPage = page;
        stream = PDPageContentStream(document, page, appendContent, true, true);
        this.flutterAssets = flutterAssets
    }

    companion object {
        fun create(document: PDDocument?, call: Map<String, Any>?, flutterAssets: FlutterPlugin.FlutterAssets): PDPage? {
            if (document == null || call == null) return null;
            val page = PDPage()
            val factory = PBPageFactory(document, page, false, flutterAssets)
            factory.setMediaBox(call)
            factory.applyActions(call)
            factory.stream.close()
            return page
        }

        fun modify(document: PDDocument?, call: Map<String, Any>?, flutterAssets: FlutterPlugin.FlutterAssets): PDPage? {
            if (document == null || call == null) return null;
            val pageIndex: Int = call["pageIndex"] as Int ?: return null;
            Log.d("PDFBox", pageIndex.toString());
            val page = document.getPage(pageIndex)
            val factory = PBPageFactory(document, page, true, flutterAssets)

            factory.applyActions(call)
            factory.stream.close()
            return page
        }
    }

    private fun applyActions(call: Map<String, Any>) {
        val arr = call["actions"] as ArrayList<Map<String, Any>>;

        arr?.forEach {
            when (it["type"] as String) {
                "text" -> drawText(it)
                "image" -> drawImage(it)
                "rectangle" -> drawRectangle(it)
            }
        }
    }

    private fun setMediaBox(call: Map<String, Any>) {
        val map = call["mediaBox"] as Map<String, Any>;
        val cords: Coords? = map?.let { getCoords(it) }
        val dims: Dims? = map?.let { getDims(it) }
        pdPage.mediaBox = PDRectangle(cords?.x!!.toFloat(), cords?.y!!.toFloat(), dims?.width!!.toFloat(), dims?.height!!.toFloat())
    }

    private fun drawText(actions: Map<String, Any>) {
        val value: String = actions["value"] as String;
        val fontPath: String = actions["fontPath"] as String;
        val fontSize: Int = actions["fontSize"] as Int
        val coords: Coords = getCoords(actions)
        val rgbColor: IntArray? = hexStringToRGB(actions["color"] as String)
        val key: String = flutterAssets.getAssetFilePathByName(fontPath)
        val font: PDFont = PDType0Font.load(pdDocument, PDFBoxResourceLoader.getStream(key))
//        val font: PDFont = PDFont
        stream.beginText()
        stream.setNonStrokingColor(rgbColor?.get(0)!!, rgbColor[1], rgbColor[2])
        stream.setFont(font, fontSize.toFloat())
//        stream.setFont(null, fontSize.toFloat());
        stream.newLineAtOffset(coords.x!!.toFloat(), coords.y!!.toFloat())
        stream.showText(value)
        stream.endText()
    }

    private fun drawRectangle(actions: Map<String, Any>) {
        val coords: Coords = getCoords(actions)
        val dims: Dims = getDims(actions)
        val rgbColor: IntArray? = hexStringToRGB(actions["color"] as String)
        stream.addRect(coords.x!!.toFloat(), coords.y!!.toFloat(), dims.width!!.toFloat(), dims.height!!.toFloat())
        stream.setNonStrokingColor(rgbColor?.get(0)!!, rgbColor[1], rgbColor[2])
        stream.fill()
    }

    private fun drawImage(actions: Map<String, Any>) {
        val imageType: String = actions["imageType"] as String
        val imagePath: String = actions["imagePath"] as String
        val imageSource: String = actions["source"] as String
        val coords: Coords = getCoords(actions)
        val dims: Dims = getDims(actions)
        if (imageType == "jpg" || imageType == "png") {
            // Create PDImageXObject
            var image: PDImageXObject? = null
            if (imageSource == "path") {
                image = if (imageType == "jpg") {
                    val bmpImage = BitmapFactory.decodeFile(imagePath)
                    JPEGFactory.createFromImage(pdDocument, bmpImage)
                } else { // imageType.equals("png") == true
                    val `in`: InputStream = FileInputStream(File(imagePath))
                    val bmp = BitmapFactory.decodeStream(`in`)
                    LosslessFactory.createFromImage(pdDocument, bmp)
                }
            }
            if (imageSource == "assets") {
                val key: String = flutterAssets.getAssetFilePathByName(imagePath)
                val `is`: InputStream = PDFBoxResourceLoader.getStream(key)
                val bmp = BitmapFactory.decodeStream(`is`)
                image = LosslessFactory.createFromImage(pdDocument, bmp)
            }

            // Draw the PDImageXObject to the stream
            if (dims.height != null && dims.width != null) {
                stream.drawImage(image, coords.x!!.toFloat(), coords.y!!.toFloat(), dims.width!!.toFloat(), dims.height!!.toFloat())
            } else {
                stream.drawImage(image, coords.x!!.toFloat(), coords.y!!.toFloat())
            }
        }
    }

    private fun hexStringToRGB(hexString: String): IntArray? {
        val colorR = Integer.valueOf(hexString.substring(1, 3), 16)
        val colorG = Integer.valueOf(hexString.substring(3, 5), 16)
        val colorB = Integer.valueOf(hexString.substring(5, 7), 16)
        return intArrayOf(colorR, colorG, colorB)
    }

    private fun getCoords(map: Map<String, Any>): Coords {
        return Coords(
                map["x"] as Int?,
                map["y"] as Int?)
    }

    private fun getDims(map: Map<String, Any>): Dims {
        return Dims(
                map["height"] as Int?,
                map["width"] as Int?)
    }


}