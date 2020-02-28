Modosu by Ludic8


TODO:
* split up RobotController: move world building to WorldController and keep only actual robot related items in there
* look how ragdoll implemented mouse controls (InputController?) would probably be called from "update" in RobotController
* implement firing for robot v
* look how lab3 shot bullets without having the ship create the object (was done in GameplayController)
* build world level

Architecture:

PHYSICS:
* GameCanvas
* GDXRoot
* InputController
* LoadingMode
* WorldController - implements Screen

ROBOT:
* RobotController - extends WorldController, implements Contact Listener
* RobotModel - extends BoxObstacle

OBSTACLE:
* BoxObstacle - extends SimpleObstacle
* CapsuleObstacle - extends SimpleObstacle
* ComplexObstacle - extends Obstacle
* Obstacle
* obstacleSelector
* PolygonObstacle - extends SimpleObstacle
* SimpleObstacle - extends Obstacle
* Wheelobstacle - extends SimpleObstacle


UTIL:
* FilmStrip - extends TextureRegion
* PooledList
* RandomController
* ScreenListener
* SoundController
* XBox360Controller
