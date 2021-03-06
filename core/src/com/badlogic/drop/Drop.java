package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Elo� on 26/02/2016.
 */
public class Drop extends Game {
    // this is the main class that is called when the game is launched.

    //The SpriteBatch object is used to render objects onto the screen, such as textures;
    // and the BitmapFont object is used, along with a SpriteBatch, to render text onto the screen.
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();

        //Use LibGDX's default font
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));

    }
    public void render(){
        super.render(); //important, without the super the game does not work!
    }

    public void dispose(){
        batch.dispose();
        font.dispose();
    }

}
