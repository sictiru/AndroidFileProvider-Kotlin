package andreidan.fileprovider.kotlin

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by andrei on 5/15/17.
 */
class PermissionUtils {

    companion object {

        fun requestPermission(activity: Activity?, requestCode: Int, vararg permissions: String): Boolean {
            var granted: Boolean = true
            val permissionsNeeded: ArrayList<String> = ArrayList()

            permissions.forEach {
                if (activity == null) {
                    return false
                }
                val permissionCheck: Int = ContextCompat.checkSelfPermission(activity, it)
                val hasPermission: Boolean = (permissionCheck == PackageManager.PERMISSION_GRANTED)
                granted = granted && hasPermission
                if (!hasPermission) {
                    permissionsNeeded.add(it)
                }
            }

            if (granted) {
                return true
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!, permissionsNeeded.toTypedArray(), requestCode)
                return false
            }
        }

        fun permissionGranted(requestCode: Int, permissionCode: Int, grantResults: IntArray): Boolean {
            return requestCode == permissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

    }
}