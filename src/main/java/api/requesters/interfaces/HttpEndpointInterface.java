package api.requesters.interfaces;

public interface HttpEndpointInterface {
    Object post();
    Object get();
    Object put();
    Object delete();
}
