/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.wurstclient.WurstClient;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
	@Redirect(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;canSeePlayer(Lnet/minecraft/entity/player/PlayerEntity;)Z",
		ordinal = 0),
		method = {"render(Lnet/minecraft/entity/LivingEntity;FFFFFF)V"})
	private boolean canWurstSeePlayer(LivingEntity e, PlayerEntity player)
	{
		if(WurstClient.INSTANCE.getHax().trueSightHack.isEnabled())
			return false;
		
		return e.canSeePlayer(player);
	}
}
