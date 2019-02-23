package eyesatop.unit.ui.models.map;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import eyesatop.util.Function;

public class BitmapToDescriptor implements Function<Bitmap,BitmapDescriptor> {
    @Override
    public BitmapDescriptor apply(Bitmap input) {
        return BitmapDescriptorFactory.fromBitmap(input);
    }
}
