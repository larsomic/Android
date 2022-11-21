package protect.card_locker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

class EditCardIdAndBarcode implements View.OnClickListener {
    private final LoyaltyCardEditActivity loyaltyCardEditActivity;

    public EditCardIdAndBarcode(LoyaltyCardEditActivity loyaltyCardEditActivity) {
        this.loyaltyCardEditActivity = loyaltyCardEditActivity;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(loyaltyCardEditActivity.getApplicationContext(), ScanActivity.class);
        final Bundle b = new Bundle();
        b.putString(LoyaltyCardEditActivity.BUNDLE_CARDID, loyaltyCardEditActivity.cardIdFieldView.getText().toString());
        i.putExtras(b);
        loyaltyCardEditActivity.mCardIdAndBarCodeEditorLauncher.launch(i);
    }
}
