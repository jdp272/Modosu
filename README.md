# Modosu by Ludic8
This is the game designed and built entirely Aidan Hobler, Connor Pugh, Grant Lee, Helen Wang, Jake Sanders, Jesse Potts, May Chen, and Stacy Wei as a semestor long project for CS 3152: Intro to Game Development. It is built in Java using LibGDX. See more details here: http://en-ci-gdiac.coecis.cornell.edu/gallery/modosu/

Check out the latest release to play!

## Notes

Architecture:

PHYSICS:
* CollisionController - implements ContactListener
* Factory
* GameCanvas
* GameOver - extends WorldController implements Screen
* GamePlayController - extends WorldController
* GDXRoot - extends Game implements ScreenListener
* HUD
* InputController
* Level
* LevelDesignerMode - extends WorldController
* LevelSelectMode - extends WorldController implements Screen, InputProcessor
* Loader
* LoadingMode - implements Screen, InputProcessor

* SpawnerList

* WorldController - implements Screen

    HOST:
    * AIController
    * ArrowModel
    * HostController
    * HostModel - extends BoxObstacle

    OBSTACLE:
    * BoxObstacle - extends SimpleObstacle
    * Obstacle
    * ObstacleSelector - implements QueryCallback
    * SandTile - extends BoxObstacle
    * SimpleObstacle - extends Obstacle
    * Wall - extends BoxObstacle
    * WaterTile - extends BoxObstacle
    
    SPIRIT:
    * SpiritModel - extends BoxObstacle

UTIL:
* FilmStrip - extends TextureRegion
* PooledList
* ScreenListener
* SoundController

