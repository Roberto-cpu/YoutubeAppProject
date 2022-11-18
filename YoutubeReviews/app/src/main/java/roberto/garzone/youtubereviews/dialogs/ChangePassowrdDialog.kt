package roberto.garzone.youtubereviews.dialogs

/**
 * @authors: Roberto Garzone 1991589, Emanuele Bettacchi 1749865
 * @date: 18/11/2022
 */

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.res.ResourcesCompat
import roberto.garzone.youtubereviews.R

/**
 * This class manages the change password dialog's functionalities
 */
class ChangePasswordDialog : AppCompatDialogFragment() {

    // Instance variables
    private lateinit var mOldPwd : EditText
    private lateinit var mNewPwd : EditText
    private lateinit var mConfPwd : EditText
    private lateinit var mShowOld : Button
    private lateinit var mShowNew : Button
    private lateinit var mShowConf : Button

    private lateinit var listener : ChangePasswordDialogInterface

    private var old  = false
    private var new = false
    private var conf  = false

    /**
     * This method creates the dialog layout
     * @param savedInstanceState : Bundle?
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.change_password_dialog_layout, null)

        mOldPwd = view.findViewById(R.id.settings_old_password)
        mNewPwd = view.findViewById(R.id.settings_new_password)
        mConfPwd = view.findViewById(R.id.settings_new_password_conf)
        mShowOld = view.findViewById(R.id.show_old_password)
        mShowNew = view.findViewById(R.id.show_new_password)
        mShowConf = view.findViewById(R.id.show_new_password_conf)

        mShowOld.setOnClickListener {
            old = showHidePassword(old, mOldPwd, mShowOld)
        }
        mShowNew.setOnClickListener {
            new = showHidePassword(new, mNewPwd, mShowNew)
        }

        mShowConf.setOnClickListener {
            conf = showHidePassword(conf, mConfPwd, mShowConf)
        }

        builder.setTitle(resources.getString(R.string.settings_change_pwd))
            .setMessage(resources.getString(R.string.settings_chg_pwd_msg))
            .setView(view)
            .setPositiveButton(resources.getString(R.string.dialog_ok)) {_,_ ->
                var pwdCheck : Boolean = checkPassword(mOldPwd.text.toString(), mNewPwd.text.toString(), mConfPwd.text.toString())
                if (pwdCheck) {
                    listener.onOkClicked(mNewPwd.text.toString())
                }
            }
            .setNegativeButton(resources.getString(R.string.dialog_deny)) {_,_ ->
                listener.onDeleteClicked()
            }

        return builder.create()
    }

    /**
     * This method shows or hides password
     */
    private fun showHidePassword (check : Boolean, edit : EditText, btn : Button) : Boolean {
        var check2 = check
        if (!check2) {
            check2 = true
            edit.transformationMethod = PasswordTransformationMethod.getInstance()
            btn.background = ResourcesCompat.getDrawable(resources, R.drawable.hidden_password, null)
        }
        else {
            check2 = false
            edit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            btn.background = ResourcesCompat.getDrawable(resources, R.drawable.show_password, null)
        }
        return check2
    }

    /**
     * This method checks the new password's correctness
     * @param old : String
     * @param new : String
     * @param conf : String
     * @return Boolean
     */
    private fun checkPassword(old : String, new : String, conf : String) : Boolean {
        return when {
            old.isEmpty() -> {
                mOldPwd.error = resources.getString(R.string.settings_chg_pwd_error_old)
                mOldPwd.setText("")
                false
            }
            new.isEmpty() -> {
                mNewPwd.error = resources.getString(R.string.settings_chg_pwd_error_new)
                mNewPwd.setText("")
                false
            }
            old == new -> {
                mNewPwd.error = resources.getString(R.string.settings_chg_pwd_error_old_new)
                mOldPwd.setText("")
                mNewPwd.setText("")
                false
            }
            new != conf -> {
                mNewPwd.error = resources.getString(R.string.settings_chg_pwd_error_new_conf)
                mNewPwd.setText("")
                mConfPwd.setText("")
                false
            }
            else -> true
        }
    }

    /**
     * This method attaches the dialog to the activity that invokes it
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ChangePasswordDialogInterface
        }
        catch (e: java.lang.ClassCastException) {
            throw java.lang.ClassCastException ("$context must be implemented ChangePasswordDialogInterface")
        }
    }

    /**
     * This interface manages what functions must be called when the dialog's buttons are clicked
     */
    interface ChangePasswordDialogInterface {
        fun onOkClicked(new : String)
        fun onDeleteClicked()
    }
}