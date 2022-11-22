package roberto.garzone.youtubereviews.dialogs

/**
 * @authors: Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date: 8/11/2022
 */

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import roberto.garzone.youtubereviews.R

class ProfileImageEmailDialog : AppCompatDialogFragment() {

    // Instance variables
    private lateinit var mEmail : EditText
    private lateinit var listener : EmailDialogInterface

    /**
     * This method creates the dialog layout
     * @param savedInstanceState : Bundle?
     * @return Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.profile_image_dialog_layout, null)

        mEmail = view.findViewById(R.id.pi_email_text)

        builder.setTitle(resources.getString(R.string.profile_image_title_text))
            .setMessage(resources.getString(R.string.profile_image_dialog_text))
            .setView(view)
            .setPositiveButton(resources.getString(R.string.profile_image_dialog_button)) { _, _ ->
                listener.onContinueClicked(mEmail.text.toString())
            }
            .setNegativeButton(resources.getString(R.string.delete_button), null)

        return builder.create()
    }

    /**
     * This method attaches the dialog to the relative activity
     * @param context : Context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as EmailDialogInterface
        } catch (e : java.lang.ClassCastException) {
            throw java.lang.ClassCastException("$context must be implemented EmailDialogInterface")
        }
    }

    /**
     * This interface defines dialog's function
     */
    interface EmailDialogInterface {
        fun onContinueClicked(email : String)
    }
}