package com.apricotjam.spacepanic.art;

import com.apricotjam.spacepanic.systems.PipeSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

public class PipeGameArt {

	public static class RotatedRegionData {
		public AtlasRegion region;
		public float rotation;
	}
	
	public static class RotatedAnimationData {
		public Array<AtlasRegion> regions = null;
		public float rotation;
	}
	
	public static IntMap<RotatedRegionData> pipeRegions = new IntMap<RotatedRegionData>();
	public static IntMap<IntMap<RotatedAnimationData>> fluidRegions = new IntMap<IntMap<RotatedAnimationData>>();
	public static TextureRegion ledBG;
	public static TextureRegion pipeBorder, pipeBorderTop;
	public static TextureRegion pipeBG;
	
	public static void load(TextureAtlas atlas) {
		// Load led bg.
		TextureRegion whitePixel = atlas.findRegion("white_pixel");
		
		ledBG = whitePixel;
		
		// Load pipe tile backgrounds.
		pipeBG = whitePixel;
		pipeBorder = atlas.findRegion("pipe-border");
		pipeBorderTop = atlas.findRegion("pipe-border-top");
		
		// Load all pipes and fluid animations.
		for (int i = 0; i < 16; ++i) {
			// Load pipes.
			RotatedRegionData rotRegComp = new RotatedRegionData();
			rotRegComp.region = atlas.findRegion("pipe"+Integer.toString(i));
			pipeRegions.put(i, rotRegComp);
			
			// Load Fluids.
			IntMap<RotatedAnimationData> fluidEntryRegions = new IntMap<RotatedAnimationData>();
			fluidRegions.put(i, fluidEntryRegions);
			
			for (int ientry = 0; ientry < 4; ++ientry) {
				if (PipeSystem.connectedAtIndex((byte)(i), ientry) || (PipeSystem.numberConnections((byte)(i)) == 1 && ientry == PipeSystem.oppositeDirectionIndex(PipeSystem.directionFromMask((byte)(i))))) {
					RotatedAnimationData rotAnimComp = new RotatedAnimationData();
					rotAnimComp.regions = atlas.findRegions("fluid"+Integer.toString(i)+"-"+Integer.toString(ientry));
					fluidEntryRegions.put(ientry,  rotAnimComp);
				}
			}
		}
		
		// Any pieces missing? Find rotated versions.
		for (int i = 0; i < 16; ++i) {
			// Pipes.
			RotatedRegionData rotData = pipeRegions.get(i);
			if (rotData.region == null) {
				for (int irot = 0; irot < 3; ++irot) {
					rotData.rotation += 90f;
					AtlasRegion region = pipeRegions.get(PipeSystem.rotateMask((byte)(i))).region;
					if (region != null) {
						rotData.region = region;
						break;
					}
				}
			}
			
			// Fluids.
			IntMap<RotatedAnimationData> fluidEntryRegions = fluidRegions.get(i);
			for (Entry<RotatedAnimationData> rotAnimData : fluidEntryRegions.entries()) {
				//RotatedAnimationData rotAnimData = fluidRegions.get(i);
				
				if (rotAnimData.value.regions.size == 0) {
					for (int irot = 1; irot < 4; ++irot) {
						rotAnimData.value.rotation += 90f;
						RotatedAnimationData foundAnimRotData = fluidRegions.get(PipeSystem.rotateMaskN((byte)(i), irot)).get((rotAnimData.key + irot)%4);
						if (foundAnimRotData.regions.size != 0) {
							rotAnimData.value.regions = foundAnimRotData.regions;
							rotAnimData.value.rotation += foundAnimRotData.rotation;
							break;
						}
					}
				}
			}
		}
		
		// Reverse direction fluid animations.
		for (int i = 0; i < 16; ++i) {
			IntMap<RotatedAnimationData> fluidEntryRegions = fluidRegions.get(i);
			for (int ientry = 0; ientry < 4; ++ientry) {
				if (PipeSystem.connectedAtIndex((byte)(i), ientry)) {
					RotatedAnimationData rotAnimData = fluidEntryRegions.get(ientry);
					if (PipeSystem.numberConnections((byte)(i)) != 1 && fluidEntryRegions.get(ientry).regions.size == 0) {
						if (PipeSystem.connectedAtIndex((byte)(i), (ientry+2)%4)) {
							RotatedAnimationData foundAnimRotData = fluidEntryRegions.get((ientry+2)%4);
							rotAnimData.regions = foundAnimRotData.regions;
							rotAnimData.rotation = foundAnimRotData.rotation + 180f;
						}
						else {
							RotatedAnimationData foundAnimRotData = fluidEntryRegions.get(PipeSystem.directionFromMask((byte)(i - (1 << ientry))));
							
							
							Array<AtlasRegion> flippedRegions = new Array<AtlasRegion>();
							for (int iregion = 0; iregion < foundAnimRotData.regions.size; ++iregion) {
								AtlasRegion flippedRegion = new AtlasRegion(foundAnimRotData.regions.get(iregion));
								flippedRegion.flip(true, false);
								flippedRegions.add(flippedRegion);
							}
							rotAnimData.regions = flippedRegions;
							rotAnimData.rotation = foundAnimRotData.rotation + 90f;
						}
					}
				}
			}
		}
	}
	
	public static void dipose() {
	}
}
