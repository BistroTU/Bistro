package edu.temple.bistro

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun OfflineRedirector(
    cm: ConnectivityManager,
    redirectComposable: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val isOnline = remember { mutableStateOf(false) }
    LaunchedEffect(cm) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                isOnline.value = false
            }

            override fun onUnavailable() {
                isOnline.value = false
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                isOnline.value = true
            }

            override fun onAvailable(network: Network) {
                isOnline.value = true
            }
        }
        cm.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
    }
    if (isOnline.value || cm.activeNetworkInfo?.isConnected ?: false) {
        content()
    } else {
        redirectComposable()
    }
}