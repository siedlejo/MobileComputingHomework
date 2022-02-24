package com.siedler.jonah.mobilecomputinghomework.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.view.*
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.helper.notifications.NotificationHelper
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import com.siedler.jonah.mobilecomputinghomework.ui.reminder.AddReminderActivity
import com.siedler.jonah.mobilecomputinghomework.ui.reminder.REMINDER_INTENT_EXTRA_KEY
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

enum class SortingMode {
    ALPHABETICALLY, CREATION_TIME, REMINDER_TIME
}

class HomeFragment : Fragment() {
    private lateinit var listViewComposable: ComposeView
    private lateinit var fab: FloatingActionButton

    private var reminderList: List<Reminder> by Delegates.observable(ArrayList<Reminder>()) { _, _, new ->
        updateFilteredReminderList(new)
    }
    private val filteredReminderList = MutableLiveData<List<Reminder>>()
    private var sortingMode: SortingMode by Delegates.observable(SortingMode.REMINDER_TIME) { _, _, _ ->
        updateFilteredReminderList(this.reminderList)
    }
    private var showAllReminders: Boolean by Delegates.observable(false) { _, _, _ ->
        updateFilteredReminderList(this.reminderList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =  inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        listViewComposable = layout.findViewById(R.id.listViewComposable)
        listViewComposable.setContent { ReminderList() }

        fab = layout.findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            val addReminderActivity = Intent(context, AddReminderActivity::class.java)
            startActivity(addReminderActivity)
        }

        return layout
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reminder_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_all_reminders -> {
                this.showAllReminders = !this.showAllReminders
                true
            }
            R.id.sortAlphabetically -> {
                this.sortingMode = SortingMode.ALPHABETICALLY
                true
            }
            R.id.sortByCreationTime -> {
                this.sortingMode = SortingMode.CREATION_TIME
                true
            }
            R.id.sortByReminderTime -> {
                this.sortingMode = SortingMode.REMINDER_TIME
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        this.reminderList = getReminderListFromDB()
    }

    private fun updateFilteredReminderList(newReminderList: List<Reminder>) {
        val filteredList = applyFilter(newReminderList)
        val sortedList = applySortingMode(filteredList)
        filteredReminderList.postValue(sortedList)
    }

    private fun getReminderListFromDB(): List<Reminder> {
        return AppDB.getInstance().reminderDao().getAllReminderOfUser(AuthenticationProvider.getAuthenticatedUser()!!.userName)
    }

    private fun applySortingMode(reminderList: List<Reminder>): List<Reminder> {
        return when(this.sortingMode) {
            SortingMode.ALPHABETICALLY ->
                reminderList.sortedBy { it.message }
            SortingMode.CREATION_TIME ->
                reminderList.sortedBy { it.creationTime }
            SortingMode.REMINDER_TIME ->
                reminderList.sortedBy { it.reminderTime }
        }
    }

    private fun applyFilter(reminderList: List<Reminder>): List<Reminder> {
        return if (showAllReminders) {
            reminderList
        } else {
            reminderList.filter { !it.reminderSeen && it.reminderTime.time - Date().time < 0 }
        }
    }


    private fun removeReminder(reminder: Reminder) {
        // remove the list item
        val newList = reminderList.toMutableList()
        newList.remove(reminder)
        this.reminderList = newList

        // cancel the notification of this reminder
        NotificationHelper.cancelScheduledNotification(reminder.reminderId)
        NotificationHelper.removeNotification(reminder.reminderId)

        // remove the reminder from the database
        AppDB.getInstance().reminderDao().deleteReminder(reminder)
    }

    private fun editReminder(reminder: Reminder) {
        val addReminderActivity = Intent(context, AddReminderActivity::class.java)
        addReminderActivity.putExtra(REMINDER_INTENT_EXTRA_KEY, reminder.reminderId)
        startActivity(addReminderActivity)
    }

    private fun markAsSeen(reminder: Reminder) {
        reminder.reminderSeen = true

        // update the list
        val newList = reminderList.toMutableList()
        val reminderIndex = reminderList.withIndex()
            .first { it.value.reminderId == reminder.reminderId }
            .index
        newList[reminderIndex] = reminder
        this.reminderList = newList

        // update the db
        AppDB.getInstance().reminderDao().updateReminder(reminder)
    }

    private fun addCalendarEntry(reminder: Reminder) {
        val beginTime = Calendar.getInstance()
        beginTime.time = reminder.reminderTime

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(Events.CONTENT_URI)
            .putExtra(
                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                beginTime.timeInMillis
            )
            .putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                beginTime.timeInMillis + (3600000)
            )
            .putExtra(Events.TITLE, reminder.message)

        startActivity(intent)
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalUnitApi::class)
    @Composable
    fun ReminderList() {
        val items: List<Reminder>? by filteredReminderList.observeAsState()

        LazyColumn(
        ) {
            items(items?: emptyList(), key = {item: Reminder -> item.reminderId }){ reminder ->
                val dismissState = rememberDismissState()

                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                    removeReminder(reminder)
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
                    .clickable {
                        editReminder(reminder)
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    reminder.message,
                    Modifier.weight(6f)
                )
                Column(
                    Modifier
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
                Icon(
                    painterResource(id = if (showAllReminders) R.drawable.ic_calendar else R.drawable.ic_seen),
                    modifier = Modifier
                        .clickable {
                            if (showAllReminders) {
                                addCalendarEntry(reminder)
                            } else {
                                markAsSeen(reminder)
                            }
                        },
                    contentDescription = "Add a new event to the calendar",
                )
            }
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }
}