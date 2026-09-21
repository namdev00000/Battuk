package com.battuk.app.ui.family

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.battuk.app.viewmodel.FamilyViewModel

@Composable
fun FamilyMembersScreen(
    viewModel: FamilyViewModel,
    onAddMember: () -> Unit,
    onContinue: (() -> Unit)? = null
) {
    val members by viewModel.members.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Family Members", style = MaterialTheme.typography.headlineSmall)
        Text("Manage your family", style = MaterialTheme.typography.bodyMedium)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(members, key = { it.id }) { member ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (member.photoUri != null) {
                                AsyncImage(model = member.photoUri, contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape))
                            } else {
                                Icon(Icons.Filled.Person, contentDescription = null)
                            }
                        }
                        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(member.name, style = MaterialTheme.typography.titleMedium)
                            Text(member.relation, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (!member.isSelf) {
                            IconButton(onClick = { viewModel.deleteMember(member) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }

        Button(onClick = onAddMember, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("+ Add Family Member")
        }

        if (onContinue != null) {
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 8.dp)
            ) { Text("Continue") }
        }
    }
}
