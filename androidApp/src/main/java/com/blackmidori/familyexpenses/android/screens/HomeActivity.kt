package com.blackmidori.familyexpenses.android.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.Session

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar()
                    }
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        Body()
                    }
                }
            }
        }
    }

    @Composable
    fun TopBar() {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Home", Modifier.align(Alignment.Center))
        }
    }

    @Composable
    fun Body() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item { Text("User: " + Session.appUser.id) }
                item { Text("accessTokenExpirationDateTime: " + Session.appUser.tokens.accessTokenExpirationDateTime) }
                item { Text("accessToken: " + Session.appUser.tokens.accessToken) }
                item { Text("refreshToken: " + Session.appUser.tokens.refreshToken) }
            }
        }
    }

    @Preview
    @Composable
    fun Preview() {
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopBar()
                }
            ) { innerPadding ->
                Box(Modifier.padding(innerPadding)) {
                    Body()
                }
            }
        }
    }
}