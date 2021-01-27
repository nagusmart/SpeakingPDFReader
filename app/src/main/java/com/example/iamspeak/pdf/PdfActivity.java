package com.example.iamspeak.pdf;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iamspeak.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    private TextToSpeech textToSpeech;
    String TAG="PdfActivity";
    int position=-1;

    @BindView(R.id.start)
    TextView start;

    @BindView(R.id.stop)
    TextView stop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        pdfView= (PDFView)findViewById(R.id.pdfView);
        position = getIntent().getIntExtra("position",-1);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
        displayFromSdcard();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPdf();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textToSpeech !=null){
                    textToSpeech.stop();
                }
            }
        });
    }

    private void displayFromSdcard() {
        pdfFileName = PdfListActivity.fileList.get(position).getName();

        pdfView.fromFile(PdfListActivity.fileList.get(position))
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

//    @OnClick(R.id.start)
    void startPdf(){
        try {
            PdfReader pdfReader = new PdfReader(PdfListActivity.fileList.get(position).getPath());
            String stringParser = "";

            int n = pdfReader.getNumberOfPages();
            for (int i = 0; i <n ; i++) {
                if(pageNumber==i)
                stringParser   = stringParser+PdfTextExtractor.getTextFromPage(pdfReader, i+1).trim()+"\n"; //Extracting the content from the different pages
            }

            pdfReader.close();
            // textView.setText(stringParser);
            textToSpeech.speak(stringParser, TextToSpeech.QUEUE_FLUSH, null);
        }catch (Exception e){
            Log.e("error",e.toString());
        }
    }


    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
    @Override
    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}
