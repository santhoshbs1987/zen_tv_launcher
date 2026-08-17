package com.ekshana.tv.launcher.data

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

data class TvRecommendation(
    val id: Long,
    val title: String,
    val description: String?,
    val packageName: String,
    val posterArtUri: Uri?,
    val intentUri: Uri?,
)

class TvRecommendationsRepository(private val context: Context) {

    /**
     * Reads "Watch Next" programs published by streaming apps (Netflix, YouTube, Prime Video, etc.)
     * and reactively updates via ContentObserver.
     */
    fun getWatchNextPrograms(): Flow<List<TvRecommendation>> = callbackFlow {
        val contentResolver = context.contentResolver
        val uri = TvContractCompat.WatchNextPrograms.CONTENT_URI

        fun queryPrograms(): List<TvRecommendation> {
            val list = mutableListOf<TvRecommendation>()
            val projection = arrayOf(
                TvContractCompat.WatchNextPrograms._ID,
                TvContractCompat.WatchNextPrograms.COLUMN_TITLE,
                TvContractCompat.WatchNextPrograms.COLUMN_SHORT_DESCRIPTION,
                TvContractCompat.WatchNextPrograms.COLUMN_PACKAGE_NAME,
                TvContractCompat.WatchNextPrograms.COLUMN_POSTER_ART_URI,
                TvContractCompat.WatchNextPrograms.COLUMN_INTENT_URI,
            )

            try {
                contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val program = WatchNextProgram.fromCursor(cursor)
                        list.add(
                            TvRecommendation(
                                id = program.id,
                                title = program.title ?: "Untitled",
                                description = program.description,
                                packageName = program.packageName ?: "",
                                posterArtUri = program.posterArtUri,
                                intentUri = program.intentUri,
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                // TVProvider might throw SecurityException or not be supported on bare emulators
            }
            return list
        }

        trySend(queryPrograms())

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySend(queryPrograms())
            }
        }

        try {
            contentResolver.registerContentObserver(uri, true, observer)
        } catch (_: Exception) {
            // Ignored if provider observer unavailable
        }

        awaitClose {
            try {
                contentResolver.unregisterContentObserver(observer)
            } catch (_: Exception) {}
        }
    }.flowOn(Dispatchers.IO)
}
