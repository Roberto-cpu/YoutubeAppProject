package roberto.garzone.youtubereviews.dialogs

/**
 * @authors Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date 01/10/2022
 */

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import roberto.garzone.youtubereviews.R

/**
 * This class manages the dialog functionalities
 */
class AddCommentDialog : AppCompatDialogFragment() {

    // Instance variables
    private var mText : EditText = TODO()
    private var listener : AddCommentDialogInterface

    /**
     * This method creates the dialog layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity!!.layoutInflater
        var view = inflater.inflate(R.layout.add_comment_dialog_layout, null)

        mText = view.findViewById(R.id.add_comment_text)

        builder.setTitle(resources.getString(R.string.add_comment_dialog_title))
            .setMessage(resources.getString(R.string.add_comment_dialog_message))
            .setView(view)
            .setPositiveButton(resources.getString(R.string.dialog_allow)) { _, _ ->
                listener.onAllowClicked(mText.text.toString())
            }
            .setNegativeButton(resources.getString(R.string.dialog_deny), null)

        return builder.create()
    }

    /**
     * This method attaches the dialog to the relative activity
     * @param context : Context
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            listener = context as AddCommentDialogInterface
        } catch (e : java.lang.ClassCastException) {
            throw java.lang.ClassCastException("$context must be implemented AddCommentDialogInterface")
        }
    }

    /**
     * This interface defines the dialog response functions
     */
    interface AddCommentDialogInterface {
        fun onAllowClicked(text : String)
    }
}