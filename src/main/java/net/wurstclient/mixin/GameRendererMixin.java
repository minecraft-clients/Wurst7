/*
 * Copyright (C) 2014 - 2020 | Alexander01998 | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.wurstclient.WurstClient;
import net.wurstclient.events.CameraTransformViewBobbingListener.CameraTransformViewBobbingEvent;
import net.wurstclient.events.HitResultRayTraceListener.HitResultRayTraceEvent;
import net.wurstclient.events.RenderListener.RenderEvent;
import net.wurstclient.hacks.NameTagsHack;
import net.wurstclient.mixinterface.IGameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
	implements AutoCloseable, SynchronousResourceReloadListener, IGameRenderer
{
	@Redirect(
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/GameRenderer;bobView(F)V",
			ordinal = 0),
		method = {"applyCameraTransformations(F)V"})
	private void onCameraTransformViewBobbing(GameRenderer gameRenderer,
		float partalTicks)
	{
		CameraTransformViewBobbingEvent event =
			new CameraTransformViewBobbingEvent();
		WurstClient.INSTANCE.getEventManager().fire(event);
		
		if(event.isCancelled())
			return;
		
		bobView(partalTicks);
	}
	
	@Inject(at = {@At(value = "FIELD",
		target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
		opcode = Opcodes.GETFIELD,
		ordinal = 0)}, method = {"renderCenter(FJ)V"})
	private void onRenderCenter(float partialTicks, long finishTimeNano,
		CallbackInfo ci)
	{
		RenderEvent event = new RenderEvent(partialTicks);
		WurstClient.INSTANCE.getEventManager().fire(event);
	}
	
	@Redirect(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/options/GameOptions;fov:D",
			opcode = Opcodes.GETFIELD,
			ordinal = 0),
		method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)D"})
	private double getFov(GameOptions options)
	{
		return WurstClient.INSTANCE.getOtfs().zoomOtf
			.changeFovBasedOnZoom(options.fov);
	}
	
	@Inject(at = {@At(value = "INVOKE",
		target = "Lnet/minecraft/entity/Entity;getCameraPosVec(F)Lnet/minecraft/util/math/Vec3d;",
		opcode = Opcodes.INVOKEVIRTUAL,
		ordinal = 0)}, method = {"updateTargetedEntity(F)V"})
	private void onHitResultRayTrace(float float_1, CallbackInfo ci)
	{
		HitResultRayTraceEvent event = new HitResultRayTraceEvent(float_1);
		WurstClient.INSTANCE.getEventManager().fire(event);
	}
	
	@Redirect(
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F",
			ordinal = 0),
		method = {"applyCameraTransformations(F)V"})
	private float wurstNauseaLerp(float delta, float first, float second)
	{
		if(!WurstClient.INSTANCE.getHax().antiWobbleHack.isEnabled())
			return MathHelper.lerp(delta, first, second);
		
		return 0;
	}
	
	@Inject(at = {@At("HEAD")},
		method = {"bobViewWhenHurt(F)V"},
		cancellable = true)
	private void onBobViewWhenHurt(float f, CallbackInfo ci)
	{
		if(WurstClient.INSTANCE.getHax().noHurtcamHack.isEnabled())
			ci.cancel();
	}
	
	@Inject(
		at = @At(value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;scalef(FFF)V",
			ordinal = 0),
		method = {
			"renderFloatingText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;FFFIFFZ)V"})
	private static void onRenderFloatingText(TextRenderer textRenderer,
		String text, float x, float y, float z, int verticalOffset, float yaw,
		float pitch, boolean translucent, CallbackInfo ci)
	{
		NameTagsHack nameTagsHack = WurstClient.INSTANCE.getHax().nameTagsHack;
		if(!nameTagsHack.isEnabled())
			return;
		
		float scale = 0.025F;
		double distance = Math.sqrt(x * x + y * y + z * z);
		
		if(distance > 10)
			scale *= distance / 10;
		
		GlStateManager.scalef(-scale, -scale, scale);
		
		// undo vanilla scaling
		GlStateManager.scalef(-40, -40, 40);
	}
	
	@Shadow
	private void bobView(float partalTicks)
	{
		
	}
	
	@Override
	public void loadWurstShader(Identifier identifier)
	{
		loadShader(identifier);
	}
	
	@Shadow
	private void loadShader(Identifier identifier)
	{
		
	}
}
