package com.nikichxp.tgbotapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.JsonObject
import com.nikichxp.tgbotapp.ui.theme.TGBotAppTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("list")
    suspend fun getEvents(@Header("token") token: String): List<JsonObject>
}

class EventsViewModel : ViewModel() {
    private var lastBaseUrl = ""
    private lateinit var apiService: ApiService


    var events by mutableStateOf<List<JsonObject>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    suspend fun fetchEvents(token: String, url: String) {
        createFor(url)
        isLoading = true
        error = null
        try {
            events = apiService.getEvents(token)
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    private fun createFor(url: String) {
        if (lastBaseUrl != url) {
            apiService = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

    // Placeholder function for event type determination
    fun determineEventType(event: JsonObject): String {
        // TODO: Implement your custom logic here
        return event.toString()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if token exists, redirect to LoginActivity if it doesn't
        val serverConnectionConfig = ServerConnectionConfig(this)
        if (!serverConnectionConfig.isSetUp()) {
            startLoginActivity()
            finish()
            return
        }

        setContent {
            TGBotAppTheme {
                MainScreen()
            }
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainScreen(
    viewModel: EventsViewModel = viewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val serverConnectionConfig = remember { ServerConnectionConfig(context) }
    val token = remember { serverConnectionConfig.token }
    val url = remember { serverConnectionConfig.url }

    LaunchedEffect(Unit) {
        viewModel.fetchEvents(token, url)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Events") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    TextButton(onClick = {
                        serverConnectionConfig.clearToken()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        (context as? MainActivity)?.finish()
                    }) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { padding ->
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CircularProgressIndicator()
                }
            }

            viewModel.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Text("Error: ${viewModel.error}")
                    Button(onClick = {
                        scope.launch { viewModel.fetchEvents(token, url) }
                    }) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                EventsList(
                    events = viewModel.events,
                    determineEventType = viewModel::determineEventType,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun EventsList(
    events: List<JsonObject>,
    determineEventType: (JsonObject) -> String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventCard(event = event, determineEventType = determineEventType)
        }
    }
}

@Composable
fun EventCard(
    event: JsonObject,
    determineEventType: (JsonObject) -> String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Event",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = determineEventType(event),
                style = MaterialTheme.typography.bodyMedium
            )
            // Add more fields as needed
        }
    }
}