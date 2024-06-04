import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.falcon.model.User
import com.example.falcon.repositories.UserRepository

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        repository.fetchUsers { User ->
            _userData.value = User as User
        }
    }

    fun addUser(user: User,callback: (Boolean) -> Unit) {
        repository.addUser(user) { success ->
            if (success) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun updateUser(user: User,callback: (Boolean) -> Unit) {
        repository.updateUser(user) { success ->
            if (success) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }
}
