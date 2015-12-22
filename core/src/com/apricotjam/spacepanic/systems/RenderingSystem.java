package com.apricotjam.spacepanic.systems;

import java.util.Comparator;

import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class RenderingSystem extends IteratingSystem {
	static final float WORLD_WIDTH = 10;
	static final float WORLD_HEIGHT = 10;
	
	private OrthographicCamera camera;
	
	private ComponentMapper<TransformComponent> transformCM;
	private ComponentMapper<TextureComponent> textureCM;
	
	private Comparator<Entity> comparator;
	
	public RenderingSystem(SpriteBatch batch) {
		super(Family.all(TransformComponent.class, TextureComponent.class).get());
		
		textureCM = ComponentMapper.getFor(TextureComponent.class);
		transformCM = ComponentMapper.getFor(TransformComponent.class);
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				return (int)Math.signum(transformCM.get(entityB).position.z - transformCM.get(entityA).position.z);
			}
		};
		
		this.batch = batch;
		
		cam = new OrthographicCamera(FRUSTRUM_WIDTH, FRUSTRUM_HEIGHT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		
		pm  = ComponentMapper.getFor(PositionComponent.class);
		rsm = ComponentMapper.getFor(RectangleShape.class);
		csm = ComponentMapper.getFor(CircleShape.class);
		
		paddles = engine.getEntitiesFor(Family.all(PositionComponent.class, ShapeRenderable.class, RectangleShape.class).get());
		balls = engine.getEntitiesFor(Family.all(PositionComponent.class, ShapeRenderable.class, CircleShape.class).get());
	}

	@Override
	public void update(float deltaTime) {
		Gdx.graphics.getGL20().glClearColor( 0.8f, 0.1f, 0.1f, 1 );
		Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.rect(0, 0, Pong.worldWidth, Pong.worldHeight);
		
		for (Entity paddle : paddles) {
			PositionComponent positionable = pm.get(paddle);
			RectangleShape rectangleShape = rsm.get(paddle);
			
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.rect(positionable.position.x, positionable.position.y, rectangleShape.width, rectangleShape.height);
		}
		for (Entity ball : balls) {
			PositionComponent positionable = pm.get(ball);
			CircleShape circleShape = csm.get(ball);
			
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.circle(positionable.position.x, positionable.position.y, circleShape.radius, 100);
		}
		shapeRenderer.end();
	}
}
