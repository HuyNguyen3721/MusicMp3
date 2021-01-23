package com.example.appmusicmp3

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.glide.transformations.BlurTransformation

object Untils {
    @JvmStatic
    @BindingAdapter("setImage")
    fun setImage(iv : ImageView, uri : String){
        Glide.with(iv.context).load(uri).into(iv)
    }
    @JvmStatic
    @BindingAdapter("setText")
    fun setText(tv : TextView , content : String){
        tv.text =  content
    }
    @JvmStatic
    @BindingAdapter("setCrlImage")
    fun setCrlImage( iv : CircleImageView , uri : String){
        Glide.with(iv.context)
            .load(uri)
            .circleCrop()
            .into(iv);
    }
    @JvmStatic
    @BindingAdapter("setbg_image_blur")
    fun setbg_image_blur( iv : ImageView , uri : String){
        Glide.with(iv.context).load(uri).transform( BlurTransformation(35,3))
            .into(iv);
    }

}