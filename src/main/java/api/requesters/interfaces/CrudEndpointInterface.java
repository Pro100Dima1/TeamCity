package api.requesters.interfaces;

import api.models.BaseModel;

import java.util.Map;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object post();

    Object post(BaseModel model, Map<String, ?> pathParams);

    Object get();

    Object get(Map<String, ?> pathParams);

    Object update(BaseModel model);

    Object update(BaseModel model, Map<String, ?> pathParams);

    Object delete();

    Object delete(Map<String, ?> pathParams);
}
