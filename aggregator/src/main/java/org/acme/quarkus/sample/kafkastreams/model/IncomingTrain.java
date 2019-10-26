package org.acme.quarkus.sample.kafkastreams.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@RegisterForReflection
public class IncomingTrain {

    public String trainId;
    public String trainName;
    
    public int _goto;
    public long togo;
}
