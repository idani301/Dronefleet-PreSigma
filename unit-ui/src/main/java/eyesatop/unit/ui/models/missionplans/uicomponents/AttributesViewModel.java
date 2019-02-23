package eyesatop.unit.ui.models.missionplans.uicomponents;

import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.SwitchViewModel;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.EditTextViewModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.SpinnerViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.missionplans.components.AttributeData;
import eyesatop.util.Function;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 14/11/2017.
 */

public class AttributesViewModel extends AbstractViewModel<View>{

    private final TextViewModel attributeName;
    private final EditTextViewModel valueText;
    private final ImageViewModel minusButton;
    private final ImageViewModel plusButton;
    private final SpinnerViewModel spinnerView;
    private final SwitchViewModel switchViewModel;
    private final ImageViewModel clearFieldButton;
    private final LittleMessageViewModel littleMessageViewModel;

    private final AttributeData attributeData;

    private final RemovableCollection bindings = new RemovableCollection();

    public AttributesViewModel(final View view, final AttributeData data, final LittleMessageViewModel littleMessageViewModel){
        super(view);
        this.attributeData = data;
        this.littleMessageViewModel = littleMessageViewModel;
        attributeName = new TextViewModel((TextView) view.findViewById(R.id.attributeName));
        valueText = new EditTextViewModel((EditText) view.findViewById(R.id.inputAttributeEditText));
        minusButton = new ImageViewModel((ImageView) view.findViewById(R.id.minusAttributeButton));
        plusButton = new ImageViewModel((ImageView) view.findViewById(R.id.plusAttributeButton));
        clearFieldButton = new ImageViewModel((ImageView)view.findViewById(R.id.clearFieldButton));

        switchViewModel = new SwitchViewModel((Switch) view.findViewById(R.id.attributeSwitch));

        spinnerView = new SpinnerViewModel((Spinner) view.findViewById(R.id.attributeSpinner),data.getSpinnerOptions());

        attributeName.text().set(data.getName());

        switch (data.getInputType()){

            case INTEGER:
                valueText.inputType().set(InputType.TYPE_NUMBER_FLAG_SIGNED);
                valueText.setOnDoneListener(new EditTextViewModel.EditTextOnDone() {
                    @Override
                    public void onDoneCallback(String textWritten) {

                        try {
                            Integer typedValue = Integer.parseInt(textWritten);

                            if(attributeData.getMinValue() != null && typedValue < attributeData.getMinValue()){
                                littleMessageViewModel.addNewMessage("Smallest number for " + attributeData.getName() + " is " + attributeData.getMinValue());
                                throw new NumberFormatException();
                            }

                            if(attributeData.getMaxValue() != null && typedValue > attributeData.getMaxValue()){
                                littleMessageViewModel.addNewMessage("Largest number for " + attributeData.getName() + " is " + attributeData.getMaxValue());
                                throw new NumberFormatException();
                            }

                            data.getIntValue().set(typedValue);
                        }
                        catch (Exception e){
                            valueText.text().set(data.getIntValue().value() == null ? "" : data.getIntValue().value().toString());
                            return;
                        }

                    }
                });

                bindings.add(valueText.text().bind(data.getIntValue().transform(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer input) {

                        if(input == null){
                            return "";
                        }

                        return input + "";
                    }
                })));

                minusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {

                        Integer currentValue = data.getIntValue().value();
                        if(currentValue == null){
                            currentValue = attributeData.getMinValue() == null ? 0 : attributeData.getMinValue();
                            data.getIntValue().set(currentValue);
                            return false;
                        }


                        if(attributeData.getMinValue() != null){
                            data.getIntValue().set(Math.max(attributeData.getMinValue(),currentValue - (int) data.getDelta()));
                        }
                        else {
                            data.getIntValue().set(currentValue - (int) data.getDelta());
                        }
                        return false;
                    }
                });

                plusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {
                        Integer currentValue = data.getIntValue().value();

                        if(currentValue == null){
                            currentValue = attributeData.getMinValue() == null ? 0 : attributeData.getMinValue();
                            data.getIntValue().set(currentValue);
                            return false;
                        }

                        if(data.getMaxValue() != null){
                            data.getIntValue().set(Math.min(data.getMaxValue(),currentValue + (int)data.getDelta()));
                        }
                        else {
                            data.getIntValue().set(currentValue + (int) data.getDelta());
                        }

                        return false;
                    }
                });
                break;
            case DOUBLE:
                valueText.inputType().set(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valueText.setOnDoneListener(new EditTextViewModel.EditTextOnDone() {
                    @Override
                    public void onDoneCallback(String textWritten) {

                        try{
                            Double valueWritten = Double.parseDouble(textWritten);

                            if(attributeData.getMinValue() != null && valueWritten < attributeData.getMinValue()){
                                littleMessageViewModel.addNewMessage("Smallest number for " + attributeData.getName() + " is " + attributeData.getMinValue());
                                throw new NumberFormatException();
                            }

                            if(attributeData.getMaxValue() != null && valueWritten > attributeData.getMaxValue()){
                                littleMessageViewModel.addNewMessage("Largest number for " + attributeData.getName() + " is " + attributeData.getMaxValue());
                                throw new NumberFormatException();
                            }

                            data.getDoubleValue().set(valueWritten);
                        }
                        catch (Exception e){
                            valueText.text().set(data.getDoubleValue().value() == null ? "" : data.getDoubleValue().value().toString());
                            return;
                        }

                    }
                });
                bindings.add(valueText.text().bind(data.getDoubleValue().transform(new Function<Double, String>() {
                    @Override
                    public String apply(Double input) {

                        if(input == null){
                            return "";
                        }

                        return input + "";
                    }
                })));

                minusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {
                        Double currentValue = data.getDoubleValue().value();

                        if(currentValue == null){
                            currentValue = attributeData.getMinValue() == null ? 0 : attributeData.getMinValue().doubleValue();
                            data.getDoubleValue().set(currentValue);
                            return false;
                        }


                        if(attributeData.getMinValue() != null){
                            data.getDoubleValue().set(Math.max(attributeData.getMinValue(),currentValue - data.getDelta()));
                        }
                        else {
                            data.getDoubleValue().set(currentValue - data.getDelta());
                        }
                        return false;
                    }
                });

                plusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {

                        Double currentValue = data.getDoubleValue().value();

                        if(currentValue == null){
                            currentValue = attributeData.getMinValue() == null ? 0 : attributeData.getMinValue().doubleValue();
                            data.getDoubleValue().set(currentValue);
                            return false;
                        }

                        if(attributeData.getMaxValue() != null){
                            data.getDoubleValue().set(Math.min(currentValue + data.getDelta(),attributeData.getMaxValue()));
                        }
                        else {
                            data.getDoubleValue().set(currentValue + data.getDelta());
                        }
                        return false;
                    }
                });

                break;
            case STRING:
                plusButton.visibility().set(Visibility.INVISIBLE);
                minusButton.visibility().set(Visibility.INVISIBLE);

                bindings.add(valueText.text().bind(data.getStringValue().withDefault("")));

                valueText.inputType().set(InputType.TYPE_CLASS_TEXT);
                valueText.setOnDoneListener(new EditTextViewModel.EditTextOnDone() {
                    @Override
                    public void onDoneCallback(String textWritten) {
                        data.getStringValue().set(textWritten);
                    }
                });
                break;
            case NONE:
                valueText.visibility().set(Visibility.GONE);
                minusButton.visibility().set(Visibility.GONE);
                plusButton.visibility().set(Visibility.GONE);
                break;
        }

        if(data.isIncludeSpinner()){
            Object spinnerStartValue = data.getSelectedSpinner().value();
            if(spinnerStartValue != null) {
                spinnerView.setSelectedItem(spinnerStartValue);
            }
            bindings.add(data.getSelectedSpinner().bind(spinnerView.selectedItem()));
        }

        if(!data.isIncludeSwitch()){
            switchViewModel.visibility().set(Visibility.GONE);
        }
        else{
            switchViewModel.setIsSwitchChecked(data.getSelectedSwitch().withDefault(false).value());
            bindings.add(data.getSelectedSwitch().bind(switchViewModel.getIsSwitchChecked()));
        }

        clearFieldButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                clear();
                return false;
            }
        });

        bindings.add(attributeData.getVisbility()
                .observe(new Observer<Visibility>() {
            @Override
            public void observe(Visibility oldValue, Visibility newValue, Observation<Visibility> observation) {
                switch(newValue){

                    case VISIBLE:
                        view.setVisibility(View.VISIBLE);
                        break;
                    case INVISIBLE:
                        view.setVisibility(View.INVISIBLE);
                        break;
                    case GONE:
                        view.setVisibility(View.GONE);
                        break;
                }
            }
        },UI_EXECUTOR).observeCurrentValue());
    }

    public void clear(){

        attributeData.getStringValue().set(null);
        attributeData.getDoubleValue().set(null);
        attributeData.getIntValue().set(null);

        valueText.text().set(null);
        spinnerView.clear();
        switchViewModel.setIsSwitchChecked(false);

    }

    public void destroy(){
        bindings.remove();
    }

    public AttributeData getAttributeData() {
        return attributeData;
    }
}
