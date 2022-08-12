package baubles.common;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesCapabilities.CapabilityBaubles;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.modcom.BaublesModCom;
import baubles.client.ClientProxy;
import baubles.common.event.CommandBaubles;
import baubles.common.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static net.minecraftforge.registries.ForgeRegistry.DEBUG;

@Mod(
		modid = Baubles.MODID, 
		name = Baubles.MODNAME, 
		version = Baubles.VERSION, 
		guiFactory = "baubles.client.gui.BaublesGuiFactory",
		dependencies = "required-after:forge@[14.23.5.2860,);")
public class Baubles {

	public static final String MODID = "baubles";
	public static final String MODNAME = "Baubles";
	public static final String VERSION = "1.12.2-1.0.6.1";

	@SidedProxy(clientSide = "baubles.client.ClientProxy", serverSide = "baubles.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value=Baubles.MODID)
	public static Baubles instance;

	public File modDir;

	public static final Logger log = LogManager.getLogger(MODID.toUpperCase());
	public static final int GUI = 0;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modDir = event.getModConfigurationDirectory();

		try {
			Config.initialize(event.getSuggestedConfigurationFile());
		} catch (Exception e) {
			Baubles.log.fatal("Baubles has a problem loading it's configuration");
		} finally {
			if (Config.config != null) {
				Config.save();
			}
		}


		CapabilityManager capabilityManager = CapabilityManager.INSTANCE;

		capabilityManager.register
				(
						IBaublesItemHandler.class,
						new CapabilityBaubles<>(),
						BaublesContainer::new
				);

		capabilityManager.register
				(
						IBauble.class,
						new BaublesCapabilities.CapabilityItemBaubleStorage(),
						() -> new BaubleItem(BaubleType.TRINKET)
				);

		proxy.registerEventHandlers();
		PacketHandler.init();

		Config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.init();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandBaubles());
	}

	private void process(FMLInterModComms.IMCEvent evt) {
		BaubleType.processBaubleTypes(evt.getMessages().stream(), evt.getMessages().stream());
	}

	private static void send(String id, Object msg) {
		FMLInterModComms.sendMessage(MODID, id, msg.toString());
	}
}
