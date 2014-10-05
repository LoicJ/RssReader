package fr.jacques.loic.rssreader.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import fr.jacques.loic.rssreader.R;
import fr.jacques.loic.rssreader.model.Article;
import fr.jacques.loic.rssreader.model.ArticleDBManager;
import fr.jacques.loic.rssreader.utils.CacheHelper;

/**
 * Created by loic on 03/10/14.
 */
public class RssItemsAdapter extends RecyclerView.Adapter {

    public static final float IS_READ_ALPHA = 0.5f;
    public static final float NOT_READ_ALPHA = 1f;

    private List<Article> mArticleList;
    private List<CustomTarget> targetList; //In order to keep strong references to Picasso Targets
    private Context context;
    private ArticleDBManager dbManager;
    private OnItemClickListener listener;

    public RssItemsAdapter(List<Article> mArticleList, Context context, ArticleDBManager dbManager) {
        if (mArticleList == null) {
            this.mArticleList = new ArrayList<Article>();
        } else {
            this.mArticleList = mArticleList;
        }

        this.context = context;
        this.dbManager = dbManager;
        targetList = new ArrayList<CustomTarget>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.article_item, viewGroup, false);

        return new MyViewHolder(v, context);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        final Article current = mArticleList.get(i);
        final MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        String picUrl;
        if (!current.isRead() && myViewHolder.cardView.getAlpha() != NOT_READ_ALPHA) {
            myViewHolder.cardView.setAlpha(NOT_READ_ALPHA);
        } else if (current.isRead() && myViewHolder.cardView.getAlpha() != IS_READ_ALPHA) {
            myViewHolder.cardView.setAlpha(IS_READ_ALPHA);
        }
        myViewHolder.title.setText(current.getTitle());
        //try to get from disk with id, if not available get from the internets thanks to cats
        if ((picUrl = current.getImage()) != null && !picUrl.isEmpty()) {
            if (!CacheHelper.getPic(context, current.getId(), myViewHolder.image)) {
                /*When loading pics for the first time, pics can take some time to download
                and appear. Meanwhile recyclerview may display wrong image because of its recycling
                views process. In this case, we removed displayed pic.
                 */
                if (myViewHolder.image.getDrawable() != null) {
                    myViewHolder.image.setImageBitmap(null);
                }
                //If pic is not available offline, let's try to download it
                CustomTarget customTarget = new CustomTarget(current.getId(), myViewHolder.image);
                targetList.add(customTarget);
                Picasso.with(context)
                        .load(Uri.parse(picUrl))
                        .into(customTarget);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        private MyViewHolder(View itemView, final Context context) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.article_pic);
            title = (TextView) itemView.findViewById(R.id.article_title);
            cardView = (CardView) itemView.findViewById(R.id.card_view);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "Title is " + mArticleList.get(getPosition()).getTitle(), Toast.LENGTH_LONG).show();
                    if (listener != null) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            image.setViewName("articlePic");
                            title.setViewName("articleTitle");
                        }
                        listener.onItemClick(v, getPosition());

                    }

                }
            });


        }

        public ImageView image;
        public TextView title;
        private CardView cardView;
    }


    private class CustomTarget implements Target {

        private long picId;
        public ImageView target;

        private CustomTarget(long picId, ImageView target) {
            this.picId = picId;
            this.target = target;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            target.setImageBitmap(bitmap);
            //once bitmap has been set, we save it on disk for offline usage, this is the second cache
            //after Picasso Lru's.
            CacheHelper.savePic(context, bitmap, picId);
            targetList.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            targetList.remove(this);

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    /**
     * As RecyclerView is still in beta, it does not apparently have any onclicklistener like Listview used to,
     * so let's create our own through the adapter
     */
    public interface OnItemClickListener {

        public void onItemClick(View view, int position);

    }

}
