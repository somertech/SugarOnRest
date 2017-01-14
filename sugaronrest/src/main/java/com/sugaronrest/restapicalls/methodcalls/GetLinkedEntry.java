package com.sugaronrest.restapicalls.methodcalls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sugaronrest.ErrorResponse;
import com.sugaronrest.restapicalls.responses.ReadLinkedEntryResponse;
import com.sugaronrest.utils.JsonObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kolao_000 on 2016-12-22.
 */
public class GetLinkedEntry {

    /**
     * Login to SugarCrm via REST API call
     *
     *  @param sessionId LoginRequest object
     *  @return LoginResponse object
     */
    public static ReadLinkedEntryResponse run(String url, String sessionId, String moduleName, String identifier, List<String> selectFields, List<Object> linkedSelectFields)  {

        ReadLinkedEntryResponse readLinkedEntryResponse = null;
        String jsonRequest = new String();
        String jsonResponse = new String();

        ObjectMapper mapper = JsonObjectMapper.getMapper();

        try {
            Map<String, Object> requestData = new LinkedHashMap<String, Object>();
            requestData.put("session", sessionId);
            requestData.put("module_name", moduleName);
            requestData.put("id", identifier);
            requestData.put("select_fields", selectFields);
            boolean linkedInfoNotSet = ((linkedSelectFields == null) || (linkedSelectFields.size() == 0));
            requestData.put("link_name_to_fields_array",  linkedInfoNotSet ? StringUtils.EMPTY : linkedSelectFields);
            requestData.put("track_view", false);

            String jsonRequestData = mapper.writeValueAsString(requestData);

            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put("method", "get_entry");
            request.put("input_type", "json");
            request.put("response_type", "json");
            request.put("rest_data", requestData);

            jsonRequest = mapper.writeValueAsString(request);

            request.put("rest_data", jsonRequestData);

            HttpResponse response = Unirest.post(url)
                    .fields(request)
                    .asString();

            if (response == null)
            {
                readLinkedEntryResponse = new ReadLinkedEntryResponse();
                ErrorResponse errorResponse = ErrorResponse.format("An error has occurred!", "No data returned.");
                readLinkedEntryResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                readLinkedEntryResponse.setError(errorResponse);
            }

            jsonResponse = response.getBody().toString();

            if (StringUtils.isNotBlank(jsonResponse)) {
                try {
                    readLinkedEntryResponse = mapper.readValue(jsonResponse, ReadLinkedEntryResponse.class);
                }
                catch (Exception exception)
                {
                    ErrorResponse errorResponse = mapper.readValue(jsonResponse, ErrorResponse.class);
                    errorResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                    errorResponse.setTrace(exception);
                    readLinkedEntryResponse = new ReadLinkedEntryResponse();
                    readLinkedEntryResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                    readLinkedEntryResponse.setError(errorResponse);
                }
            }

            if (readLinkedEntryResponse == null) {
                readLinkedEntryResponse = new ReadLinkedEntryResponse();
                String message = String.format("Object type %S to json conversion failed - response data is invalid!", readLinkedEntryResponse.getClass().getSimpleName());
                ErrorResponse errorResponse = ErrorResponse.format("An error has occurred!", message);
                readLinkedEntryResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                readLinkedEntryResponse.setError(errorResponse);
            }
        }
        catch (Exception exception) {
            readLinkedEntryResponse = new ReadLinkedEntryResponse();
            ErrorResponse errorResponse = ErrorResponse.format(exception, exception.getMessage());
            readLinkedEntryResponse.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            errorResponse.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            readLinkedEntryResponse.setError(errorResponse);
        }

        readLinkedEntryResponse.setJsonRawRequest(jsonRequest);
        readLinkedEntryResponse.setJsonRawResponse(jsonResponse);

        return readLinkedEntryResponse;
    }
}