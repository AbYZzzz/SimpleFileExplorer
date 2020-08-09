package com.aj.android.simplefileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {
    private ArrayList<FileData> fileData = new ArrayList<>();
    private Context context;
    private onFileClick fileClick;

    FileAdapter(Context context, onFileClick fileClick) {
        this.context = context;
        this.fileClick = fileClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        File file = new File(fileData.get(position).getFileUri());
        holder.fileName.setText(fileData.get(position).getFileName());
        if (file.isDirectory()) {
            holder.fileSize.setVisibility(View.GONE);
            holder.fileIcon.setImageDrawable(context.getDrawable(R.drawable.ic_folder)); //change icon of the item view to folder icon if it is a Directory
        } else {
            holder.fileSize.setVisibility(View.VISIBLE);
            holder.fileSize.setText(sizeConverter(file.length()));
            holder.fileIcon.setImageDrawable(context.getDrawable(R.drawable.ic_file)); //change icon of the item view to file icon
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileClick.onCLick(position);//call onClick() method in MainActivity with position as argu
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileData == null ? 0 : fileData.size();
    }

    //set Data to the Adapter
    public void setData(ArrayList<FileData> fileData) {
        this.fileData = fileData;
        this.notifyDataSetChanged();
    }

    //Converts bytes to KiloBytes, MegaBytes, GigaBytes
    public String sizeConverter(long size) {
        DecimalFormat size_format = new DecimalFormat();
        size_format.setMaximumFractionDigits(2);
        if (size >= 1000000000)
            return size_format.format((Float.parseFloat(String.valueOf(size))) / 1000000000) + "GB";
        else if (size >= 1000000)
            return size_format.format((Float.parseFloat(String.valueOf(size))) / 1000000) + "MB";
        else if (size >= 1000)
            return size_format.format((Float.parseFloat(String.valueOf(size))) / 1000) + "KB";
        else
            return size_format.format(Float.parseFloat(String.valueOf(size))) + "B";
    }

    //interface for on click callback
    public interface onFileClick {
        void onCLick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView fileIcon;
        private TextView fileName, fileSize;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            view = itemView;
        }
    }
}
