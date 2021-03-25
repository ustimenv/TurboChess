package es.ucm.fdi.iw.turbochess.model;

/**
 * Used to json-ize objects
 */
public interface Transferable<T> {
    T toTransfer();
}