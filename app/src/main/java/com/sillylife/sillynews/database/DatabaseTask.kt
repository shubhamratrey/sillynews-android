package com.sillylife.sillynews.database

import android.os.AsyncTask
import android.util.Log

class DatabaseTask(var taskType: DatabaseTaskType, var dao: Any?, val listener: (Any?) -> Unit) : AsyncTask<Any, Void, Any>() {

    override fun doInBackground(vararg params: Any): Any? {
        var any: Any = params[0]
        when (taskType) {
            DatabaseTaskType.INSERT -> {

            }
            DatabaseTaskType.UPDATE -> {

            }
            DatabaseTaskType.GET -> {

            }
            DatabaseTaskType.DELETE -> {

            }
        }
        return -1
    }


    override fun onPostExecute(result: Any?) {
        var any: Any? = null
        when (taskType) {
            DatabaseTaskType.INSERT -> {

            }
            DatabaseTaskType.GET -> {

            }
            DatabaseTaskType.UPDATE -> {

            }
        }
        if (listener != null) {
            listener(any)
        }
    }

    override fun onPreExecute() {

    }

    override fun onProgressUpdate(vararg text: Void) {

    }
}