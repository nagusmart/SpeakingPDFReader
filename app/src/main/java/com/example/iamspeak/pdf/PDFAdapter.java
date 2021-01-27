package com.example.iamspeak.pdf;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iamspeak.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFAdapterViewHolder> {

    private Context context;
    private ArrayList<File> al_pdf;


    public PDFAdapter(Context context, ArrayList<File> al_pdf) {
        this.context = context;
        this.al_pdf = al_pdf;

    }


    @Override
    public int getItemCount() {
        return al_pdf.size();
    }

    @Override
    public PDFAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pdf_list, parent, false);
        return new PDFAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PDFAdapterViewHolder holder, int position) {
        File file = al_pdf.get(position);
        holder.txtName.setText(file.getName());
    }


    public class PDFAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_image)
        ImageView image;

        @BindView(R.id.tv_name)
        TextView txtName;

        @BindView(R.id.list)
        CardView list;


        PDFAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            list.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
          if(getAdapterPosition()!=-1){
              Intent intent = new Intent(context, PdfActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.putExtra("position", getAdapterPosition());
              context.startActivity(intent);
          }
        }
    }

}
