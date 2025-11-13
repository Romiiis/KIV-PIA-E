package com.romiiis.event;

import com.romiiis.domain.Project;

public record AdminMessageEvent(Project project,
                                boolean sendToCustomer,
                                boolean sendToTranslator,
                                String text){
}
