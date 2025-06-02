package com.lortey.humme.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.lortey.humme.R
import com.lortey.humme.getTranslation


//auto size text to be possibly biggest used in learn screen
@Composable
fun AutoSizeText(
    text: String,
    color:  Color,

) {
    val typography = MaterialTheme.typography
    var textStyle by remember {
        mutableStateOf(typography.displaySmall.copy())
    }
    Text(text = text,
        color = color,
        style = textStyle,
        onTextLayout = {textLayoutResult ->
            if((textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight)){
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            }
        },
        textAlign = TextAlign.Center
    )
}


//unified pop up used for example for settings
@Composable
fun PopUp(title:String = "", text:String = "", closeAction:()->Unit, visibility:Boolean, secondButton:(@Composable () -> Unit)? = null){

        Popup(
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
    ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = { closeAction() })
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.align(Alignment.Center)
                            .background(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.background
                            )
                            .clickable {}
                    ) {
                        if (title.length > 1) {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(3.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(5.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Button(onClick = {closeAction()}){
                                Text(
                                    getTranslation("Close"),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            if(secondButton != null){
                                secondButton()
                            }
                        }
                    }
                }

        }
}

@Composable
fun LoadingPopup(
    isShowing: Boolean,
    onDismissRequest: () -> Unit = {}
) {
    if (isShowing) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
/*not implemented
class IndexTracker<T>(var value: T)
var UniversalSelected: MutableList<Boolean> = mutableListOf()
@Composable
fun UniversalGrid(selectMode:Boolean, changeSelectModeTrue:() -> Unit, changeSelectModeFalse:() -> Unit, navController: NavController,items1:List<String>,
                  items2:List<String>? = null, onClickAction:() -> Unit, indexTracker: IndexTracker<Int> = IndexTracker(0), TrackIndex:Boolean = false,
                  deckTracker: IndexTracker<Deck> = IndexTracker(getDeck()), decks: List<Deck> = listOf()) {
    UniversalSelected = remember(items1.size) {
        MutableList(items1.size) { false }
    }
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    )
    {

        items(items1.size) { index ->
            Column(
                modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = false
                )
                .background(
                    if (UniversalSelected[index] == false) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.primary
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            UniversalSelected[index] = true
                            changeSelectModeTrue()
                        },
                        onTap = {
                            if (selectMode) {
                                UniversalSelected[index] =
                                    !UniversalSelected[index] //flips ones to zeroes and vice versa
                                if (UniversalSelected.count { it == true } == 0) {
                                    // if no more selected cards left stop select mode
                                    changeSelectModeFalse()
                                }
                            } else {
                                if (TrackIndex){
                                    indexTracker.value = index
                                }else{
                                    deckTracker.value = decks[index]
                                }
                                onClickAction()
                            }
                        }
                    )
                }
                .fillMaxWidth(1f / 2f)
                .height((250 / 3f).dp)
                .padding(10.dp)
            ) {
                Text(
                    text = items1[index],
                    color = if (UniversalSelected[index] == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (items2 != null) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (items2 != null) {
                    Text(
                        text = items2[index],
                        color = if (UniversalSelected[index] == false) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.padding(vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}*/
