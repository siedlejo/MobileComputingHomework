package com.siedler.jonah.mobilecomputinghomework.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.ui.reminder.AddReminderActivity
import java.util.*

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
        listViewComposable.setContent { MessageList(getPlaceholderMessages()) }

        fab = layout.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val addReminderActivity = Intent(context, AddReminderActivity::class.java)
            startActivity(addReminderActivity)
        }

        return layout
    }

    @Composable
    fun MessageList(messages: List<Reminder>) {
        LazyColumn {
            items(1) {
                messages.forEach { reminder ->
                    MessageRow(reminder)
                }
            }
        }
    }

    @Composable
    fun MessageRow(reminder: Reminder) {
        Text(reminder.message)
    }

    private fun getPlaceholderMessages(): List<Reminder> {
        var reminderList: MutableList<Reminder> = LinkedList();
        for (i in 0..10) {
            reminderList.add(Reminder("$i"))
        }
        return reminderList
    }
}