package com.ssakura49.sakuratinker.core.library.events;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;

public class LoadRegistryClassEvent extends Event implements IModBusEvent {
    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public boolean hasResult() {
        return false;
    }
}
