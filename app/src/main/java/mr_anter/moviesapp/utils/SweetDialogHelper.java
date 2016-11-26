package mr_anter.moviesapp.utils;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mostafa_anter on 9/2/16.
 */
public class SweetDialogHelper {
    private FragmentActivity mContext;
    private SweetAlertDialog pDialog;

    public SweetDialogHelper(FragmentActivity mContext) {
        this.mContext = mContext;
    }

    public void showMaterialProgress(String message) {
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#69ABB2"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void showBasicMessage(String message) {
        pDialog = new SweetAlertDialog(mContext);
        pDialog.setTitleText(message).show();
    }

    public void showTitleWithATextUnder(String title, String message) {
        pDialog = new SweetAlertDialog(mContext);
        pDialog.setTitleText(title)
                .setContentText(message)
                .show();
    }

    public void showWarningMessage(String title, String message, String confirmMessage) {
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(title)
                .setContentText(message)
                .setConfirmText(confirmMessage)
                .show();
    }

    public void showSuccessfulMessage(String title, String message) {
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(title)
                .setContentText(message)
                .show();
    }

    public void showErrorMessage(String title, String message) {
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText(title)
                .setContentText(message)
                .show();
    }

    public void dismissDialog() {
        pDialog.dismissWithAnimation();
    }
}
