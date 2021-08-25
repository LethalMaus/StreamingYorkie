package com.lethalmaus.streaming_yorkie.dialog

import android.os.Bundle
import android.view.View
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.BaseDialogFragment

class LoginErrorDialog : BaseDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding!!.dialogTitle.text = getString(R.string.error_login_title)
        viewBinding!!.dialogDescription.text = getString(R.string.error_login_description)
        viewBinding!!.dialogActionButton.text = getString(R.string.general_okay)
        viewBinding!!.dialogActionButton.setOnClickListener {
            this.dismiss()
        }
        viewBinding!!.dialogCancelButton.visibility = View.GONE
    }
}