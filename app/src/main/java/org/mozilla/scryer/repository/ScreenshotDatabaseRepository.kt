/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.scryer.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import kotlinx.coroutines.experimental.launch
import org.mozilla.scryer.R
import org.mozilla.scryer.persistence.CollectionModel
import org.mozilla.scryer.persistence.SuggestCollectionHelper
import org.mozilla.scryer.persistence.ScreenshotDatabase
import org.mozilla.scryer.persistence.ScreenshotModel

class ScreenshotDatabaseRepository(private val database: ScreenshotDatabase) : ScreenshotRepository {
    private var collectionListData = database.collectionDao().getCollections()
    private val screenshotListData = database.screenshotDao().getScreenshots()

    override fun addScreenshot(screenshots: List<ScreenshotModel>) {
        database.screenshotDao().addScreenshot(screenshots)
    }

    override fun updateScreenshot(screenshot: ScreenshotModel) {
        database.screenshotDao().updateScreenshot(screenshot)
    }

    override fun getScreenshot(screenshotId: String): ScreenshotModel? {
        return database.screenshotDao().getScreenshot(screenshotId)
    }

    override fun getScreenshots(collectionIds: List<String>): LiveData<List<ScreenshotModel>> {
        return database.screenshotDao().getScreenshots(collectionIds)
    }

    override fun getScreenshots(): LiveData<List<ScreenshotModel>> {
        return screenshotListData
    }

    override fun deleteScreenshot(screenshot: ScreenshotModel) {
        database.screenshotDao().deleteScreenshot(screenshot)
    }

    override fun getCollections(): LiveData<List<CollectionModel>> {
        return collectionListData
    }

    override fun getCollectionList(): List<CollectionModel> {
        return database.collectionDao().getCollectionList()
    }

    override fun addCollection(collection: CollectionModel) {
        database.collectionDao().addCollection(collection)
    }

    override fun getCollection(id: String): CollectionModel? {
        return database.collectionDao().getCollection(id)
    }

    override fun getCollectionCovers(): LiveData<Map<String, ScreenshotModel>> {
        return Transformations.switchMap(database.screenshotDao().getCollectionCovers()) { models ->
            MutableLiveData<Map<String, ScreenshotModel>>().apply {
                value = models.map { it.collectionId to it }.toMap()
            }
        }
    }

    override fun updateCollection(collection: CollectionModel) {
        database.collectionDao().updateCollection(collection)
    }

    override fun setupDefaultContent(context: Context) {
        launch {
            val none = CollectionModel(CollectionModel.CATEGORY_NONE,
                    context.getString(R.string.home_action_unsorted), 0, 0)
            addCollection(none)

            val nameList = listOf(R.string.sorting_suggestion_1st,
                    R.string.sorting_suggestion_2nd,
                    R.string.sorting_suggestion_3rd,
                    R.string.sorting_suggestion_4th,
                    R.string.sorting_suggestion_5th)

            if (nameList.size < SuggestCollectionHelper.suggestCollections.size) {
                throw RuntimeException("Not enough name for all suggestion collection")
            }

            SuggestCollectionHelper.suggestCollections.forEachIndexed { index, collection ->
                collection.name = context.getString(nameList[index])
                addCollection(collection)
            }
        }
    }

    override fun getScreenshotList(): List<ScreenshotModel> {
        return database.screenshotDao().getScreenshotList()
    }

    override fun getScreenshotList(collectionIds: List<String>): List<ScreenshotModel> {
        return database.screenshotDao().getScreenshotList(collectionIds)
    }

    override fun deleteCollection(collection: CollectionModel) {
        database.collectionDao().deleteCollection(collection)
    }

    override fun updateCollectionId(collection: CollectionModel, id: String) {
        database.collectionDao().updateCollectionId(collection, id)
    }
}
