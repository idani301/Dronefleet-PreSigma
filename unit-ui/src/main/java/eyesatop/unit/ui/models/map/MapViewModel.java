package eyesatop.unit.ui.models.map;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Environment;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.map.gmaps.addons.mapbox.MapBoxOfflineTileProvider;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.util.Consumer;
import eyesatop.util.Removable;
import eyesatop.util.android.SourceFiles;
import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.geo.Location;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import files.FilesUtils;

public class MapViewModel extends AbstractViewModel<View> {

    private static final String ORTO_RELATIVE_PATH = "/Eyesatop_Data/Orto";
    private final File ortoFilesFolder;
    private final File sdOrtoFilesFolder;


    private final ObservableCollection<MapItem> mapItems;
    private final ObservableList<MapItem> mapItemsWithListeners = new ObservableList<>();
    private final Property<Float> googleMapZoom = new Property<>();
    private final MapFragment mapFragment;
    private final Property<Location> focusRequests = new Property<>();

    public MapViewModel(
            final View view,
            final Property<Location> crosshairLocation) {
        super(view);
        Activity activity = findActivity(view);
        FragmentManager fragmentManager = activity.getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);

        ortoFilesFolder = EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.ORTO_MBTILES,false);

        sdOrtoFilesFolder = EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.ORTO_MBTILES,true);
        if(sdOrtoFilesFolder != null && !sdOrtoFilesFolder.exists()){
            sdOrtoFilesFolder.mkdirs();
        }

        if(!ortoFilesFolder.exists()){
            ortoFilesFolder.mkdirs();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                File[] mbtilesFiles = FilesUtils.listFiles(ortoFilesFolder, "mbtiles");
                for(File mbTileFile : mbtilesFiles){

                    // Create new TileOverlayOptions instance.
                    TileOverlayOptions opts = new TileOverlayOptions();

                    // Create an instance of MapBoxOfflineTileProvider.
                    MapBoxOfflineTileProvider provider = new MapBoxOfflineTileProvider(mbTileFile);

                    // Set the tile provider on the TileOverlayOptions.
                    opts.tileProvider(provider);

                    // Add the tile overlay to the map.
                    TileOverlay overlay = googleMap.addTileOverlay(opts);
                    overlay.setTransparency(0.25F);
                    overlay.setZIndex(0F);
                }


                File[] sdMbtilesFiles = FilesUtils.listFiles(sdOrtoFilesFolder, "mbtiles");
                for(File mbTileFile : sdMbtilesFiles){

                    // Create new TileOverlayOptions instance.
                    TileOverlayOptions opts = new TileOverlayOptions();

                    // Create an instance of MapBoxOfflineTileProvider.
                    MapBoxOfflineTileProvider provider = new MapBoxOfflineTileProvider(mbTileFile);

                    // Set the tile provider on the TileOverlayOptions.
                    opts.tileProvider(provider);

                    // Add the tile overlay to the map.
                    TileOverlay overlay = googleMap.addTileOverlay(opts);
                    overlay.setTransparency(0.25F);
                    overlay.setZIndex(0F);
                }

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        Location touchedLocation = new Location(latLng.latitude, latLng.longitude);
                        crosshairLocation.set(touchedLocation);

                        for(MapItem mapItem : mapItemsWithListeners){
                            if(mapItem.isLocationHitItem(touchedLocation)){
                                System.out.println("Hit inside drawable");
                                MapItem.MapClickListener mapItemListener = mapItem.mapListener().value();
                                if(mapItemListener != null){
                                    mapItemListener.onMapClick();
                                }
                            }
                            else{
                                System.out.println("Hit outside drawable");
                            }
                        }
                    }
                });

                googleMapZoom.set(googleMap.getCameraPosition().zoom);

                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {

                        if(Math.abs(googleMap.getCameraPosition().zoom - googleMapZoom.value()) > 0.3){
                            googleMapZoom.set(googleMap.getCameraPosition().zoom);
                        }
                    }
                });
            }
        });

        focusRequests().observe(new Observer<Location>() {
            @Override
            public void observe(Location oldValue, final Location newValue, Observation<Location> observation) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newValue.getLatitude(),newValue.getLongitude()), Math.max(17, googleMap.getCameraPosition().zoom)));
                    }
                });
            }
        });

        mapItems = new ObservableList<>();

        googleMapZoom.observe(new Observer<Float>() {
            @Override
            public void observe(Float oldValue, Float newValue, Observation<Float> observation) {
                for (MapItem item : mapItems) {
                    item.zoomChange(newValue);
                }
            }
        }, UI_EXECUTOR);

        mapItems.observe(new CollectionObserver<MapItem>(){

            private final Map<MapItem, Removable> bindings = new HashMap<>();
            private final Map<MapItem, Removable> mapListenersBindings = new HashMap();

            @Override
            public void added(final MapItem mapItem, Observation<MapItem> observation) {
                withMap(new AddMarker(bindings, mapItem, view.getContext()));

                ObservableValue<MapItem.MapClickListener> itemListener = mapItem.mapListener();
                if(itemListener != null){
                    mapListenersBindings.put(mapItem,itemListener.observe(new Observer<MapItem.MapClickListener>() {
                        @Override
                        public void observe(MapItem.MapClickListener oldValue, MapItem.MapClickListener newValue, Observation<MapItem.MapClickListener> observation) {

                            if(newValue != null){
                                mapItemsWithListeners.add(mapItem);
                            }
                            else{
                                mapItemsWithListeners.remove(mapItem);
                            }
                        }
                    }).observeCurrentValue());
                }
            }

            @Override
            public void removed(MapItem mapItem, Observation<MapItem> observation) {
                withMap(new RemoveMarker(bindings, mapItem));
                mapListenersBindings.remove(mapItem);
            }

            @Override
            public void replaced(MapItem oldValue, MapItem newValue, Observation<MapItem> observation) {
                throw new UnsupportedOperationException("use add or remove, not set.");
            }
        });
    }

    public void addMapItem(MapItem mapItem){
        mapItems.add(mapItem);
    }

    public void removeMapItem(MapItem mapItem){
        mapItems.remove(mapItem);
    }

    private void withMap(final Consumer<GoogleMap> consumer) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                consumer.apply(googleMap);
            }
        });
    }

    public Property<Location> focusRequests() {
        return focusRequests;
    }
}
