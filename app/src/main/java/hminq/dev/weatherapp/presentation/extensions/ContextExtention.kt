package hminq.dev.weatherapp.presentation.extensions

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri

/**
 * Opens URL in a specific app if installed, otherwise opens in browser
 */
fun Context.openUrlInApp(url: String, appPackageName: String? = null) {
    if (url.isBlank()) return

    // Check if specific app is installed
    if (!appPackageName.isNullOrEmpty() && isAppInstalled(appPackageName)) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                setPackage(appPackageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            return
        } catch (_: ActivityNotFoundException) {
            // App installed but can't handle this URL, fall through to browser
            openInBrowser(url)
        }
    }
    // Fallback: Open in browser
    openInBrowser(url)
}

/**
 * Opens URL in default browser
 */
fun Context.openInBrowser(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(this, "Cannot open this link", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Check if an app is installed
 */
fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.copyToClipboard(text: String, label: String = "Copied Email Address") {
    (getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Toast.makeText(this, "Copied Email Address", Toast.LENGTH_SHORT).show()
        }
    } ?: Toast.makeText(this, "Cannot copy to clipboard", Toast.LENGTH_SHORT).show()
}