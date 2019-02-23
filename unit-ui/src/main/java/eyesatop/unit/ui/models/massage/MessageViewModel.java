package eyesatop.unit.ui.models.massage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import eyesatop.controller.DroneController;
import eyesatop.unit.DroneUnit;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.DroneStatusModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.util.Function;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;

/**
 * Created by Einav on 04/07/2017.
 */

public class MessageViewModel {

    private final Activity activity;
    private final DroneUnit unit;

    private final TextViewModel body;
    private final TextViewModel header;

    private final TextViewModel okButton;
    private final TextViewModel cancelButton;
    private final LinearLayout cancelButtonLinearLayout;

    private final ImageView imageHeader;

    private final View massageView;
    private final View screen;
    private final View line;
    private TextureView videoSurface;
    private boolean hasMessageInProgress = false;

    public interface MessageViewModelListener {
        void onOkButtonPressed();
        void onCancelButtonPressed();
    }

    public MessageViewModel(Context context, DroneUnit unit,TextureView videoSurface) {

        this.activity = (Activity) context;
        this.unit = unit;
        this.videoSurface = videoSurface;

        this.header = new TextViewModel((TextView) activity.findViewById(R.id.massageHeader));
        this.body = new TextViewModel((TextView) activity.findViewById(R.id.massageBodyText));
        this.okButton = new TextViewModel((TextView) activity.findViewById(R.id.buttonOkMassageType1));
        this.cancelButton = new TextViewModel((TextView) activity.findViewById(R.id.buttonCancelMassageType1));
        this.imageHeader = (ImageView) activity.findViewById(R.id.massageImage);
        this.massageView = activity.findViewById(R.id.includeMessage);
        this.screen = activity.findViewById(R.id.linearLayoutMenu);
        this.line = activity.findViewById(R.id.messageLine);
        this.cancelButtonLinearLayout = (LinearLayout) activity.findViewById(R.id.buttonCancelLayout);
        okButton.view().setClickable(true);
        cancelButton.view().setClickable(true);
    }

    public void display() {
        massageView.setVisibility(View.VISIBLE);
        screen.setFocusable(true);
        screen.setClickable(true);
    }

    public void dispose() {
        hasMessageInProgress = false;
        massageView.setVisibility(View.GONE);
        screen.setFocusable(false);
        screen.setClickable(false);
    }

    public void addCoordinateMessage(final DroneController controller, DtmProvider dtmProvider) {

        hasMessageInProgress = true;

        final Uri uriOnStart = getImageUri(activity, videoSurface.getBitmap());

        imageHeader.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.target));

        final Location lookAtLocation = controller.lookAtLocation().value();
        final Location droneLocation = Telemetry.telemetryToLocation(controller.telemetry().value());

        if (lookAtLocation == null) {
            return;
        }

        String distanceString = null;
        double distanceToTarget;
        double azToTarget;

        if (droneLocation != null) {
            distanceToTarget = droneLocation.distance(lookAtLocation);
            azToTarget = droneLocation.az(lookAtLocation);
            distanceString = "Distance : " + (int) distanceToTarget + "(m), Az : " + (int) azToTarget + DroneStatusModel.DEGREE;
        }

        String targetMSLString = "";
        Double targetMSL = null;
        try {
            targetMSL = dtmProvider.terrainAltitude(lookAtLocation);
            targetMSLString = targetMSL + "(m)";
        } catch (TerrainNotFoundException e) {
            e.printStackTrace();
            targetMSLString = "Unknown";
        }

        body.text().set("Lat : " + lookAtLocation.getLatitude() + "\n" +
                "Lon : " + lookAtLocation.getLongitude() +
                "\nMSL : " + targetMSLString +
                (distanceString != null ? ("\n" + distanceString) : ""));
        header.text().set("Target Coordinate");

        cancelButton.text().set("Share");

        cancelButtonLinearLayout.setVisibility(View.VISIBLE);
        okButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                dispose();
                return false;
            }
        });
        cancelButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                dispose();

//                PackageManager pm= activity.getPackageManager();
//                try {
//
//                    Intent waIntent = new Intent(Intent.ACTION_SEND);
//                    waIntent.setType("text/plain");

//
//                    PackageInfo info= pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//                    //Check if package exists or not. If not then code
//                    //in catch block will be called
//                    waIntent.setPackage("com.whatsapp");
//
//                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
//                    activity.startActivity(Intent.createChooser(waIntent, "Share with"));
//
//                } catch (PackageManager.NameNotFoundException e) {
//                }

                String text = "http://maps.google.com/maps?saddr=" + lookAtLocation.getLatitude() + "," + lookAtLocation.getLongitude();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriOnStart);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(Intent.createChooser(shareIntent, "send"));
                return false;
            }
        });

        display();
    }

    public void addGeneralMessage(String headerText, String bodyText, Drawable image, final MessageViewModelListener listener){

        hasMessageInProgress = true;
        imageHeader.setImageDrawable(image);
        cancelButton.text().set("Cancel");
        header.text().set(headerText);
        body.text().set(bodyText);

        okButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                listener.onOkButtonPressed();
                dispose();
                return false;
            }
        });


        cancelButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                listener.onCancelButtonPressed();
                dispose();
                return false;
            }
        });

        display();
    }

    public boolean isHasMessageInProgress() {
        return hasMessageInProgress;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
