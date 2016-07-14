package com.teamwizardry.librarianlib.client.fx.particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public abstract class ParticleRenderQueue<T extends QueuedParticle> {

    protected List<T> renderQueue = new ArrayList<>();
    protected boolean sort = false;
    
    public ParticleRenderQueue(boolean sort) {
    	this.sort = sort;
        ParticleRenderDispatcher.INSTANCE.addQueue(this);
    }

    public void add(T particle) {
        renderQueue.add(particle);
    }

    public abstract String name();

    public void dispatchQueuedRenders(Tessellator tessellator) {
    	if(renderQueue.size() == 0) return;
    	if(sort) {
    		float partialTicks = renderQueue.get(0).partialTicks;
	    	EntityPlayer entity = Minecraft.getMinecraft().thePlayer;
	    	
	    	double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
	        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
	        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
	        
	        Vec3d playerPos = new Vec3d(d0, d1, d2);
	        Vec3d look = playerPos.add(  entity.getLook(partialTicks)  );
	        projectToRay(playerPos, look, Vec3d.ZERO);
	        
	        for (T t : renderQueue) {
	        	Vec3d v = t.getPos();
	        	v = new Vec3d(v.xCoord, v.yCoord, v.zCoord);
	        	v = projectToRay(playerPos, look, v);
				t.distFromPlayer = v.lengthVector();
			}
	    	Collections.sort(renderQueue, (a, b) -> { return -Double.compare(a.distFromPlayer, b.distFromPlayer); });
    	}
    	renderParticles(tessellator);
    }

    public abstract void renderParticles(Tessellator tessellator);
    
    private Vec3d projectToRay(Vec3d a, Vec3d b, Vec3d p) {
    	Vec3d ap = p.subtract(a);
    	Vec3d ab = b.subtract(a);
    	return a.add( ab.scale(ap.dotProduct(ab)/ab.dotProduct(ab)) );
    }
}
