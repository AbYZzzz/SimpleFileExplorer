package com.aj.android.simplefileexplorer;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<FileData>>, FileAdapter.onFileClick {

    public static final int FILE_LOADER_ID = 101;
    private static final String TAG = "MainActivity";

    private File fileDir;
    private FileAdapter fileAdapter;
    private RecyclerView fileRecycler;
    private TextView noFileFound;
    private LoaderManager loaderManager;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FileData> fileDataArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();

        loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(FILE_LOADER_ID, null, this);

    }

    void initialise() {
        fileDir = Environment.getExternalStorageDirectory();
        fileAdapter = new FileAdapter(ContextCompat.createDeviceProtectedStorageContext(this), this);
        fileRecycler = findViewById(R.id.fileRecycler);
        noFileFound = findViewById(R.id.noFileFound);
        layoutManager = new LinearLayoutManager(this);
        fileRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, getTheme()));        // separate item by a vertical line
        fileRecycler.addItemDecoration(itemDecoration);
        fileRecycler.setAdapter(fileAdapter);
    }

    @NonNull
    @Override
    public Loader<ArrayList<FileData>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<FileData>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad(); //Force an asynchronous load
            }
            @Nullable
            @Override
            public ArrayList<FileData> loadInBackground() {
                Log.d(TAG, "loadInBackground: called");
                fileDataArrayList.clear();
                File[] files = fileDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        fileDataArrayList.add(new FileData(file.getName(), file.getAbsolutePath()));
                    }
                } else return null;
                return fileDataArrayList;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<FileData>> loader, ArrayList<FileData> data) {
        if (data == null)
            noFileFound.setVisibility(View.VISIBLE);
        else
            noFileFound.setVisibility(View.GONE);
        fileAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<FileData>> loader) {
        fileAdapter.setData(null); //set adapter data to null when DestroyLoader is called
    }

    @Override
    public void onCLick(int position) {
        File clickedFile = new File(fileDataArrayList.get(position).getFileUri());
        if (clickedFile.isDirectory()) {
            fileDir = clickedFile;
            loaderManager.restartLoader(FILE_LOADER_ID, null, this); //load files and folder in Directory(fileDir)
        }
    }

    @Override
    public void onBackPressed() {
        if (fileDir.equals(new File(String.valueOf(Environment.getExternalStorageDirectory()))))
            super.onBackPressed();
        else {
            fileDir = fileDir.getParentFile();
            loaderManager.restartLoader(FILE_LOADER_ID, null, this); //go back to parent Folder
        }
    }
}