package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FamilyAlbumAdapter extends RecyclerView.Adapter<FamilyAlbumAdapter.ViewHolder> {
        private List<FamilyAlbumItem> Image;

        public FamilyAlbumAdapter( List<FamilyAlbumItem> image1) {
            Image = image1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate view (list_item)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);


            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

           final FamilyAlbumItem image = Image.get(position);

           holder.imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   Intent imageIntent = new Intent(v.getContext(), ImageFull.class);
                   imageIntent.putExtra("url", image.getImageUrl());
                   v.getContext().startActivity(imageIntent);

               }
           });

           if (image!=null) {
               Picasso.get()
                       .load(image.getImageUrl())
                       .placeholder(R.mipmap.img)
                       .fit()
                       .centerCrop()
                       .into(holder.imageView);
           }
        }

        @Override
        public int getItemCount() {
            return Image.size();
        }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_upload);

        }
    }
    }

