package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;


public class Drop extends ApplicationAdapter {
	//files we are referencing
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	//camera and spriteBatch
	private OrthographicCamera camera;
	private SpriteBatch batch;

	//rectangle (used to keep track of the position of the sprites in the screen)
	private Rectangle bucket;
	//Array of rectangles for raindrops
	private Array<Rectangle> raindrops;


	//vector where where user is touching screen
	private Vector3 touchPos;

	//variable to keep track of the time we last spawned a raindrop
	private long lastDropTime;

	@Override
	public void create () {
		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		//create camera -- ensure that we can use target resolution (800x480) no matter actual screen size
		// it creates a world that is 800 x 400 units wide. it is the camera that controls the coordinate system that positions stuff on the screen
		//the origin (0, 0) of this coordinate system is in the lower left corner by default. It is possible to change
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		//create sprite batch
		batch = new SpriteBatch();

		//rectangle bucket, helps store the location of our bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		//initiate raindrop array and create first raindrop
		raindrops = new Array<Rectangle>();
		spawnRaindrop();

		//create vector that will store info on where user touching screen
		touchPos = new Vector3();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1); // set the clear color to blue.
		//arguments are the red, green, blue and alpha component of that color, each within the range [0, 1].
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // call instructs OpenGL to actually clear the screen.
		camera.update(); // is generally a good practice to update the camera once per frame

		// render bucket
		batch.setProjectionMatrix(camera.combined); //tells spriteBatch to use coordinate system set by camera

		batch.begin(); //tells SriteBatch to start new Batch

		batch.draw(bucketImage, bucket.x, bucket.y); //renders bucket in the screen

		for(Rectangle raindrop: raindrops){
			batch.draw(dropImage, raindrop.x, raindrop.y); //renders drops in the screen
		}

		batch.end(); //all draw commands between batch.begin() and batch.end will be made at once. It speeds up rendering

		//user interaction with bucket
		//if statement asks if screen is touched or mouse pressed
		if(Gdx.input.isTouched()){

			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos); // this line and the ones above it transform the touch/mouse coordinates into our camera coordinates
			bucket.x = touchPos.x - 64/2; //change position fo the bucket to where its been touched.
		}
		//two lines below ensures the bucket stays within screen limit
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		//spawn a new raindrop when it is time
		if(timeToSpawnRaindrop()){
			spawnRaindrop();
		}


		moveRaindrops();

	}

	/**
	 * Method that creates a new Rectangle, sets it to a position at the top of the
	 * screen and adds it to the raindrops array
	 */
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	/**
	 *
	 * @return True if it is time to spawn a new raindrop. Returns false otherwise
	 */

	private boolean timeToSpawnRaindrop(){
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) return true;
		return false;
	}

	/**
	 * Moves the raindrops in the screen.
	 * They move at a constant speed of 200 pixels/units per second.
	 * If the raindrop is beneath the bottom edge of the screen, we remove it from the array.
	 */
	private void moveRaindrops(){
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()){
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			raindropBucketOverlap(raindrop, iter);
			if(raindrop.y + 64 < 0) iter.remove();

		}
	}

	/**
	 * if a raindrop hits the bucket, we want to playback our drop sound and
	 * remove the raindrop from the array
	 * @param raindrop the raindrop we are checking if overlaped with the bucket
	 * @param iter the iterator that allows us to remove drop from the raindrops Array
	 */
	private void raindropBucketOverlap(Rectangle raindrop, Iterator<Rectangle> iter) {
		if(raindrop.overlaps(bucket)){
			dropSound.play();
			iter.remove();
		}
	}

	/**
	 * " it is in general a good idea to help out the operating system a little and clean up the mess we created."
	 * I guess this just makes sure that stuff is erased from memory once the user is done playing the game.
	 */
	@Override
	public void dispose(){
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

}
