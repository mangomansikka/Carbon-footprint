package fi.metropolia.canopy.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Utility class to manage and check application permissions.
 */
class PermissionManager {

    companion object {
        /**
         * List of permissions required for core app functionality (Location and Activity Recognition).
         */
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    /**
     * Checks if all [REQUIRED_PERMISSIONS] have been granted.
     */
    fun hasAllPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}

/**
 * A Compose-friendly helper that provides a launcher for requesting required permissions.
 *
 * @param onPermissionsGranted Callback invoked when all permissions are granted.
 * @param onPermissionsDenied Callback invoked if any permission is denied.
 * @return A lambda function that initiates the permission check/request process.
 */
@Composable
fun rememberPermissionLauncher(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
): () -> Unit {

    val context = LocalContext.current
    val permissionManager = PermissionManager()

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted = PermissionManager.REQUIRED_PERMISSIONS.all {
                permissions[it] == true
            }

            if (granted) onPermissionsGranted()
            else onPermissionsDenied()
        }

    return {
        if (permissionManager.hasAllPermissions(context)) {
            onPermissionsGranted()
        } else {
            launcher.launch(PermissionManager.REQUIRED_PERMISSIONS)
        }
    }
}
