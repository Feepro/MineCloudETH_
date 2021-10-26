package com.chopchop.minecloud

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.InputStream
import android.graphics.drawable.VectorDrawable

import android.graphics.BitmapFactory

import android.graphics.drawable.BitmapDrawable

import androidx.core.content.ContextCompat

import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import java.lang.IllegalArgumentException


object Utils {
    fun getVideoDuration(context:Context,path: String): Long {
        val retriever =  MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(File(path)));
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release()
        return time!!.toLong();
    }


    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Recorder_app" + File.separator

            val folder = File(path)
            if (!folder.exists())
                folder.mkdirs()

            return path
        }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun getBitmap(vectorDrawable: VectorDrawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        vectorDrawable.draw(canvas)
        return bitmap
    }

     fun getBitmap(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            BitmapFactory.decodeResource(context.resources, drawableId)
        } else if (drawable is VectorDrawable) {
            getBitmap(drawable)
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }
    fun saveBalance(context: Context, balance:Double ){
        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        prefsEditor.putFloat("balance", balance.toFloat())
        prefsEditor.apply()
    }
    fun getBalance(context: Context): Double {

        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        return mPrefs.getFloat("balance", 0.00000001f).toDouble()
    }

    fun saveUserId(context: Context, userId:Int ){
        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        prefsEditor.putInt("userId", userId)
        prefsEditor.apply()
    }
    fun getUserId(context: Context): Int {

        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        return mPrefs.getInt("userId", -1)
    }

    fun addSub(context: Context, userId:Int ): Boolean {
        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        val subs = mPrefs.getStringSet("subs",null)
        val newSubs : MutableList<String> = if(subs != null)
             subs.toMutableList()
        else
            ArrayList()

        return if(newSubs.contains(userId.toString()))
            false
        else{
            newSubs.add(userId.toString())
            prefsEditor.putStringSet("subs",newSubs.toSet())
            prefsEditor.apply()
            true
        }
    }
    fun addMySubs(context: Context,mySubs:Int){
        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        prefsEditor.putInt("mySubs", mySubs)
        prefsEditor.apply()
    }
    fun getMySubs(context: Context): Int {

        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        return mPrefs.getInt("mySubs", 0)
    }
    fun saveRazgon(context: Context, razgon: Float){
        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        prefsEditor.putFloat("razgon", razgon)
        prefsEditor.apply()
    }
    fun getRazgon(context: Context): Float {

        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        return mPrefs.getFloat("razgon", 1f)
    }
    fun saveRubBalance(context: Context, rubBalance: Float){
        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        prefsEditor.putFloat("rubBalance", rubBalance)
        prefsEditor.apply()
    }
    fun getRubBalance(context: Context): Float {

        val mPrefs: SharedPreferences = context.getSharedPreferences("SPData", Context.MODE_PRIVATE)
        return mPrefs.getFloat("rubBalance", 0f)
    }
}