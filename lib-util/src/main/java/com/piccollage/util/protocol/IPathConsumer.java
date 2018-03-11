package com.piccollage.util.protocol;

import android.os.Bundle;

public interface IPathConsumer {
    void onPathUpdate(String root, Bundle params);
}
