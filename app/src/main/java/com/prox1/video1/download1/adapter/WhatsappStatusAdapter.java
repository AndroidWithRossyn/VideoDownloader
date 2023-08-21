package com.prox1.video1.download1.adapter;

import static com.prox1.video1.download1.util.Utils.RootDirectoryWhatsappShow;
import static com.prox1.video1.download1.util.Utils.createFileFolder;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prox1.video1.download1.R;
import com.prox1.video1.download1.databinding.ItemsWhatsappViewBinding;
import com.prox1.video1.download1.interfaces.FileListWhatsappClickInterface;
import com.prox1.video1.download1.model.WhatsappStatusModel;
import com.prox1.video1.download1.util.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WhatsappStatusAdapter extends RecyclerView.Adapter<WhatsappStatusAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WhatsappStatusModel> fileArrayList;
    private LayoutInflater layoutInflater;
    public String SaveFilePath = RootDirectoryWhatsappShow + "/";
    String fileName = "";
    ProgressDialog dialogProgress;
    private FileListWhatsappClickInterface fileListClickInterface;

    public WhatsappStatusAdapter(Context context, ArrayList<WhatsappStatusModel> files) {
        this.context = context;
        this.fileArrayList = files;
        initProgress();
    }

    public WhatsappStatusAdapter(Context context2, ArrayList<WhatsappStatusModel> arrayList, FileListWhatsappClickInterface fileListWhatsappClickInterface) {
        this.context = context2;
        this.fileArrayList = arrayList;
        this.fileListClickInterface = fileListWhatsappClickInterface;
        initProgress();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.items_whatsapp_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WhatsappStatusModel fileItem = fileArrayList.get(i);
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            viewHolder.binding.ivPlay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.ivPlay.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT > 29) {
            Glide.with(this.context).load(fileItem.getUri()).into(viewHolder.binding.pcw);
        } else {
            Glide.with(this.context).load(fileItem.getPath()).into(viewHolder.binding.pcw);
        }

        viewHolder.binding.tvDownload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                createFileFolder();
                if (Build.VERSION.SDK_INT > 29) {
                    try {
                        if (fileItem.getUri().toString().endsWith(".mp4")) {
                            fileName = "status_" + System.currentTimeMillis() + ".mp4";
                            new DownloadFileTask().execute(new String[]{fileItem.getUri().toString()});
                            Utils.setToast(WhatsappStatusAdapter.this.context, WhatsappStatusAdapter.this.context.getResources().getString(R.string.download_complete));
                            return;
                        }
                        fileName = "status_" + System.currentTimeMillis() + ".png";
                        new DownloadFileTask().execute(new String[]{fileItem.getUri().toString()});
                        Utils.setToast(WhatsappStatusAdapter.this.context, WhatsappStatusAdapter.this.context.getResources().getString(R.string.download_complete));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String path = fileItem.getPath();
                    String substring = path.substring(path.lastIndexOf("/") + 1);
                    try {
                        FileUtils.copyFileToDirectory(new File(path), new File(SaveFilePath));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    String substring2 = substring.substring(12);
                    MediaScannerConnection.scanFile(context, new String[]{new File(SaveFilePath + substring2).getAbsolutePath()}, new String[]{fileItem.getUri().toString().endsWith(".mp4") ? "video/*" : "image/*"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                        public void onMediaScannerConnected() {
                        }

                        public void onScanCompleted(String str, Uri uri) {
                        }
                    });
                    new File(SaveFilePath, substring).renameTo(new File(SaveFilePath, substring2));
                    Context context2 = context;
                    Toast.makeText(context2, context.getResources().getString(R.string.saved_to) + SaveFilePath + substring2, 1).show();
                }


                /*final String path = fileItem.getPath();
                String filename = path.substring(path.lastIndexOf("/") + 1);
                final File file = new File(path);
                File destFile = new File(SaveFilePath);
                try {
                    FileUtils.copyFileToDirectory(file, destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String fileNameChange = filename.substring(12);
                File newFile = new File(SaveFilePath + fileNameChange);
                String ContentType = "image/*";
                if (fileItem.getUri().toString().endsWith(".mp4")) {
                    ContentType = "video/*";
                } else {
                    ContentType = "image/*";
                }
                MediaScannerConnection.scanFile(context, new String[]{newFile.getAbsolutePath()}, new String[]{ContentType},
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            public void onMediaScannerConnected() {
                            }

                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });

                File from = new File(SaveFilePath, filename);
                File to = new File(SaveFilePath, fileNameChange);
                from.renameTo(to);

                Toast.makeText(context, context.getResources().getString(R.string.saved_to) + SaveFilePath + fileNameChange, Toast.LENGTH_LONG).show();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemsWhatsappViewBinding binding;

        public ViewHolder(ItemsWhatsappViewBinding mbinding) {
            super(mbinding.getRoot());
            this.binding = mbinding;
        }
    }

    public void initProgress() {
        ProgressDialog progressDialog = new ProgressDialog(this.context);
        this.dialogProgress = progressDialog;
        progressDialog.setProgressStyle(0);
        this.dialogProgress.setTitle("Saving");
        this.dialogProgress.setMessage("Saving. Please wait...");
        this.dialogProgress.setIndeterminate(true);
        this.dialogProgress.setCanceledOnTouchOutside(false);
    }

    class DownloadFileTask extends AsyncTask<String, String, String> {
        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... strArr) {
        }

        DownloadFileTask() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... strArr) {
            try {
                InputStream openInputStream = WhatsappStatusAdapter.this.context.getContentResolver().openInputStream(Uri.parse(strArr[0]));
                File file = new File(Utils.RootDirectoryWhatsappShow + File.separator + WhatsappStatusAdapter.this.fileName);
                file.setWritable(true, false);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = openInputStream.read(bArr);
                    if (read > 0) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileOutputStream.close();
                        openInputStream.close();
                        return null;
                    }
                }
            } catch (IOException e) {
                System.out.println("error in creating a file");
                e.printStackTrace();
                return null;
            }
        }

        public void onPostExecute(String str) {
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    Context access$000 = WhatsappStatusAdapter.this.context;
                    MediaScannerConnection.scanFile(access$000, new String[]{new File(Utils.RootDirectoryWhatsappShow + File.separator + WhatsappStatusAdapter.this.fileName).getAbsolutePath()}, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            onPostExecute(str);
                        }
                    });
                    return;
                }
                Context access$0002 = WhatsappStatusAdapter.this.context;
                access$0002.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(new File(Utils.RootDirectoryWhatsappShow + File.separator + WhatsappStatusAdapter.this.fileName))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            super.onCancelled();
        }
    }
}