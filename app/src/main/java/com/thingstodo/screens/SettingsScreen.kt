package com.thingstodo.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.thingstodo.MainActivity
import com.thingstodo.ui.AppTheme
import com.thingstodo.utils.SharedPreferencesUtil
import com.thingstodo.utils.SharedPreferencesUtil.DELETE_KEY

@Preview
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen {}
    }
}

@Composable
fun SettingsScreen(
    updateCurrentOptionItemList: (Context) -> Unit
) {
    val context = LocalContext.current

    var isDarkModeChecked by remember { mutableStateOf(SharedPreferencesUtil.isDarkModeMap(context)) }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enable dark mode for map",
                color = Color.White
            )
            Checkbox(
                checked = isDarkModeChecked,
                onCheckedChange = {
                    SharedPreferencesUtil.setDarkModeMap(context, it)
                    isDarkModeChecked = it
                }
            )
        }

        Button(
            onClick = {
                val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                with(sharedPreferences.edit()) {
                    putStringSet(DELETE_KEY, setOf())
                    apply()
                }

                updateCurrentOptionItemList(context)

                Toast.makeText(context, "Activity list has been reset", Toast.LENGTH_LONG).show()
            }
        ) {
            Text("Reset activity list")
        }
//        AndroidView(
//            modifier = Modifier.fillMaxWidth(),
//            factory = { context ->
//                AdView(context).apply {
//                    setAdSize(AdSize.BANNER)
//                    adUnitId = context.getString(R.string.ad_id_banner)
//                    loadAd(AdRequest.Builder().build())
//                }
//            }
//        )
    }
}
