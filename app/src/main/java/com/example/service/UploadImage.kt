package com.example.service

import android.os.AsyncTask
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL
import java.util.*

class UploadImage(private val delegate: AsyncResponse) : AsyncTask<String, Any, Any>() {

    override fun doInBackground(vararg params: String?): JSONObject {
        val rightNow = Calendar.getInstance()
        val date = "%d-%02d-%02d".format(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH) + 1, rightNow.get(
            Calendar.DAY_OF_MONTH))
        return try {
            val apiResponse = URL("https://space-time-12b72.firebaseapp.com/%s.json".format(date)).readText()
            JSONObject(apiResponse)
        }catch (e: FileNotFoundException){
            JSONObject("{\"error\" : \"file not found \"}")
        }
    }

    override fun onPostExecute(result: Any) {
        delegate.processFinish(result)
    }
}