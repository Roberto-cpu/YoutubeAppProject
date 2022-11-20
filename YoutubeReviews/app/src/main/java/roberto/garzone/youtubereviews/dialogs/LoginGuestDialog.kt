package roberto.garzone.youtubereviews.dialogs

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import roberto.garzone.youtubereviews.R

/**
 * This class manages the login guest dialog behaviour
 */
class LoginGuestDialog : AppCompatDialogFragment() {

    // Instance variable
    private lateinit var listener : LoginGuestDialogInterface

    /**
     * This method creates the dialog layout (-, - = dialog, which)
     * @param savedInstanceState : Bundle
     * @return Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(resources.getString(R.string.login_guest_dialog_title))
            .setMessage(resources.getString(R.string.login_guest_dialog_message))
            .setPositiveButton(resources.getString(R.string.dialog_yes)) { _, _ ->
                listener.onYesClicked()
            }
            .setNegativeButton(resources.getString(R.string.dialog_no)) { _, _ -> }

        return builder.create()
    }

    /**
     * This method links the dialog page to the relative context
     * @param context : Context
     */
    override fun onAttach(context : Context) {
        super.onAttach(context)

        try {
            listener = context as LoginGuestDialogInterface
        } catch (e : java.lang.ClassCastException) {
            throw java.lang.ClassCastException("$context must be implemented LoginGuestDialogInterface")
        }
    }

    /**
     * This interface defines the dialog functionality
     */
    interface LoginGuestDialogInterface {
        fun onYesClicked()
    }
}