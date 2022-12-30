package vip.radium.module.impl.player;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import vip.radium.event.impl.world.TickEvent;
import vip.radium.module.Module;
import vip.radium.module.ModuleCategory;
import vip.radium.module.ModuleInfo;
;

@ModuleInfo(label = "Fast Place", category = ModuleCategory.PLAYER)
public class FastPlace extends Module {

	@EventLink
	public final Listener<TickEvent> onTickEvent = event -> {
		mc.rightClickDelayTimer = 0;
	};
}
