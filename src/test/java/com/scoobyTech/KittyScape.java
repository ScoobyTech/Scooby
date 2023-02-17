package com.scoobyTech;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class KittyScape
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(KittyScapePlugin.class);
		RuneLite.main(args);
	}
}