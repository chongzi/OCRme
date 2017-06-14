package com.ashomok.imagetotext.ocr_result.pdf_builder;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;

/**
 * Created by iuliia on 6/2/17.
 */

public class PDFBuilder {
    private Context context;

    public PDFBuilder(Context context) {
        this.context = context;
    }

    public void buildPDF() {
        // open a new document
        PrintedPdfDocument document = new PrintedPdfDocument(context, printAttributes);

// start a page
        PdfDocument.Page page = document.startPage(0);

// draw something on the page
        View content = getContentView();
        content.draw(page.getCanvas());

// finish the page
        document.finishPage(page);

// add more pages

// write the document content
        document.writeTo(getOutputStream());

//close the document
        document.close();
    }
}
