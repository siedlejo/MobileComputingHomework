package com.siedler.jonah.mobilecomputinghomework.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.ui.reminder.AddReminderActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class HomeFragment : Fragment() {
    private lateinit var listViewComposable: ComposeView
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =  inflater.inflate(R.layout.fragment_home, container, false)

        listViewComposable = layout.findViewById(R.id.listViewComposable)

        fab = layout.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val addReminderActivity = Intent(context, AddReminderActivity::class.java)
            startActivity(addReminderActivity)
        }

        return layout
    }

    override fun onResume() {
        super.onResume()

        val reminderList = AppDB.getInstance().reminderDao().getAllReminder()
        listViewComposable.setContent { MessageList(messages = reminderList) }
    }

    @Composable
    fun MessageList(messages: List<Reminder>) {
        LazyColumn(
        ) {
            items(1) {
                messages.forEach { reminder ->
                    MessageRow(reminder)
                }
                Spacer(
                    modifier = Modifier.height(100.dp)
                )
            }
        }
    }

    @Composable
    fun MessageRow(reminder: Reminder) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
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
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(reminder.reminderTime),
                    Modifier
                        .weight(3f)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
                Text(
                    SimpleDateFormat("EEE, MMM d, yy", Locale.getDefault()).format(reminder.reminderTime),
                    Modifier.weight(2f)
                )
            }
        }
        Divider(color = Color.Black, thickness = 1.dp)
    }
}