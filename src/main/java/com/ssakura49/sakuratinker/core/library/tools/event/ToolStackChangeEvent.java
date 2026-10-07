package com.ssakura49.sakuratinker.core.library.tools.event;

import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


@Getter
public class ToolStackChangeEvent extends Event {
    private final IToolStackView tool;

    public ToolStackChangeEvent(IToolStackView tool) {
        this.tool = tool;
    }

}
