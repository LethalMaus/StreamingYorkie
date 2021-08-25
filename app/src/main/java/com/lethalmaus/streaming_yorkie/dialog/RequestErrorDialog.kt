package com.lethalmaus.streaming_yorkie.dialog

import android.os.Bundle
import android.view.View
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.BaseDialogFragment
import com.lethalmaus.streaming_yorkie.repository.Endpoint

class RequestErrorDialog(val endpoint: Endpoint) : BaseDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding!!.dialogTitle.text = getString(R.string.error_request_title)
        val text = "${getString(R.string.error_request_description)} $endpoint"
        viewBinding!!.dialogDescription.text = text
        viewBinding!!.dialogActionButton.text = getString(R.string.general_okay)
        viewBinding!!.dialogActionButton.setOnClickListener {
            this.dismiss()
        }
        viewBinding!!.dialogCancelButton.visibility = View.GONE
    }
}