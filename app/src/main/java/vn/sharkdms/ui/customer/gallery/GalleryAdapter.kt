package vn.sharkdms.ui.customer.gallery

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import vn.sharkdms.R

class GalleryAdapter(
    private var images: List<Bitmap>,
    var context: Context
) : BaseAdapter() {
    var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_add_gallery, parent, false)
        }
        val ivGallery = view?.findViewById<ImageView>(R.id.iv_add_gallery)
        ivGallery?.setImageBitmap(images[position])

        return view!!
    }

}