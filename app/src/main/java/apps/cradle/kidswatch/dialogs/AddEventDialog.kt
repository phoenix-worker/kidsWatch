package apps.cradle.kidswatch.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import apps.cradle.kidswatch.MainViewModel
import apps.cradle.kidswatch.adapters.EventIconsAdapter
import apps.cradle.kidswatch.databinding.DialogAddEventBinding
import java.util.Calendar
import java.util.Locale

class AddEventDialog : DialogFragment() {

    private lateinit var binding: DialogAddEventBinding
    private val addEventsDialogVM by viewModels<AddEventsDialogViewModel>()
    private val mainVM by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddEventBinding.inflate(inflater, container, false)
        binding.timePicker.setIs24HourView(true)
        binding.icons.adapter = EventIconsAdapter(addEventsDialogVM)
        (binding.icons.layoutManager as GridLayoutManager).spanCount = 4
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onStart() {
        super.onStart()
        addEventsDialogVM.icons.observe(viewLifecycleOwner) {
            (binding.icons.adapter as EventIconsAdapter).submitList(it)
        }
        binding.buttonSave.setOnClickListener {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.currentHour)
            calendar.set(Calendar.MINUTE, binding.timePicker.currentMinute)
            addEventsDialogVM.getSelectedIconFilename()?.let { iconFileName ->
                mainVM.saveNewEvent(
                    time = calendar.timeInMillis,
                    iconFileName = iconFileName
                )
            }
            dismiss()
        }
    }

}