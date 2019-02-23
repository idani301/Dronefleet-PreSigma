//package com.example.abstractcontroller.records;
//
//import android.os.Environment;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import eyesatop.controller.DroneController;
////import eyesatop.util.android.files.UniqueFile;
//import eyesatop.util.model.ObservableValue;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by Idan on 01/11/2017.
// */
//
//public class ControllerRecords {
//
//    public static void recordController(DroneController controllerToCopy) throws IOException {
//        File recordFolder = new UniqueFile("Records","", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Idan Records")).createUniqueFile();
//
//        addObservableValueRecord(controllerToCopy.telemetry(),recordFolder,"Telemetry");
//        addObservableValueRecord(controllerToCopy.motorsOn(),recordFolder,"Motors On");
//        addObservableValueRecord(controllerToCopy.connectivity(),recordFolder,"Connectivity");
//        addObservableValueRecord(controllerToCopy.flying(),recordFolder,"flying");
//        addObservableValueRecord(controllerToCopy.flightMode(),recordFolder,"flightMode");
//        addObservableValueRecord(controllerToCopy.gps(),recordFolder,"gps");
//        addObservableValueRecord(controllerToCopy.lastKnownLocation(),recordFolder,"lastKnownLocation");
//        addObservableValueRecord(controllerToCopy.droneBattery(),recordFolder,"droneBattery");
//        addObservableValueRecord(controllerToCopy.lookAtLocation(),recordFolder,"lookAtLocation");
//        addObservableValueRecord(controllerToCopy.model(),recordFolder,"model");
//        addObservableValueRecord(controllerToCopy.rcFlightModeSwitchPosition(),recordFolder,"rcFlightModeSwitchPosition");
//        addObservableValueRecord(controllerToCopy.rcInFunctionMode(),recordFolder,"rcInFunctionMode");
//        addObservableValueRecord(controllerToCopy.rcLocation(),recordFolder,"rcLocation");
//        addObservableValueRecord(controllerToCopy.rcSignalStrengthPercent(),recordFolder,"rcSignalStrengthPercent");
//        addObservableValueRecord(controllerToCopy.gimbal().gimbalState(),recordFolder,"gimbalState");
//        addObservableValueRecord(controllerToCopy.gimbal().currentTask(),recordFolder,"currentTask_gimbal");
//        addObservableValueRecord(controllerToCopy.camera().isShootingPhoto(),recordFolder,"isShootingPhoto");
//        addObservableValueRecord(controllerToCopy.camera().mediaStorage(),recordFolder,"mediaStorage");
//        addObservableValueRecord(controllerToCopy.camera().mode(),recordFolder,"mode");
//        addObservableValueRecord(controllerToCopy.camera().recording(),recordFolder,"recording");
//        addObservableValueRecord(controllerToCopy.camera().recordTimeInSeconds(),recordFolder,"recordTimeInSeconds");
//        addObservableValueRecord(controllerToCopy.camera().zoomLevel(),recordFolder,"zoomLevel");
//        addObservableValueRecord(controllerToCopy.camera().currentTask(),recordFolder,"currentTask_camera");
//        addObservableValueRecord(controllerToCopy.flightTasks().current(),recordFolder,"currentTask_flight");
//        addObservableValueRecord(controllerToCopy.droneHome().homeLocation(),recordFolder,"homeLocation");
//        addObservableValueRecord(controllerToCopy.droneHome().limitationActive(),recordFolder,"limitationActive");
//        addObservableValueRecord(controllerToCopy.droneHome().takeOffDTM(),recordFolder,"takeOffDTM");
//        addObservableValueRecord(controllerToCopy.droneHome().maxDistanceFromHome(),recordFolder,"maxDistanceFromHome");
//        addObservableValueRecord(controllerToCopy.droneHome().currentTask(),recordFolder,"currentTask_home");
//    }
//
//    private static void addObservableValueRecord(ObservableValue observableValue, File rootFolder, String name) throws IOException {
//
//        final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
//
//        final Property<Long> lastUpdateTime = new Property<>(System.currentTimeMillis());
//        File newLog = new File(rootFolder.getAbsolutePath() + "/" + name + ".txt");
//        final FileOutputStream fileOutputStream = new FileOutputStream(newLog);
//        newLog.createNewFile();
//        observableValue.observe(new Observer() {
//            @Override
//            public void observe(Object oldValue, Object newValue, Observation observation) {
//                long currentTime = System.currentTimeMillis();
//                final String newLine = "Interval : " + (currentTime - lastUpdateTime.value()) + "\n";
//                lastUpdateTime.set(currentTime);
//
//                logExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            fileOutputStream.write(newLine.getBytes());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            fileOutputStream.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }
//}
