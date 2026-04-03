package com.jarvis.app.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun JarvisAppRoot(context: Context) {
    val vm: JarvisViewModel = viewModel(factory = JarvisViewModel.Factory(context))
    val messages by vm.messages.collectAsState()
    val state by vm.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Jarvis Asistente Personal", style = MaterialTheme.typography.headlineSmall)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Dashboard")
                Button(onClick = vm::updateSystem) { Text("Actualizar recursos") }
                Text(state.latestSystem)
                OutlinedTextField(value = "https://api.github.com", onValueChange = {}, enabled = false, label = { Text("API ejemplo") })
                Button(onClick = { vm.callPublicApi("https://api.github.com") }) { Text("Consultar API pública") }
                Text(state.apiSummary)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Música")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.searchMusic("Daft Punk") }) { Text("Buscar Daft Punk") }
                    Button(onClick = { vm.handleVoiceCommand("reproduce Queen") }) { Text("Comando de voz") }
                }
                Text(state.musicStatus)
            }
        }

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(messages) { msg ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("${msg.role}: ${msg.text}", Modifier.padding(10.dp))
                }
            }
        }

        OutlinedTextField(
            value = state.input,
            onValueChange = vm::onInputChanged,
            label = { Text("Escribe tu mensaje") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = vm::sendMessage) { Text("Enviar") }
            Button(onClick = vm::createPdfFromInput) { Text("Generar PDF") }
            Button(onClick = { vm.handleVoiceCommand("genera imagen paisaje nocturno") }) { Text("Generar imagen") }
        }
    }
}
