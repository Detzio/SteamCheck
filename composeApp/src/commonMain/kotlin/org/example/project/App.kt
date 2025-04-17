package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.ui.graphics.painter.Painter

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.compose_multiplatform
import kotlinproject.composeapp.generated.resources.pp
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ProfileCard(
    name: String,
    title: String,
    painterResource: Painter,
) {
    var isBioVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        backgroundColor = Color.White,
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource,
                contentDescription = null,
            )
            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = Color.Gray,
            )
            Button(
                modifier = Modifier
                    .padding(horizontal = 56.dp)
                    .fillMaxWidth(),
                onClick = { sendMessage() },
            ) {
                Text(text = "Envoyer un message")
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 8.dp),
                onClick = { isBioVisible = !isBioVisible },
            ) {
                Text(text = if (isBioVisible) "Afficher moins" else "Afficher plus")
            }
            AnimatedVisibility(visible = isBioVisible) {
                Text(
                    text = "Biographie : Passionné par le développement logiciel et les nouvelles technologies.",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.DarkGray,
                )
            }
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        ProfileCard(
            name = "Dusséaux Thomas",
            title = "Software Engineer",
            painterResource = painterResource(Res.drawable.pp),
        )
    }
}
