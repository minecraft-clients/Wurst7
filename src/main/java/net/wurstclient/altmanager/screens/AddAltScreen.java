/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.altmanager.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.chat.TextComponent;
import net.wurstclient.altmanager.AltManager;

public final class AddAltScreen extends AltEditorScreen
{
	private final AltManager altManager;
	
	public AddAltScreen(Screen prevScreen, AltManager altManager)
	{
		super(prevScreen, new TextComponent("New Alt"));
		this.altManager = altManager;
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Add";
	}
	
	@Override
	protected void pressDoneButton()
	{
		altManager.add(getEmail(), getPassword(), false);
		minecraft.openScreen(prevScreen);
	}
}
