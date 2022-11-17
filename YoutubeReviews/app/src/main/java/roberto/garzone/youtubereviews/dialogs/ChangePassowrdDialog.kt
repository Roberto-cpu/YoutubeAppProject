package roberto.garzone.youtubereviews.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import roberto.garzone.youtubereviews.R

class ChangePasswordDialog : AppCompatDialogFragment() {
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
            showHidePassword(old, mOldPwd)
        }
        mShowNew.setOnClickListener {
            showHidePassword(new, mNewPwd)
        }

        mShowConf.setOnClickListener {
            showHidePassword(conf, mConfPwd)
        }

        builder.setTitle(resources.getString(R.string.settings_change_pwd))
            .setMessage(resources.getString(R.string.settings_chg_pwd_msg))
            .setView(view)
            .setPositiveButton(resources.getString(R.string.dialog_ok)) {_,_ ->
                listener.onOkClicked(mOldPwd.text.toString(), mNewPwd.text.toString(), mConfPwd.text.toString())
            }
            .setNegativeButton(resources.getString(R.string.dialog_deny)) {_,_ ->}

        return builder.create()
    }

    private fun showHidePassword (check : Boolean, edit : EditText) {
        var check2 = check
        if (!check2) {
            check2 = true
            edit.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        else {
            check2 = false
            edit.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
    }

    interface ChangePasswordDialogInterface {
        fun onOkClicked(old : String, new : String, conf : String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {listener = context as ChangePasswordDialogInterface
        }
        catch (e: java.lang.ClassCastException) {
            throw java.lang.ClassCastException ("$context must be implemented ChangePasswordDialogInterface")
        }
    }
}