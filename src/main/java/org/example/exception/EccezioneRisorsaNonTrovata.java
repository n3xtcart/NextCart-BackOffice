package org.example.exception;

public class EccezioneRisorsaNonTrovata extends RuntimeException {
    public EccezioneRisorsaNonTrovata(String message) {
        super(message);
    }
    public EccezioneRisorsaNonTrovata(String message, Throwable cause) {
        super(message, cause);
    }
}