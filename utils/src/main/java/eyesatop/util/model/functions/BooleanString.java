package eyesatop.util.model.functions;

/**
 * Created by einav on 26/04/2017.
 */
public class BooleanString implements eyesatop.util.Function<Boolean,String> {

    @Override
    public String apply(Boolean input) {
        if(input == null || input == false){
            return "No";
        }
        return "yes";
    }
}
