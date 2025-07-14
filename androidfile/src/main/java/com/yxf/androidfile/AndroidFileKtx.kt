package com.yxf.androidfile

import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicInteger




private val handler = Handler(Looper.getMainLooper())

fun runOnMainThread(runnable: Runnable) {
    if (isOnMainThread()) {
        runnable.run()
    } else {
        handler.post(runnable)
    }
}

fun isOnMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}


private val nextLocalRequestCode = AtomicInteger()

internal fun <I, O> FragmentActivity.startContractForResult(
    contract: ActivityResultContract<I, O>,
    input: I,
    callback: ActivityResultCallback<O>
) {
    val key = "activity_rq_for_result#${nextLocalRequestCode.getAndIncrement()}"
    val registry = activityResultRegistry
    var launcher: ActivityResultLauncher<I>? = null
    val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (Lifecycle.Event.ON_DESTROY == event) {
                launcher?.unregister()
                lifecycle.removeObserver(this)
            }
        }
    }
    lifecycle.addObserver(observer)
    val newCallback = ActivityResultCallback<O> {
        launcher?.unregister()
        lifecycle.removeObserver(observer)
        callback.onActivityResult(it)
    }
    launcher = registry.register(key, contract, newCallback)
    launcher.launch(input)
}

internal fun String.fixPath(): String {
    var result = this.trim()
    if (result.startsWith("/")) {
        result = result.substringAfter("/")
    }
    if (result.endsWith("/")) {
        result = result.substringBeforeLast("/")
    }
    return result
}

internal fun String.appendPath(child: String): String {
    val parent = fixPath()
    if (parent.isNullOrEmpty()) {
        return child.fixPath()
    }
    val append = child.fixPath()
    if (append.isNullOrEmpty()) {
        return parent
    }
    return "${parent}/${append}"
}

internal fun String.addPathSuffix(): String {
    var result = this.trim()
    if (!result.endsWith("/")) {
        result += "/"
    }
    return result
}

internal fun String.addPathPrefix(): String {
    var result = this.trim()
    if (!result.startsWith("/")) {
        result = "/${result}"
    }
    return result
}

internal fun String.getNameFromPath(): String {
    return substringAfterLast("/")
}

internal fun String.getParentFromPath(): String {
    return substringBeforeLast("/", "")
}
