package com.dgangan.avcom;

import lombok.Data;
import java.util.UUID;

@Data
public class AvPreset {

    private UUID uuid;
    private String name;
    private AvSettings settings;
    private boolean enabled;

    public AvPreset(String name, AvSettings settings){
     this.uuid = UUID.randomUUID();
     this.name = name;
     this.settings = settings;
    }
}
