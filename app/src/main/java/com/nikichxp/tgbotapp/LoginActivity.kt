package com.nikichxp.tgbotapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nikichxp.tgbotapp.ui.theme.TGBotAppTheme

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
@Preview
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    serverInfoViewModel: ServerInfoViewModel = viewModel()
) {

    val context = LocalContext.current
    val serverConnectionConfig = remember { ServerConnectionConfig(context) }
    val url = remember { serverConnectionConfig.url }

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
                value = serverInfoViewModel.token,
                onValueChange = { serverInfoViewModel.updateToken(it) },
                label = { Text("API Token") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (loginViewModel.isTokenVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { loginViewModel.toggleTokenVisibility() }) {
                        Text(if (loginViewModel.isTokenVisible) "Hide" else "Show")
                    }
                }
            )

            OutlinedTextField(
                value = serverInfoViewModel.url,
                onValueChange = { serverInfoViewModel.updateUrl(it) },
                label = { Text("URL") },
                placeholder = {
                    Log.i("tag", "placeholder is: ${serverInfoViewModel.url}")
                    Text(serverInfoViewModel.url)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (serverInfoViewModel.token.isBlank()) {
                        Toast.makeText(context, "Please enter a token", Toast.LENGTH_SHORT).show()
                    } else {
                        serverInfoViewModel.saveToken(serverConnectionConfig)
                        serverInfoViewModel.saveUrl(serverConnectionConfig)

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