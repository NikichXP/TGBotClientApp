package com.nikichxp.tgbotapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nikichxp.tgbotapp.ui.theme.TGBotAppTheme
import kotlin.reflect.KProperty

class ServerConnectionConfig(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var token: String by SharedPrefsDataProvider(TOKEN_KEY, sharedPreferences)
    var url: String by SharedPrefsDataProvider(URL_KEY, sharedPreferences)

    fun isSetUp(): Boolean {
        return token.isNotEmpty() && url.isNotEmpty()
    }

    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }

    companion object {
        private const val TOKEN_KEY = "api_token"
        private const val URL_KEY = "remote_url"
    }
}

class SharedPrefsDataProvider(
    private val configKey: String,
    private val sharedPreferences: SharedPreferences
) {
    operator fun getValue(o: Any, property: KProperty<*>): String {
        return sharedPreferences.getString(configKey, "") ?: ""
    }

    operator fun setValue(o: Any, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(configKey, value).apply()
    }
}


// LoginViewModel
class LoginViewModel : ViewModel() {
    var token by mutableStateOf("")
        private set

    var url by mutableStateOf("")
        private set

    var isTokenVisible by mutableStateOf(false)
        private set

    fun updateToken(newToken: String) {
        token = newToken
    }

    fun updateUrl(newUrl: String){
        url = newUrl
    }

    fun toggleTokenVisibility() {
        isTokenVisible = !isTokenVisible
    }

    fun saveToken(serverConnectionConfig: ServerConnectionConfig) {
        serverConnectionConfig.token = token
    }

    fun saveUrl(serverConnectionConfig: ServerConnectionConfig) {
        serverConnectionConfig.url = url
    }
}

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val serverConnectionConfig = ServerConnectionConfig(this)
        if (serverConnectionConfig.isSetUp()) {
            startMainActivity()
            finish()
            return
        }

        setContent {
            TGBotAppTheme {
                LoginScreen()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val serverConnectionConfig = remember { ServerConnectionConfig(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your API token",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = viewModel.token,
                onValueChange = { viewModel.updateToken(it) },
                label = { Text("API Token") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.isTokenVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleTokenVisibility() }) {
                        Text(if (viewModel.isTokenVisible) "Hide" else "Show")
                    }
                }
            )

            OutlinedTextField(
                value = viewModel.url,
                onValueChange = { viewModel.updateUrl(it) },
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viewModel.token.isBlank()) {
                        Toast.makeText(context, "Please enter a token", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveToken(serverConnectionConfig)
                        viewModel.saveUrl(serverConnectionConfig)

                        Toast.makeText(context, "Token saved", Toast.LENGTH_SHORT).show()

                        // Navigate to MainActivity
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        (context as? LoginActivity)?.finish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
    }
}