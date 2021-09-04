package com.example.photostore.Adapter;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photostore.ModelClass.ModelData;
import com.example.photostore.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<ModelData> modelData;
    private Context context;

    public RecyclerAdapter(List<ModelData> modelData, Context context) {
        this.modelData = modelData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_data,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ModelData listData= modelData.get(position);
        Picasso.with(context)

                .load(listData.getImageurl())
                .placeholder(R.drawable.loader)
                .into(holder.imageView);
        holder.img_download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_download(listData.getImageurl());
            }
        });
        holder.img_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.with(context)
                        .load(listData.getImageurl())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("image/*");
                                intent.putExtra("android.intent.extra.STREAM", getlocalBitmapUri(bitmap));
                                context.startActivity(Intent.createChooser(intent, "share"));

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

            }
        });
    }

    public void file_download(String uRl) {

        Random r =new Random(10000);
        File direct = new File(Environment.getExternalStorageDirectory()
                + r.toString()+"/test_app");
        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Image Download")
                .setDescription("Image Downloade")
                .setDestinationInExternalPublicDir("/test_app", r.toString()+"test.jpg");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        mgr.enqueue(request);
    }
    private Uri getlocalBitmapUri(Bitmap bitmap) {

        Uri bmuri = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
            bmuri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return bmuri;

    }




    @Override
    public int getItemCount() {
        return modelData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView, img_download_button, img_share_button;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView =(ImageView)itemView.findViewById(R.id.image_view);
            img_download_button =(ImageView)itemView.findViewById(R.id.btn_download);
            img_share_button =(ImageView)itemView.findViewById(R.id.btn_share);
        }
    }
}