package com.siedler.jonah.mobilecomputinghomework.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import com.siedler.jonah.mobilecomputinghomework.ui.reminder.AddReminderActivity
import com.siedler.jonah.mobilecomputinghomework.ui.reminder.REMINDER_INTENT_EXTRA_KEY
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var listViewComposable: ComposeView
    private lateinit var fab: FloatingActionButton

    private val reminderList = MutableLiveData<List<Reminder>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =  inflater.inflate(R.layout.fragment_home, container, false)

        listViewComposable = layout.findViewById(R.id.listViewComposable)
        listViewComposable.setContent { ReminderList() }

        fab = layout.findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            val addReminderActivity = Intent(context, AddReminderActivity::class.java)
            startActivity(addReminderActivity)
        }

        return layout
    }

    override fun onResume() {
        super.onResume()

        reminderList.postValue(AppDB.getInstance().reminderDao().getAllReminderOfUser(AuthenticationProvider.getAuthenticatedUser().userName))
    }

    private fun removeReminderFromDB(reminder: Reminder) {
        val newList = reminderList.value?.toMutableList() ?: emptyList<Reminder>().toMutableList()
        newList.remove(reminder)
        reminderList.postValue(newList)
        AppDB.getInstance().reminderDao().deleteReminder(reminder)
    }

    private fun editReminder(reminder: Reminder) {
        val addReminderActivity = Intent(context, AddReminderActivity::class.java)
        addReminderActivity.putExtra(REMINDER_INTENT_EXTRA_KEY, reminder.reminderId)
        startActivity(addReminderActivity)
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalUnitApi::class)
    @Composable
    fun ReminderList() {
        val items: List<Reminder>? by reminderList.observeAsState()

        LazyColumn(
        ) {
            items(items?: emptyList(), key = {item: Reminder -> item.reminderId }){ reminder ->
                val dismissState = rememberDismissState()

                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                    removeReminderFromDB(reminder)
                }

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { FractionalThreshold(0.4f) },
                    background = {
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 1f else 1.25f
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Icon",
                                modifier = Modifier
                                    .scale(scale)
                                    .padding(10.dp, 0.dp)
                            )
                        }
                    },
                    dismissContent = {
                        Card(
                            elevation = animateDpAsState(
                                if (dismissState.dismissDirection != null) 4.dp else 0.dp
                            ).value,
                            shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = Alignment.CenterVertically)
                        ) {
                            Row(
                                modifier= Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                ReminderRow(reminder)
                            }
                        }
                    }
                )
            }
            items(1) {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    @Composable
    fun ReminderRow(reminder: Reminder) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editReminder(reminder) }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    reminder.message,
                    Modifier.weight(6f)
                )
                Column(
                    Modifier
                        .alignByBaseline()
                        .height(60.dp)
                        .weight(3f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(reminder.reminderTime),
                        Modifier
                            .weight(3f)
                            .fillMaxHeight(),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp
                    )
                    Text(
                        SimpleDateFormat(
                            "EEE, MMM d, yy",
                            Locale.getDefault()
                        ).format(reminder.reminderTime),
                        Modifier.weight(2f)
                    )
                }
            }
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }
}