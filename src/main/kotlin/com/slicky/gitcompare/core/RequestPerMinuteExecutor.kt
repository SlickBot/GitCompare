package com.slicky.gitcompare.core

import javafx.beans.property.Property
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Created by SlickyPC on 28.5.2017
 *
 * The "meh" implementation with sleeping (not good).
 */
class RequestPerMinuteExecutor(var rpmProperty: Property<Number>, name: String = "DelayedExecutor", isDaemon: Boolean = false) {

    private val executor = Executors.newSingleThreadExecutor {
        Thread(it).apply {
            this.name = name
            this.isDaemon = isDaemon
        }
    }

    fun <T> submit(op: () -> T): Future<T> {
        return executor.submit<T> {
            try {
                TimeUnit.MILLISECONDS.sleep(rpmProperty.value.toWaitTimeMillis())
            } catch(ignored: Exception) {}
            op.invoke()
        }
    }

    fun shutdown() = executor.shutdown()
    fun shutdownNow(): MutableList<Runnable> = executor.shutdownNow()

    private fun Number.toWaitTimeMillis() = (60 / toDouble() * 1000).toLong()

}