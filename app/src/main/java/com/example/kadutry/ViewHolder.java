package com.example.kadutry;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewHolder extends RecyclerView.ViewHolder {
View mView;
TextView mTitleTv;
ImageView mImageIv;
TextView mDetailTv;
    public ViewHolder(View itemView)
    {

        super(itemView);
        mView=itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemlongClick(view,getAdapterPosition());
                return true;
            }
        });
    }
    public void setDetails(Context ctx, String title, String description, String image){
        TextView mTitleView=mView.findViewById(R.id.rTitleTv);
        TextView mDetailTv=mView.findViewById(R.id.rDescriptionTv);
        ImageView mImageIv=mView.findViewById(R.id.rImageView);
        mTitleView.setText(title);
        mDetailTv.setText(description);
        Picasso.get().load(image).into(mImageIv);



    }
    private ViewHolder.ClickListener mClickListener;
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemlongClick(View view,int position);

    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener=clickListener;




    }
}
