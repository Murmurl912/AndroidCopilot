package com.example.androidcopilot.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AppSettingScreen() {

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {

        Text(text = "Chat",
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 6.dp),
            color = MaterialTheme.colorScheme.primary
        )

        SettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(text = "Chat Model")
            },
            subtitle = {
                Text("No api provided. Config your chat model api provider.")
            },
            onClick = {}
        )

        SettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(text = "Memory Size")
            },
            subtitle = {
                Text("4096 token per chat. Use model's context limit size")
            },
            onClick = {}
        )

        SettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(text = "Memory Type")
            },
            subtitle = {
                Text("Buffered memory, Android Copilot won't remember older messages that exceed memory size")
            },
            onClick = {}
        )

        SwitchSettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(text = "Custom Instruction")
            },
            subtitle = {
                Text("Custom instruction will be applied to all new chats.")
            },
            checked = false,
            onCheckedChange = {}
        )

        Text(text = "Function call",
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 6.dp),
            color = MaterialTheme.colorScheme.primary
        )

        SwitchSettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text("Enable Function Call")
            },
            subtitle = {
                Text("Allow Android Copilot to access internet and execute command on devices")
            },
            checked = false,
            onCheckedChange = {}
        )


        Text(text = "Speech",
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 6.dp),
            color = MaterialTheme.colorScheme.primary
        )

        SwitchSettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text("Read response when using keyboard input.")
            },
            subtitle = {
                Text("Android copilot will read it's response.")
            },
            checked = false,
            onCheckedChange = {}
        )

        SwitchSettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text("Read response when using speech input")
            },
            subtitle = {
                Text("Android copilot will read it's response when you are using speech input.")
            },
            checked = false,
            onCheckedChange = {}
        )

        Text(text = "App",
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 6.dp),
            color = MaterialTheme.colorScheme.primary
        )

        SwitchSettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text("Haptic Feedback")
            },
            subtitle = {
                Text("Provide haptic feedback when Android Copilot is responding")
            },
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit),
    onClick: () -> Unit,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    subtitle: (@Composable () -> Unit)? = null,
    subtitleTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingAction: (@Composable () -> Unit)? = null,
) {

    Row(modifier.clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        if (leadingIcon != null) {
            Box(
                Modifier
                    .padding(20.dp)
                    .height(IntrinsicSize.Max)) {
                leadingIcon()
            }
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
        Column(
            Modifier
                .padding(vertical = 12.dp)
                .weight(1F),
            verticalArrangement = Arrangement.Center) {

            ProvideTextStyle(value = titleTextStyle) {
                CompositionLocalProvider(
                    content = title
                )
            }
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                ProvideTextStyle(value = subtitleTextStyle) {
                    CompositionLocalProvider(
                        content = subtitle
                    )
                }
            }
        }
        if (trailingAction != null) {
            Box(
                Modifier
                    .padding(20.dp)
                    .height(IntrinsicSize.Max)
            ) {
                trailingAction()
            }
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
    }

}

@Composable
fun SwitchSettingItem(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit),
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    subtitle: (@Composable () -> Unit)? = null,
    subtitleTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    leadingIcon: (@Composable () -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    SettingItem(
        modifier = modifier,
        title = title,
        onClick = {
            onCheckedChange?.invoke(!checked)
        },
        titleTextStyle = titleTextStyle,
        subtitle = subtitle,
        subtitleTextStyle = subtitleTextStyle,
        leadingIcon = leadingIcon,
        trailingAction = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}


@Preview
@Composable
fun SwitchSettingItemPreview() {
    Column {
        SwitchSettingItem(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text("Function Call")
            },
            subtitle = {
                Text("Allow Android Copilot to access internet and execute command on devices")
            },
            checked = false,
            onCheckedChange = {}
        )
    }
}