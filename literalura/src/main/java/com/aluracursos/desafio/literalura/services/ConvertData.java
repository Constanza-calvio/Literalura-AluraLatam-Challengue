package com.aluracursos.desafio.literalura.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertData implements IConvertData{
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T takeData(String json, Class<T> clase) {
        try {
            return mapper.readValue(json.toString(), clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
