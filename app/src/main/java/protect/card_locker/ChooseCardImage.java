package protect.card_locker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

class ChooseCardImage implements View.OnClickListener {
    private final LoyaltyCardEditActivity loyaltyCardEditActivity;

    public ChooseCardImage(LoyaltyCardEditActivity loyaltyCardEditActivity) {
        this.loyaltyCardEditActivity = loyaltyCardEditActivity;
    }

    @Override
    public void onClick(View v) throws NoSuchElementException {
        ImageView targetView;

        if (v.getId() == R.id.frontImageHolder) {
            targetView = loyaltyCardEditActivity.cardImageFront;
        } else if (v.getId() == R.id.backImageHolder) {
            targetView = LoyaltyCardEditActivity.cardImageBack;
        } else if (v.getId() == R.id.thumbnail) {
            targetView = loyaltyCardEditActivity.thumbnail;
        } else {
            throw new IllegalArgumentException("Invalid IMAGE ID " + v.getId());
        }

        LinkedHashMap<String, Callable<Void>> cardOptions = new LinkedHashMap<>();
        if (targetView.getTag() != null && v.getId() != R.id.thumbnail) {
            cardOptions.put(loyaltyCardEditActivity.getString(R.string.removeImage), () -> {
                if (targetView == loyaltyCardEditActivity.cardImageFront) {
                    loyaltyCardEditActivity.mFrontImageRemoved = true;
                    loyaltyCardEditActivity.mFrontImageUnsaved = false;
                } else {
                    loyaltyCardEditActivity.mBackImageRemoved = true;
                    LoyaltyCardEditActivity.mBackImageUnsaved = false;
                }

                LoyaltyCardEditActivity.setCardImage(targetView, null, true);
                return null;
            });
        }

        if (v.getId() == R.id.thumbnail) {
            cardOptions.put(loyaltyCardEditActivity.getString(R.string.selectColor), () -> {
                ColorPickerDialog.Builder dialogBuilder = ColorPickerDialog.newBuilder();

                if (loyaltyCardEditActivity.tempLoyaltyCard.headerColor != null) {
                    dialogBuilder.setColor(loyaltyCardEditActivity.tempLoyaltyCard.headerColor);
                }

                ColorPickerDialog dialog = dialogBuilder.create();
                dialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        loyaltyCardEditActivity.updateTempState(LoyaltyCardField.headerColor, color);

                        loyaltyCardEditActivity.thumbnailEditIcon.setBackgroundColor(Utils.needsDarkForeground(color) ? Color.BLACK : Color.WHITE);
                        loyaltyCardEditActivity.thumbnailEditIcon.setColorFilter(Utils.needsDarkForeground(color) ? Color.WHITE : Color.BLACK);

                        // Unset image if set
                        loyaltyCardEditActivity.thumbnail.setTag(null);

                        loyaltyCardEditActivity.generateIcon(loyaltyCardEditActivity.storeFieldEdit.getText().toString());
                    }

                    @Override
                    public void onDialogDismissed(int dialogId) {
                        // Nothing to do, no change made
                    }
                });
                dialog.show(loyaltyCardEditActivity.getSupportFragmentManager(), "color-picker-dialog");

                LoyaltyCardEditActivity.setCardImage(targetView, null, false);
                loyaltyCardEditActivity.mIconRemoved = true;
                loyaltyCardEditActivity.mIconUnsaved = false;

                return null;
            });
        }

        cardOptions.put(loyaltyCardEditActivity.getString(R.string.takePhoto), () -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permissionRequestType;

                if (v.getId() == R.id.frontImageHolder) {
                    permissionRequestType = LoyaltyCardEditActivity.PERMISSION_REQUEST_CAMERA_IMAGE_FRONT;
                } else if (v.getId() == R.id.backImageHolder) {
                    permissionRequestType = LoyaltyCardEditActivity.PERMISSION_REQUEST_CAMERA_IMAGE_BACK;
                } else if (v.getId() == R.id.thumbnail) {
                    permissionRequestType = LoyaltyCardEditActivity.PERMISSION_REQUEST_CAMERA_IMAGE_ICON;
                } else {
                    throw new IllegalArgumentException("Unknown ID type " + v.getId());
                }

                loyaltyCardEditActivity.requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionRequestType);
            } else {
                int cardImageType;

                if (v.getId() == R.id.frontImageHolder) {
                    cardImageType = Utils.CARD_IMAGE_FROM_CAMERA_FRONT;
                } else if (v.getId() == R.id.backImageHolder) {
                    cardImageType = Utils.CARD_IMAGE_FROM_CAMERA_BACK;
                } else if (v.getId() == R.id.thumbnail) {
                    cardImageType = Utils.CARD_IMAGE_FROM_CAMERA_ICON;
                } else {
                    throw new IllegalArgumentException("Unknown ID type " + v.getId());
                }

                loyaltyCardEditActivity.takePhotoForCard(cardImageType);
            }
            return null;
        });

        cardOptions.put(loyaltyCardEditActivity.getString(R.string.addFromImage), () -> {
            if (v.getId() == R.id.frontImageHolder) {
                loyaltyCardEditActivity.mRequestedImage = Utils.CARD_IMAGE_FROM_FILE_FRONT;
            } else if (v.getId() == R.id.backImageHolder) {
                loyaltyCardEditActivity.mRequestedImage = Utils.CARD_IMAGE_FROM_FILE_BACK;
            } else if (v.getId() == R.id.thumbnail) {
                loyaltyCardEditActivity.mRequestedImage = Utils.CARD_IMAGE_FROM_FILE_ICON;
            } else {
                throw new IllegalArgumentException("Unknown ID type " + v.getId());
            }

            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");

            try {
                loyaltyCardEditActivity.mPhotoPickerLauncher.launch(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(loyaltyCardEditActivity.getApplicationContext(), R.string.failedLaunchingPhotoPicker, Toast.LENGTH_LONG).show();
                Log.e(LoyaltyCardEditActivity.TAG, "No activity found to handle intent", e);
            }

            return null;
        });

        int titleResource;

        if (v.getId() == R.id.frontImageHolder) {
            titleResource = R.string.setFrontImage;
        } else if (v.getId() == R.id.backImageHolder) {
            titleResource = R.string.setBackImage;
        } else if (v.getId() == R.id.thumbnail) {
            titleResource = R.string.setIcon;
        } else {
            throw new IllegalArgumentException("Unknown ID type " + v.getId());
        }

        new MaterialAlertDialogBuilder(loyaltyCardEditActivity)
                .setTitle(loyaltyCardEditActivity.getString(titleResource))
                .setItems(cardOptions.keySet().toArray(new CharSequence[cardOptions.size()]), (dialog, which) -> {
                    Iterator<Callable<Void>> callables = cardOptions.values().iterator();
                    Callable<Void> callable = callables.next();

                    for (int i = 0; i < which; i++) {
                        callable = callables.next();
                    }

                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Rethrow as NoSuchElementException
                        // This isn't really true, but a View.OnClickListener doesn't allow throwing other types
                        throw new NoSuchElementException(e.getMessage());
                    }
                })
                .show();
    }
}
