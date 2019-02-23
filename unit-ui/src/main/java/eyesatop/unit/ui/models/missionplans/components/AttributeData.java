package eyesatop.unit.ui.models.missionplans.components;

import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 14/11/2017.
 */

public class AttributeData {

    private final String name;
    private final boolean includeSwitch;
    private final InputType inputType;
    private final double delta;
    private final boolean includeSpinner;
    private final Integer minValue;
    private final Integer maxValue;

    private final Property<ViewModel.Visibility> visbility = new Property<>(ViewModel.Visibility.VISIBLE);

    private final Property<String> stringValue = new Property<>();
    private final Property<Object> selectedSpinner = new Property<>();
    private final Property<Integer> intValue = new Property<>();
    private final Property<Double> doubleValue = new Property<>();
    private final BooleanProperty selectedswitch = new BooleanProperty();
    private final Object[] spinnerOptions;

    public AttributeData(String name, boolean includeSwitch, InputType inputType, double delta,Integer minValue, Integer maxValue, Object[] spinnerOptions) {
        this.name = name;
        this.includeSwitch = includeSwitch;
        this.inputType = inputType;
        this.delta = delta;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.includeSpinner = spinnerOptions != null && spinnerOptions.length > 0;
        this.spinnerOptions = spinnerOptions;
    }

    public static AttributeData createSimpleStringValue(String name){
        return new AttributeData(name,false,InputType.STRING,0, null, null, null);
    }

    public static AttributeData createSimpleIntegerValue(String name,Integer delta,Integer minValue,Integer maxValue){
        return new AttributeData(name,false,InputType.INTEGER,delta.doubleValue(),minValue, maxValue, null);
    }

    public static AttributeData createSimpleSpinner(String name,Object[] spinnerOptions){
        return new AttributeData(name,false,InputType.NONE,0,null, null, spinnerOptions);
    }

    public static AttributeData createSimpleDoubleValue(String name,double delta,Integer minValue,Integer maxValue){
        return new AttributeData(name,false,InputType.DOUBLE,delta, minValue, maxValue, null);
    }

    public static AttributeData createSimpleSwitch(String name){
        return new AttributeData(name,true,InputType.NONE,0,null, null, null);
    }

    public static AttributeData createDoubleSpinner(String name,Object[] spinnerOptions,Double delta,Integer minValue,Integer maxValue){
        return new AttributeData(name,false,InputType.DOUBLE,delta.doubleValue(), minValue, maxValue, spinnerOptions);
    }

    public String getName() {
        return name;
    }

    public boolean isIncludeSwitch() {
        return includeSwitch;
    }

    public InputType getInputType() {
        return inputType;
    }

    public double getDelta() {
        return delta;
    }

    public boolean isIncludeSpinner() {
        return includeSpinner;
    }

    public Property<Object> getSelectedSpinner() {
        return selectedSpinner;
    }

    public Property<Integer> getIntValue() {
        return intValue;
    }

    public Property<Double> getDoubleValue() {
        return doubleValue;
    }

    public BooleanProperty getSelectedSwitch() {
        return selectedswitch;
    }

    public Property<String> getStringValue() {
        return stringValue;
    }

    public Property<ViewModel.Visibility> getVisbility() {
        return visbility;
    }

    public void clear(){
        this.doubleValue.set(null);
        this.stringValue.set(null);
        this.intValue.set(null);
        selectedSpinner.set(null);
        selectedswitch.set(null);
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public Object[] getSpinnerOptions() {
        return spinnerOptions;
    }

    public enum InputType {
        INTEGER,
        DOUBLE,
        STRING,
        NONE
    }
}
