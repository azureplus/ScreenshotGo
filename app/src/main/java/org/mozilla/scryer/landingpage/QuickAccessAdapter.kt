/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.scryer.landingpage

import android.app.Activity
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import org.mozilla.scryer.DetailPageActivity
import org.mozilla.scryer.R
import org.mozilla.scryer.persistence.ScreenshotModel
import java.io.File

class QuickAccessAdapter: RecyclerView.Adapter<ScreenshotItemHolder>() {
    var list: List<ScreenshotModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_screenshot, parent, false)

        val holder = ScreenshotItemHolder(view)
        holder.title = view.findViewById(R.id.title)
        holder.image = view.findViewById(R.id.image_view)
        holder.itemView.setOnClickListener { _ ->
            holder.adapterPosition.takeIf { position ->
                position != RecyclerView.NO_POSITION

            }?.let { position: Int ->
//                Toast.makeText(parent.context, "Item ${list[position].path} clicked",
//                        Toast.LENGTH_SHORT).show()
                val intent = Intent(parent.context, DetailPageActivity::class.java)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        parent.context as Activity, holder.image!!, ViewCompat.getTransitionName(holder.image))
                val bundle = options.toBundle()
                intent.putExtra("path", list[position].path)
                (parent.context as AppCompatActivity).startActivity(intent, bundle)
            }
        }
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ScreenshotItemHolder, position: Int) {
        val path = list[position].path
        val fileName = path.substring(path.lastIndexOf(File.separator) + 1)
        holder.title?.text = fileName
        holder.image?.let {
            Glide.with(holder.itemView.context)
                    .load(File(list[position].path)).into(it)
        }
    }

    override fun onViewRecycled(holder: ScreenshotItemHolder) {
        super.onViewRecycled(holder)
        holder.image?.let {
            Glide.with(holder.itemView.context).clear(it)
        }
    }
}