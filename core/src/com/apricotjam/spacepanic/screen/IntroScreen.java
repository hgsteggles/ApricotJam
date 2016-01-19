package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Assets;
import com.apricotjam.spacepanic.art.MapArt;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.art.Particles;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.MovementComponent;
import com.apricotjam.spacepanic.components.ParticleEffectComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.apricotjam.spacepanic.components.TweenSpec.Cycle;
import com.apricotjam.spacepanic.gameelements.GameSettings;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.misc.EntityUtil;
import com.apricotjam.spacepanic.systems.AnimatedShaderSystem;
import com.apricotjam.spacepanic.systems.AnimationSystem;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.MovementSystem;
import com.apricotjam.spacepanic.systems.ParticleSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.apricotjam.spacepanic.systems.ScrollSystem;
import com.apricotjam.spacepanic.systems.ShaderLightingSystem;
import com.apricotjam.spacepanic.systems.SoundSystem;
import com.apricotjam.spacepanic.systems.TickerSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;

public class IntroScreen extends BasicScreen {
	private RandomXS128 rng = new RandomXS128(0);
	
	private final float TIME_UNTIL_SHIP = 1f;
	private final float SHIP_DURATION = 6f;
	private final float TRAIL_TIME = 3.1f;
	private final float BACKGROUND_SPIN_DURATION = 4f;
	
	private final float WINDOW_OPEN_DURATION = 3f;
	
	private TransformComponent shipTransComp;
	private TransformComponent asteroidTransComp;
	
	private final float NEXT_EXPLOSION_TIME = 4f;
	private float explosionTimeAccum = 0;
	private boolean explosion2 = false;
	private boolean explosion3 = false;
	
	private boolean shipHit = false;

	private Entity background;
	
	public IntroScreen(SpacePanic spacePanic, Entity background) {
		super(spacePanic);
		
		for (int ir = 0; ir < 10; ++ir)
			rng.setSeed(rng.nextLong());

		this.background = EntityUtil.clone(background);
		addBackgroundTween(this.background);
		add(this.background);
		
		Entity asteroid = createAsteroid();
		asteroidTransComp = ComponentMappers.transform.get(asteroid);
		add(asteroid);
		
		add(createEndEntity());
		
		if (GameSettings.getIntroSkippable()) {
			add(createSkipEntity());
			add(createSkipText());
		}
		else {
			GameSettings.setIntroSkippable(true);
		}

		add(new MovementSystem());
		add(new ScrollSystem());
		add(new ClickSystem());
		add(new TweenSystem());
		add(new AnimationSystem());
		add(new AnimatedShaderSystem());
		add(new ShaderLightingSystem());
		add(new TickerSystem());
		add(new SoundSystem());
		add(new ParticleSystem());
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		if (!shipHit) {
			if (asteroidTransComp.position.y < -1f) {
				shipHit = true;
				createShip();
				
				Entity ship = createShip();
				shipTransComp = ComponentMappers.transform.get(ship);
				add(ship);
				
				add(createWindow(shipTransComp));
				add(createPlayer(shipTransComp));
				
				add(createBurner(shipTransComp, 0f, -2.5f, -1f, 0.03f));
				add(createBurner(shipTransComp, -2.5f, -4f, -1f, 0.01f));
				add(createBurner(shipTransComp, 2.5f, -4f, -1f, 0.01f));
				
				add(createExplosion(shipTransComp, -2.5f, 1f, 3f, 0.5f));
			}
		}
		else {
			if (!explosion2) {
				explosionTimeAccum += delta;
				if (explosionTimeAccum > NEXT_EXPLOSION_TIME) {
					explosionTimeAccum = 0;
					add(createExplosion(shipTransComp, -2.3f, -0.5f, 3f, 0.5f));
					explosion2 = true;
				}
			}
			else if (!explosion3) {
				explosionTimeAccum += delta;
				if (explosionTimeAccum > 0.5f*NEXT_EXPLOSION_TIME) {
					explosionTimeAccum = 0;
					add(createExplosion(shipTransComp, -0.5f, 0f, 3f, 1.2f));
					explosion3 = true;
				}
			}
		}
	}
	
	private Entity createSkipText() {
		Entity entity = new Entity();
		
		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "press to skip";
		fontComp.color.set(1.0f, 1.0f, 1.0f, 0f);
		fontComp.centering = false;
		entity.add(fontComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0.01f*BasicScreen.WORLD_WIDTH, 0.01f*BasicScreen.WORLD_HEIGHT, 10f);
		entity.add(transComp);
		
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0f;
		tweenSpec.end = 1f;
		tweenSpec.period = (TIME_UNTIL_SHIP + SHIP_DURATION + TRAIL_TIME)/10f;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.bitmapfont.get(e).color.a = a;
			}
			
			@Override
			public void endTween(Entity e) {
				TweenSpec ts = new TweenSpec();
				ts.period = (TIME_UNTIL_SHIP + SHIP_DURATION + TRAIL_TIME)*8f/10f;
				ts.tweenInterface = new TweenInterface() {
					@Override
					public void applyTween(Entity e, float a) {
					}
					
					@Override
					public void endTween(Entity ee) {
						TweenSpec tss = new TweenSpec();
						tss.start = 1f;
						tss.end = 0f;
						tss.period = (TIME_UNTIL_SHIP + SHIP_DURATION + TRAIL_TIME)/10f;
						tss.tweenInterface = new TweenInterface() {
							@Override
							public void applyTween(Entity eee, float a) {
								ComponentMappers.bitmapfont.get(eee).color.a = a;
							}
						};
						ComponentMappers.tween.get(ee).tweenSpecs.add(tss);
					}
				};
				ComponentMappers.tween.get(e).tweenSpecs.add(ts);;
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
		
		return entity;
	}
	
	private Entity createSkipEntity() {
		Entity entity = new Entity();
		
		ClickComponent clickComp = new ClickComponent();
		clickComp.shape = new Rectangle(0, 0, BasicScreen.WORLD_WIDTH, BasicScreen.WORLD_HEIGHT);
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				startGame();
			}
		};
		entity.add(clickComp);
		
		return entity;
	}
	
	private Entity createWindow(TransformComponent parentTrans) {
		final float ypos = 1.95f;
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = MiscArt.podScreenRegion;
		float scale = 1f;
		texComp.size.set(scale*texComp.region.getRegionWidth()*RenderingSystem.PIXELS_TO_WORLD, scale*texComp.region.getRegionHeight()*RenderingSystem.PIXELS_TO_WORLD);
		entity.add(texComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = 0;
		transComp.position.y = ypos;
		transComp.position.z = 2.0f;
		transComp.rotation = 0f;
		transComp.parent = parentTrans;
		entity.add(transComp);
		
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 2f;
		tweenSpec.end = 0f;
		tweenSpec.period = WINDOW_OPEN_DURATION;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				if (a < 1f) {
					TextureComponent txc = ComponentMappers.texture.get(e);
					txc.size.x = a*txc.region.getRegionWidth()*RenderingSystem.PIXELS_TO_WORLD;
					
					TransformComponent tc = ComponentMappers.transform.get(e);
					tc.position.y = ypos + (txc.size.x - txc.region.getRegionWidth()*RenderingSystem.PIXELS_TO_WORLD)/2f;
				}
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
		
		return entity;
	}
	
	private Entity createPlayer(TransformComponent parentTrans) {
		final float ypos = 2.0f;
		
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = MiscArt.playerRegion;
		float scale = 0.5f;
		texComp.size.set(scale*texComp.region.getRegionWidth()*RenderingSystem.PIXELS_TO_WORLD, scale*texComp.region.getRegionHeight()*RenderingSystem.PIXELS_TO_WORLD);
		entity.add(texComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = 0;
		transComp.position.y = ypos;
		transComp.position.z = 2.0f;
		transComp.rotation = 90f;
		transComp.parent = parentTrans;
		entity.add(transComp);
		
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = -1;
		tweenSpec.end = 1;
		tweenSpec.loops = 8;
		tweenSpec.period = WINDOW_OPEN_DURATION/tweenSpec.loops;
		tweenSpec.interp = Interpolation.sine;
		tweenSpec.reverse = true;
		tweenSpec.cycle = Cycle.LOOP;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				tc.position.y = ypos + 0.02f*a;
			}

		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
		
		tweenComp = new TweenComponent();
		tweenSpec = new TweenSpec();
		tweenSpec.period = WINDOW_OPEN_DURATION;
		tweenSpec.cycle = Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
			}
			
			@Override
			public void endTween(Entity e) {
				ComponentMappers.texture.get(e).region = MiscArt.playerMoveRegion;
				
				MovementComponent moveComp = new MovementComponent();
				float speed = 1.0f/TIME_UNTIL_SHIP;
				moveComp.linearVelocity.x = 1.5f;
				moveComp.linearVelocity.y = 0.1f;
				e.add(moveComp);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
		
		return entity;
	}
	
	private Entity createAsteroid() {
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = MapArt.asteroids.get(0);
		texComp.size.set(1f, 1f);
		entity.add(texComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = (3f/4f)*BasicScreen.WORLD_WIDTH + texComp.size.x/2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT + texComp.size.y/2f;
		transComp.position.z = 2.0f;
		entity.add(transComp);
		
		float diffx = transComp.position.x - 0.5f*BasicScreen.WORLD_WIDTH;
		float diffy = transComp.position.y;
		
		MovementComponent moveComp = new MovementComponent();
		float speed = 1.0f/TIME_UNTIL_SHIP;
		moveComp.linearVelocity.x = -speed*diffx;
		moveComp.linearVelocity.y = -speed*diffy;
		moveComp.rotationalVelocity = 1f;
		entity.add(moveComp);
		
		return entity;
	}
	
	private Entity createShip() {
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = MiscArt.shipRegion;
		texComp.size.set(texComp.region.getRegionWidth()*RenderingSystem.PIXELS_TO_WORLD, texComp.region.getRegionHeight()*RenderingSystem.PIXELS_TO_WORLD);
		entity.add(texComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = - texComp.size.y;
		transComp.position.z = 1.0f;
		transComp.rotation -= 90f;
		entity.add(transComp);
		
		MovementComponent moveComp = new MovementComponent();
		moveComp.linearVelocity.y = (BasicScreen.WORLD_HEIGHT + texComp.size.y)/SHIP_DURATION;
		entity.add(moveComp);
		
		return entity;
	}
	
	private Entity createBurner(TransformComponent parentComp, float x, float y, float z,  float scale) {
		Entity entity = new Entity();
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;
		transComp.position.z = z;
		transComp.rotation = 0f;
		transComp.parent = parentComp;
		entity.add(transComp);
		
		ParticleEffectComponent particleComp = new ParticleEffectComponent();
		particleComp.effect = new ParticleEffect(Assets.particles.get("burner"));
		particleComp.effect.scaleEffect(scale);
		TransformComponent totalTrans = transComp.getTotalTransform();
		particleComp.effect.setPosition(totalTrans.position.x, totalTrans.position.y);
		particleComp.effect.start();
		entity.add(particleComp);
		
		return entity;
	}
	
	private Entity createExplosion(TransformComponent parentComp, float x, float y, float z, float scale) {
		Entity entity = new Entity();
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(x, y, z);
		transComp.parent = parentComp;
		entity.add(transComp);
		
		ParticleEffectComponent particleComp = new ParticleEffectComponent();
		particleComp.effect = new ParticleEffect(Assets.particles.get("explosion"));
		particleComp.effect.scaleEffect(0.01f*scale);
		TransformComponent totalTrans = transComp.getTotalTransform();
		particleComp.effect.setPosition(totalTrans.position.x, totalTrans.position.y);
		Particles.setContinuous(particleComp.effect, true);
		particleComp.effect.start();
		entity.add(particleComp);
		
		return entity;
	}
	
	private void addBackgroundTween(Entity background) {
		TweenComponent tweenComp = ComponentMappers.tween.get(background);
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.period = TIME_UNTIL_SHIP + SHIP_DURATION + TRAIL_TIME;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
			}
			
			@Override
			public void endTween(Entity e) {
				TweenComponent twc = new TweenComponent();
				TweenSpec ts = new TweenSpec();
				ts.period = BACKGROUND_SPIN_DURATION/2f;
				ts.cycle = Cycle.LOOP;
				ts.loops = 2;
				ts.reverse = true;
				ts.interp = Interpolation.sine;
				ts.tweenInterface = new TweenInterface() {
					@Override
					public void applyTween(Entity e, float a) {
						ComponentMappers.movement.get(e).rotationalVelocity = 48.0f*a;
						ComponentMappers.scroll.get(e).speed.x = 8.0f*a;
					}
					
					@Override
					public void endTween(Entity e) {
					}
				};
				twc.tweenSpecs.add(ts);
				e.add(twc);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
	}
	
	private Entity createEndEntity() {
		Entity entity = new Entity();
		
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.period = TIME_UNTIL_SHIP + SHIP_DURATION + TRAIL_TIME + BACKGROUND_SPIN_DURATION + 0.01f;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
			}
			
			@Override
			public void endTween(Entity e) {
				startGame();
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
		
		return entity;
	}

	private void startGame() {
		ComponentMappers.movement.get(background).rotationalVelocity = 0f;
		ComponentMappers.scroll.get(background).speed.x = 0f;
		ComponentMappers.tween.get(background).tweenSpecs.clear();
		spacePanic.setScreen(new GameScreen(spacePanic, background));
	}

	@Override
	public void backPressed() {
		startGame();
	}
}
