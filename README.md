Modosu by Ludic8

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

