package eyesatop.unit.ui.models.tabs;

import android.graphics.Color;
import android.view.MotionEvent;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.unit.ui.DefultsColors;
import eyesatop.unit.ui.models.massage.ButtonText;
import eyesatop.unit.ui.models.massage.Text;
import eyesatop.unit.ui.models.massage.TextType;
import eyesatop.util.Function;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Einav on 04/07/2017.
 */



public class MessageInfoModel {

    private final BooleanProperty isDisplayed = new BooleanProperty(false);
    private final Property<Text> messageBody = new Property<>();
    private final Property<Text> messageHeader = new Property<>();
    private final Property<String> messageImage = new Property<>();
    private final Property<ButtonText> messageOkButton = new Property<>();
    private final Property<ButtonText> messageCancelButton = new Property<>();

    private final DroneTabModel father;

    public MessageInfoModel(DroneTabModel father) {
        this.father = father;
    }

    public BooleanProperty getIsDisplayed() {
        return isDisplayed;
    }

    public Property<Text> getMessageBody() {
        return messageBody;
    }

    public Property<Text> getMessageHeader() {
        return messageHeader;
    }

    public Property<String> getMessageImageName() {
        return messageImage;
    }

    public Property<ButtonText> getMessageOkButton() {
        return messageOkButton;
    }

    public Property<ButtonText> getMessageCancelButton() {
        return messageCancelButton;
    }

    public void showMessage(Text body, Text header, ButtonText okButton, ButtonText cancelButton, String imageName){

        isDisplayed.set(true);
        messageBody.set(body);
        messageHeader.set(header);
        messageOkButton.set(okButton);
        messageCancelButton.set(cancelButton);
        messageImage.set(imageName);
    }

    public void showMessage(Text body, Text header, ButtonText okButton, ButtonText cancelButton){

        isDisplayed.set(true);
        messageBody.set(body);
        messageHeader.set(header);
        messageOkButton.set(okButton);
        messageCancelButton.set(cancelButton);
        messageImage.set(null);
    }

    public void showTakeOffMessage(){

        showMessage(
                new Text("Ensure it is safe to take-off.\nAircraft will rise to 1.2 M and hover.", Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_text_type), Color.WHITE, Color.TRANSPARENT),
                new Text("TakeOff?", Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_header_text_type), Color.parseColor(DefultsColors.ORANGE) , Color.TRANSPARENT),
                new ButtonText("GO!", Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_text_type), Color.BLACK, Color.parseColor(DefultsColors.TAKEOFF_GREEN),
                        new Function<MotionEvent, Boolean>() {
                            @Override
                            public Boolean apply(MotionEvent input) {

                                father.getDroneTasks().takeOff(50);

                                isDisplayed.set(false);
                                return false;
                            }

                            private boolean containTaskBlocker(DroneController controller, FlightTaskType taskType){

                                for(FlightTaskBlockerType taskBlocker : controller.flightTasks().tasksBlockers()){
                                    if(taskBlocker.affectedTasks().contains(taskType)){
                                        return true;
                                    }
                                }
                                return false;
                            }
                        }),
                standartCancelButton(),
                "btn_takeoff_orange");

        father.selected().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                if(newValue == false){
                    isDisplayed.set(false);
                    observation.remove();
                }
            }
        });
    }

    public void showWarningMessage(String message, String header, Function<MotionEvent, Boolean> buttonAction){
        showMessage(
                new Text(message,Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_text_type),Color.WHITE,Color.TRANSPARENT),
                new Text(header,Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_header_text_type),Color.parseColor(DefultsColors.BLUE_MESSAGE),Color.TRANSPARENT),
                standartOKButton(buttonAction),
                notAvailbleButton(),
                "warning_icon"
        );
    }

    public ButtonText noActiveButton(Text text){
        return new ButtonText(text.getText(), text.getTextSize(), text.getColor(), text.getColor(), new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                isDisplayed.set(false);
                return null;
            }
        });
    }

    public ButtonText notAvailbleButton(){
        return new ButtonText("", 0, 0, 0, null);
    }



    public ButtonText standartCancelButton(){
        return new ButtonText("Cancel", Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_text_type), Color.WHITE, Color.GRAY, new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                isDisplayed.set(false);
                return false;
            }
        });
    }

    public ButtonText standartOKButton(Function<MotionEvent, Boolean> buttonAction){
        return new ButtonText("OK", Text.textType.getDefaultTextType(TextType.DefaultTextType.menu_text_type), Color.WHITE, Color.GRAY, buttonAction);
    }
}
