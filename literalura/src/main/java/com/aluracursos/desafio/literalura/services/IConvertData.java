package com.aluracursos.desafio.literalura.services;

public interface IConvertData {
    <T> T takeData(String json, Class<T> clase);
}
