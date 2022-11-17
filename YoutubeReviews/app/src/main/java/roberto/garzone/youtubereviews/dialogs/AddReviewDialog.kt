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
 * This class manages the dialog to add a new review
 */
class AddReviewDialog : AppCompatDialogFragment() {

    // Instance variable
    private var listener : AddReviewDialogInterface? = null

    /**
     * This method creates the dialog layout
     * @param savedInstanceState : Bundle
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        builder.setTitle(resources.getString(R.string.new_review_dialog_title))
            .setMessage(resources.getString(R.string.new_review_dialog_message))
            .setPositiveButton(resources.getString(R.string.dialog_ok)) { _, _ ->
                listener!!.onOkClicked()
            }

        return builder.create()
    }

    /**
     * This method links the dialog to the relative activity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AddReviewDialogInterface
        } catch (e : java.lang.ClassCastException) {
            throw java.lang.ClassCastException("$context must be implemented AddReviewDialogInterface")
        }
    }

    /**
     * This interface defines dialog's the positive button function
     */
    interface AddReviewDialogInterface {
        fun onOkClicked()
    }
}