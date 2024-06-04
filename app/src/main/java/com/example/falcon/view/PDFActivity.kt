package com.example.falcon.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.core.net.toUri
import com.example.falcon.R
import com.github.barteksc.pdfviewer.PDFView

class PDFActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfactivity)
        val name = intent.getStringExtra("pdf_name")!!
        val url = intent.getStringExtra("pdf_url")!!

        val pdfView: PDFView = findViewById(R.id.pdfView)

        Log.d("ionedn","${name},  ${url}")
        pdfView.fromUri(Uri.parse(url)).load()
    }
}