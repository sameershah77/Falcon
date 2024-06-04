package com.example.falcon.fragments

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.falcon.R
import com.example.falcon.databinding.FragmentAssignTaskBinding
import com.example.falcon.databinding.FragmentProfileBinding
import com.example.falcon.model.Task
import com.example.falcon.view.AssignTaskActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date
import java.util.Locale


class AssignTaskFragment : BottomSheetDialogFragment()  {
    private lateinit var binding: FragmentAssignTaskBinding
    var priority:String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAssignTaskBinding.inflate(inflater, container, false)
        val view = binding.root
            binding.priorityRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val selectedRadioButton = view.findViewById<RadioButton>(checkedId) // Find RadioButton in dialog view
                priority = selectedRadioButton.text.toString()
            }

            binding.assignTaskSelectDate.setOnClickListener {
                // MaterialDatePicker ko initialize karo
                val builder = MaterialDatePicker.Builder.datePicker()
                // Apply custom style
                builder.setTheme(R.style.CustomMaterialDatePicker)
                // DatePickerDialog ke tarah, yaha par bhi set karein initial date
                val calendar = Calendar.getInstance()
                val initialDate = MaterialDatePicker.todayInUtcMilliseconds()
                builder.setSelection(initialDate)
                // MaterialDatePicker ke liye range set karein current date se pehle
                val constraintsBuilder = CalendarConstraints.Builder()
                constraintsBuilder.setValidator(DateValidatorPointForward.now())
                builder.setCalendarConstraints(constraintsBuilder.build())
                // MaterialDatePicker ko create karein
                val datePicker = builder.build()
                // Date select hone par callback set karein
                datePicker.addOnPositiveButtonClickListener { selection ->
                    selection.let {
                        // Yahan par date format karein jaise aap chahein
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = sdf.format(Date(it))
                        // Selected date ko use karne ke liye yahan par kuch aur code likhein
                        binding.assignTaskShowDate!!.text = formattedDate
                    }
                }
                // MaterialDatePicker ko show karein
                val fragManager = childFragmentManager
                datePicker.show(fragManager, "DATE_PICKER")
            }

        binding.saveTask.setOnClickListener {
            val date = binding.assignTaskShowDate.text.toString()
            val task = binding.assignTaskMakeTask.text.toString()

            if(priority.isEmpty() && date.isEmpty() && task.isEmpty()) {
                showToast(R.drawable.alert,"Please all feilds first",R.color.toast_yello)
            }
            else if(priority.isEmpty()) {
                showToast(R.drawable.alert,"Please select priority",R.color.toast_yello)
            }
            else if(date.isEmpty()) {
                showToast(R.drawable.alert,"Please select deadline",R.color.toast_yello)
            }
            else if(task.isEmpty()) {
                showToast(R.drawable.alert,"Please make a task",R.color.toast_yello)
            }
            else {
                val status = false
                val startDate = getCurrentDate()
                val task = Task(task, priority, date,startDate,status)

                // Call the callback function with the task data
                (activity as? AssignTaskActivity)?.taskCallback?.invoke(task)

                dismiss()
            }

        }
        return view
    }

    private fun getCurrentDate(): String {
        // Get the current date and time
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Format the date as a string
        return dateFormat.format(calendar.time)
    }

    private fun showToast(iconResId: Int, message: String, textColor: Int) {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.custom_toast, null)

        val iconImageView = toastView.findViewById<ImageView>(R.id.toast_icon)
        val messageTextView = toastView.findViewById<TextView>(R.id.toast_message)

        iconImageView.setImageResource(iconResId)
        messageTextView.text = message
        messageTextView.setTextColor(ContextCompat.getColor(requireContext(), textColor))

        val toast = Toast(requireContext())
        toast.view = toastView
        toast.setGravity(Gravity.BOTTOM, 0, 400)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}