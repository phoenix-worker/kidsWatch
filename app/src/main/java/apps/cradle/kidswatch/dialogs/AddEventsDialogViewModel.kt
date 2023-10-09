package apps.cradle.kidswatch.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.cradle.kidswatch.App
import apps.cradle.kidswatch.adapters.EventIconsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddEventsDialogViewModel : ViewModel() {

    private val _icons = MutableLiveData<List<EventIconsAdapter.EventIcon>>()
    val icons: LiveData<List<EventIconsAdapter.EventIcon>> = _icons

    private fun loadAvailableIcons() {
        viewModelScope.launch(Dispatchers.IO) {
            App.getInstance().assets.list("icons")?.let { names ->
                _icons.postValue(
                    names.mapIndexed { index, name ->
                        EventIconsAdapter.EventIcon(
                            fileName = "icons/$name",
                            isSelected = index == 0
                        )
                    }
                )
            }
        }
    }

    fun selectEventIcon(eventIcon: EventIconsAdapter.EventIcon) {
        viewModelScope.launch(Dispatchers.IO) {
            _icons.postValue(
                _icons.value?.map {
                    EventIconsAdapter.EventIcon(
                        fileName = it.fileName,
                        isSelected = it.fileName == eventIcon.fileName
                    )
                }
            )
        }
    }

    fun getSelectedIconFilename(): String? {
        return _icons.value?.firstOrNull { it.isSelected }?.fileName
    }

    init {
        loadAvailableIcons()
    }

}