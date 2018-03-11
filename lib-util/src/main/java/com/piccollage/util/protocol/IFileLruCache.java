package com.piccollage.util.protocol;

import java.io.File;
import java.io.IOException;

/**
 * Created by prada on 03/10/2017.
 */

public interface IFileLruCache<DATA> {
    void open() throws IOException;
    void close() throws IOException;

    File put(String key, DATA data) throws IOException;
    File get(String key) throws IOException ;

    boolean isOpened();
}
