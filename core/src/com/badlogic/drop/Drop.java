package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.awt.geom.Rectangle2D;

public class Drop extends ApplicationAdapter {
	//files we are referencing
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	//camera and spriteBatch
	private OrthographicCamera camera;
	private SpriteBatch batch;

	//rectangle
	private Rectangle bucket;

	//vector where where user is touching screen
	private Vector3 touchPos;

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
		// it creates a world that is 800 x 400 units wide.
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
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end(); //all draw commands between batch.begin() and batch.end will be made at once. It speeds up rendering

		//user interaction with bucket
		//if statement asks if screen is touched or mouse pressed
		if(Gdx.input.isTouched()){

			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos); // this line and the ones above it transform the touch/mouse coordinates into our camera coordinates
			bucket.x = touchPos.x - 64/2; //change position fo the bucket to where its been touched.
		}
	}
}
