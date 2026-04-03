package com.jarvis.app.pdf

import com.jarvis.app.system.AppStorage
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File

class PdfService(private val storage: AppStorage) {
    fun createPdf(title: String, content: String): File {
        storage.ensure()
        val output = File(storage.pdfs, "${title.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
        PDDocument().use { doc ->
            val page = PDPage()
            doc.addPage(page)
            PDPageContentStream(doc, page).use { stream ->
                stream.beginText()
                stream.setFont(PDType1Font.HELVETICA, 12f)
                stream.newLineAtOffset(40f, 700f)
                stream.showText(title)
                stream.newLineAtOffset(0f, -20f)
                stream.showText(content.take(1000))
                stream.endText()
            }
            doc.save(output)
        }
        return output
    }
}
