public class ReferenceObject {

    private SimpleObject fieldObj;

    //no-arg constructor (need for Deserializer)
    public ReferenceObject(){}

    public ReferenceObject(SimpleObject simpleObj){
        fieldObj = simpleObj;
    }
}
