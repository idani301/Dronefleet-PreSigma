package eyesatop.unit.ui.models.massage;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.util.Cancellable;
import eyesatop.util.Function;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by einav on 19/07/2017.
 */

public class LittleMessageViewModel {

    private static final int Timeout = 3;

    private final Activity activity;
    private final View littleMessageView;
    private final TextViewModel littleMessageButton;
    private final TextViewModel littleMessageBody;

    private Future lastTaskSent = null;

    private final ExecutorService littleMessagesExecutor = Executors.newSingleThreadExecutor();

    private Property<String> frontMessage = new Property<>();
    private Property<Integer> remainingSeconds = new Property<>(0);

    private final ArrayList<String> pendingMessages = new ArrayList<>();

    public LittleMessageViewModel(Context context) {
        activity = (Activity)context;

        littleMessageView = activity.findViewById(R.id.includeLittleMessage);
        littleMessageBody = new TextViewModel((TextView) activity.findViewById(R.id.littleMessageBody));
        littleMessageButton = new TextViewModel((TextView) activity.findViewById(R.id.littleMessageConfirm));

        littleMessageView.setVisibility(View.GONE);

        frontMessage.observe(new Observer<String>() {
            @Override
            public void observe(String oldValue, String newValue, Observation<String> observation) {

                if(littleMessageView.getVisibility() != View.VISIBLE){
                    show();
                }

                if(remainingSeconds.value() > 0){
                    pendingMessages.add(oldValue);
                }

                if(lastTaskSent != null) {
                    lastTaskSent.cancel(true);
                }
                remainingSeconds.set(Timeout);
                littleMessageBody.text().set(newValue);

                lastTaskSent = littleMessagesExecutor.submit(new LittleMessageRunnable());
            }
        },UI_EXECUTOR);

        remainingSeconds.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                littleMessageButton.text().set("OK(" + newValue + ")");

                if(newValue == 0){
                    if(pendingMessages.size() > 0){
                        String pendingMessage = pendingMessages.remove(pendingMessages.size()-1);
                        frontMessage.set(pendingMessage);
                    }
                    else{
                        dispose();
                    }
                }

            }
        },UI_EXECUTOR).observeCurrentValue();

        littleMessageButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(lastTaskSent != null){
                    lastTaskSent.cancel(true);
                }
                remainingSeconds.set(0);

                return false;
            }
        });
    }

    public void addNewMessage(String newMessage){
        frontMessage.set(newMessage);
    }

    private void dispose(){
        littleMessageView.setVisibility(View.GONE);
    }

    private void show(){
        littleMessageView.setVisibility(View.VISIBLE);
    }

    private class LittleMessageRunnable implements Runnable {

        @Override
        public void run() {
            int counter = Timeout;
            while(counter > 0){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                counter--;
                remainingSeconds.set(counter);
            }
        }
    }
}
