package com.linkvalue.rxkotlinrssreader.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.linkvalue.rxkotlinrssreader.R
import com.linkvalue.rxkotlinrssreader.model.article.Article

import com.squareup.picasso.Picasso

import java.util.ArrayList

/**
 * Created by loic on 03/10/14.
 */
class RssItemsAdapter(mArticleList: List<Article>?, private val context: Context) : RecyclerView.Adapter<RssItemsAdapter.MyViewHolder>() {

    private var mArticleList: List<Article>? = null
//    private val targetList: MutableList<CustomTarget> //In order to keep strong references to Picasso Targets
    private var listener: OnItemClickListener? = null

    init {
        if (mArticleList == null) {
            this.mArticleList = ArrayList<Article>()
        } else {
            this.mArticleList = mArticleList
        }
//        targetList = ArrayList<CustomTarget>()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {

        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.article_item, viewGroup, false)

        return MyViewHolder(v)

    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val current = mArticleList!![position]
        val myViewHolder = holder as MyViewHolder
        val picUrl : String?
        if (!current.isRead && myViewHolder.cardView.getAlpha() !== NOT_READ_ALPHA) {
            myViewHolder.cardView.setAlpha(NOT_READ_ALPHA)
        } else if (current.isRead && myViewHolder.cardView.getAlpha() !== IS_READ_ALPHA) {
            myViewHolder.cardView.setAlpha(IS_READ_ALPHA)
        }
        myViewHolder.title.setText(current.title)
        //try to get from disk with id, if not available get from the internets thanks to cats
        picUrl = current.image
        if (!TextUtils.isEmpty(picUrl)) {
//            if (!CacheHelper.getPic(context, current.getId(), myViewHolder.image)) {
            /*When loading pics for the first time, pics can take some time to download
            and appear. Meanwhile recyclerview may display wrong image because of its recycling
            views process. In this case, we removed displayed pic.
             */
            if (myViewHolder.image.drawable != null) {
                myViewHolder.image.setImageBitmap(null)
            }
            //If pic is not available offline, let's try to download it
//            val customTarget = CustomTarget(current.getId(), myViewHolder.image)
//            targetList.add(customTarget)
            Picasso.with(context)
                    .load(Uri.parse(picUrl))
                    .into(myViewHolder.image)
//            }
        }        }

//    override fun onBindViewHolder(holder: Nothing?, position: Int) {
//        val current = mArticleList!![position]
//        val myViewHolder = holder as MyViewHolder
//        val picUrl : String?
//        if (!current.isRead && myViewHolder?.cardView.getAlpha() !== NOT_READ_ALPHA) {
//            myViewHolder.cardView.setAlpha(NOT_READ_ALPHA)
//        } else if (current.isRead && myViewHolder?.cardView.getAlpha() !== IS_READ_ALPHA) {
//            myViewHolder.cardView.setAlpha(IS_READ_ALPHA)
//        }
//        myViewHolder.title.setText(current.title)
//        //try to get from disk with id, if not available get from the internets thanks to cats
//        picUrl = current.image
//        if (!TextUtils.isEmpty(picUrl)) {
////            if (!CacheHelper.getPic(context, current.getId(), myViewHolder.image)) {
//            /*When loading pics for the first time, pics can take some time to download
//            and appear. Meanwhile recyclerview may display wrong image because of its recycling
//            views process. In this case, we removed displayed pic.
//             */
//            if (myViewHolder.image.drawable != null) {
//                myViewHolder.image.setImageBitmap(null)
//            }
//            //If pic is not available offline, let's try to download it
////            val customTarget = CustomTarget(current.getId(), myViewHolder.image)
////            targetList.add(customTarget)
//            Picasso.with(context)
//                    .load(Uri.parse(picUrl))
//                    .into(myViewHolder.image)
////            }
//        }    }

//    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
//
//        val current = mArticleList!![i]
//        val myViewHolder = viewHolder as MyViewHolder
//        val picUrl: String?
//        if (!current.isRead() && myViewHolder.cardView.getAlpha() !== NOT_READ_ALPHA) {
//            myViewHolder.cardView.setAlpha(NOT_READ_ALPHA)
//        } else if (current.isRead() && myViewHolder.cardView.getAlpha() !== IS_READ_ALPHA) {
//            myViewHolder.cardView.setAlpha(IS_READ_ALPHA)
//        }
//        myViewHolder.title.setText(current.getTitle())
//        //try to get from disk with id, if not available get from the internets thanks to cats
//        picUrl = current.image
//        if (!picUrl?.isEmpty()) {
////            if (!CacheHelper.getPic(context, current.getId(), myViewHolder.image)) {
//            /*When loading pics for the first time, pics can take some time to download
//            and appear. Meanwhile recyclerview may display wrong image because of its recycling
//            views process. In this case, we removed displayed pic.
//             */
//            if (myViewHolder.image.drawable != null) {
//                myViewHolder.image.setImageBitmap(null)
//            }
//            //If pic is not available offline, let's try to download it
////            val customTarget = CustomTarget(current.getId(), myViewHolder.image)
//            targetList.add(customTarget)
//            Picasso.with(context)
//                    .load(Uri.parse(picUrl))
//                    .into(customTarget)
////            }
//        }
//    }

    override fun getItemCount(): Int {
        return mArticleList!!.size
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class MyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context: Context? = null
        var image: ImageView
        var title: TextView
        val cardView: CardView

        init {
            image = itemView.findViewById(R.id.article_pic) as ImageView
            title = itemView.findViewById(R.id.article_title) as TextView
            cardView = itemView.findViewById(R.id.card_view) as CardView


            itemView.setOnClickListener { v ->
                //                    Toast.makeText(context, "Title is " + mArticleList.get(getPosition()).getTitle(), Toast.LENGTH_LONG).show();
                    listener?.onItemClick(v, adapterPosition)


            }


        }

    }


//    private inner class CustomTarget private constructor(private val picId: Long, var target: ImageView) : Target {
//        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//
//        }
//
//        override fun onBitmapFailed(errorDrawable: Drawable?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            target.setImageBitmap(bitmap)
//            //once bitmap has been set, we save it on disk for offline usage, this is the second cache
//            //after Picasso Lru's.
////            CacheHelper.savePic(context, bitmap, picId)
////            targetList.remove(this)
//        }
//
//
//        //
////        @Override
////        fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
////            target.setImageBitmap(bitmap)
////            //once bitmap has been set, we save it on disk for offline usage, this is the second cache
////            //after Picasso Lru's.
////            CacheHelper.savePic(context, bitmap, picId)
////            targetList.remove(this)
////        }
////
////        fun onBitmapFailed(errorDrawable: Drawable) {
////            targetList.remove(this)
////
////        }
////
////        fun onPrepareLoad(placeHolderDrawable: Drawable) {
////
////        }
//    }

    /**
     * As RecyclerView is still in beta, it does not apparently have any onclicklistener like Listview used to,
     * so let's create our own through the adapter
     */
    interface OnItemClickListener {

        fun onItemClick(view: View, position: Int)

    }

    companion object {

        val IS_READ_ALPHA : Float = 0.5f
        val NOT_READ_ALPHA : Float = 1f
    }

}
