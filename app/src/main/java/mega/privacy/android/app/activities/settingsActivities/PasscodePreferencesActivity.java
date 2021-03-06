package mega.privacy.android.app.activities.settingsActivities;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;

import mega.privacy.android.app.R;
import mega.privacy.android.app.fragments.settingsFragments.SettingsPasscodeLockFragment;

import static mega.privacy.android.app.utils.Constants.*;

public class PasscodePreferencesActivity extends PreferencesBaseActivity {

    public static final int INVALID_OPTION = -1;
    public static final int PIN_4_OPTION = 0;
    public static final int PIN_6_OPTION = 1;
    public static final int PIN_ALPHANUMERIC_OPTION = 2;

    private SettingsPasscodeLockFragment sttPasscodeLock;
    private AlertDialog setPinDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aB.setTitle(getString(R.string.settings_pin_lock_switch).toUpperCase());
        sttPasscodeLock = new SettingsPasscodeLockFragment();
        replaceFragment(sttPasscodeLock);
    }

    /**
     * Method for show the Panel to update the pin.
     */
    public void showPanelSetPinLock() {
        if (sttPasscodeLock == null)
            return;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final CharSequence[] items = {getString(R.string.four_pin_lock), getString(R.string.six_pin_lock), getString(R.string.AN_pin_lock)};

        dialogBuilder.setSingleChoiceItems(items, INVALID_OPTION, (dialog, item) -> {
            setPinDialog.dismiss();
            switch (item) {
                case PIN_4_OPTION:
                    dbH.setPinLockType(PIN_4);
                    sttPasscodeLock.intentToPinLock();
                    break;

                case PIN_6_OPTION:
                    dbH.setPinLockType(PIN_6);
                    sttPasscodeLock.intentToPinLock();
                    break;

                case PIN_ALPHANUMERIC_OPTION:
                    dbH.setPinLockType(PIN_ALPHANUMERIC);
                    sttPasscodeLock.intentToPinLock();
                    break;
            }
        });
        dialogBuilder.setTitle(getString(R.string.pin_lock_type));

        dialogBuilder.setOnKeyListener((arg0, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismissDialog();
            }
            return true;
        });

        dialogBuilder.setOnCancelListener(
                dialog -> dismissDialog()
        );

        setPinDialog = dialogBuilder.create();
        setPinDialog.setCanceledOnTouchOutside(true);
        setPinDialog.show();
    }

    private void dismissDialog() {
        setPinDialog.dismiss();
        sttPasscodeLock.cancelSetPinLock();
    }
}
