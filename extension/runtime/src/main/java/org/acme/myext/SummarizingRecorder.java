package org.acme.myext;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class SummarizingRecorder {

    public void summarizeBootstrap(String text) {
        System.out.println(text);
    }
}
