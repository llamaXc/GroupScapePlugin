package net.runelite.client.plugins.groupscape.messages;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.groupscape.pojos.Drop;

import java.util.ArrayList;
import java.util.List;

public class DropMessage {
        @Getter @Setter
        String source;

        @Getter @Setter
        int combatLevel;

        @Getter @Setter
        List<Drop> drops = new ArrayList<Drop>();
}
