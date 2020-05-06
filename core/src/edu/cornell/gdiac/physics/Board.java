package edu.cornell.gdiac.physics;

import edu.cornell.gdiac.physics.obstacle.BorderCorner;
import edu.cornell.gdiac.physics.obstacle.BorderEdge;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class Board {

    /** The 2D array that is the board */
    private Obstacle[][] board;

    // These define the region of the board that can be used
    /** The index after the largest y index usable by the board */
    private int topBorder;
    /** The smallest y index usable by the board */
    private int bottomBorder;
    /** The smallest x index usable by the board */
    private int leftBorder;
    /** The index after the largest y index usable by the board */
    private int rightBorder;

    // These are offsets for drawing all objects, based on the position of the lower left corner
    /** The initial bottom border for the y offset */
    private int initialBottomBorder;
    /** The initial left border for the x offset */
    private int initialLeftBorder;

    /**
     * Creates a default board that can hold
     *
     * @param maxWidthCapacity The max width that the board can hold
     * @param maxHeightCapacity The max height that the board can hold
     */
    public Board(int maxWidthCapacity, int maxHeightCapacity) {
        board = new Obstacle[maxWidthCapacity][maxHeightCapacity];
    }

    /**
     * Gets the x index of tile index that an x coordinate is in
     *
     * @param coord The box2D x coordinate
     *
     * @return The x value of the tile index
     */
    public int xCoordToTile(float coord) {
        return Math.round((coord - (Constants.TILE_WIDTH / 2.f)) / Constants.TILE_WIDTH) + initialLeftBorder;
    }

    /**
     * Gets the y index of tile index that an y coordinate is in
     *
     * @param coord The box2D y coordinate
     *
     * @return The y value of the tile index
     */
    public int yCoordToTile(float coord) {
        return Math.round((coord - (Constants.TILE_HEIGHT / 2.f)) / Constants.TILE_HEIGHT) + initialBottomBorder;
    }

    /**
     * Gets the x coordinate of the center of a tile
     *
     * @param index The x value of the tile index
     *
     * @return The box2D x coordinate of the tile center
     */
    public float xTileToCoord(int index) {
        return xTileToCoord(index, false);
    }

    /**
     * Gets the y coordinate of the center of a tile
     *
     * @param index The y value of the tile index
     *
     * @return The box2D y coordinate of the tile center
     */
    public float yTileToCoord(int index) {
        return yTileToCoord(index, false);
    }

    /**
     * Gets the x coordinate of a tile
     *
     * @param index The x value of the tile index
     * @param corner If true, gets the lower left corner coordinate. Otherwise,
     *               gets the center coordinate
     *
     * @return The box2D x coordinate of the tile
     */
    public float xTileToCoord(int index, boolean corner) {
        return ((index - initialLeftBorder) + 0.5f) * Constants.TILE_WIDTH - (corner ? Constants.TILE_WIDTH / 2.f : 0);
    }

    /**
     * Gets the y coordinate of a tile
     *
     * @param index The y value of the tile index
     * @param corner If true, gets the lower left corner coordinate. Otherwise,
     *               gets the center coordinate
     *
     * @return The box2D y coordinate of the tile corner
     */
    public float yTileToCoord(int index, boolean corner) {
        return ((index - initialBottomBorder) + 0.5f) * Constants.TILE_HEIGHT - (corner ? Constants.TILE_HEIGHT / 2.f : 0);
    }

    /**
     * The initial bottom border represents the bottom border the last
     * time that it was reset. It is not changed when the bottom border is
     * changed, to ensure that the offset for obstacles in the world stays
     * constant as the corner changes
     *
     * It can be used as a constant point with which to reference coordinates,
     * so that positions of obstacles don't have to change every time the actual
     * lower left corner is changed
     *
     * @return The initial bottom border
     */
    public int getInitialBottomBorder() { return initialBottomBorder; }

    /**
     * The initial left border represents the left border the last time that
     * it was reset. It is not changed when the left border is changed, to
     * ensure that the offset for obstacles in the world stays constant as the
     * corner changes
     *
     * It can be used as a constant point with which to reference coordinates,
     * so that positions of obstacles don't have to change every time the actual
     * lower left corner is changed
     *
     * @return The initial left border
     */
    public int getInitialLeftBorder() { return initialLeftBorder; }

    /**
     * This is the difference between the actual bottom border and the initial
     * one. It is important for rendering so that the drawing code knows the
     * offset by which to move all the obstacles drawn.
     *
     * Note that obstacles were placed relative to the initial border, but they
     * must be drawn relative to the current border
     *
     * @return The offset of the bottom border, for drawing
     */
    public int getBottomOffset() { return bottomBorder - initialBottomBorder; }

    /**
     * This is the difference between the actual left border and the initial
     * one. It is important for rendering so that the drawing code knows the
     * offset by which to move all the obstacles drawn.
     *
     * Note that obstacles were placed relative to the initial border, but they
     * must be drawn relative to the current border
     *
     * @return The offset of the left border, for drawing
     */
    public int getLeftOffset() { return leftBorder - initialLeftBorder; }

    /**
     * @return The top border of the board
     */
    public int getTopBorder() { return topBorder; }

    /**
     * @return The bottom border of the board
     */
    public int getBottomBorder() { return bottomBorder; }

    /**
     * @return The left border of the board
     */
    public int getLeftBorder() { return leftBorder; }

    /**
     * @return The right border of the board
     */
    public int getRightBorder() { return rightBorder; }

    /**
     * @return The width of the board, in tile coordinates
     */
    public int getWidth() { return rightBorder - leftBorder; }

    /**
     * @return The height of the board, in tile coordinates
     */
    public int getHeight() { return topBorder - bottomBorder; }

    /**
     * Adds a new object to the game. It will only add an object if that object
     * corresponds to a tile which is empty, in the board, and not on the border
     * location, and only within the board
     *
     * @param obj
     *
     * @return If the object was added
     */
    public boolean addNewObstacle(Obstacle obj) {
        int x = xCoordToTile(obj.getX());
        int y = yCoordToTile(obj.getY());

        if(x >= leftBorder && y >= bottomBorder && x < rightBorder && y < topBorder && board[x][y] == null) {
            obj.setPosition(xTileToCoord(x), yTileToCoord(y));
            board[x][y] = obj;
            return true;

            // TODO: uncomment this when hasPedestal is added
//			if(obj.getName() == "pedestal") {
//				hasPedestal = true;
//			}
        }
        return false;
    }

    /**
     * Returns the object at the specified position. Null is returned if the
     * space is empty or if the space is outside the bounds of the region
     *
     * @param xCoord The x Box2D coordinate to query
     * @param yCoord The y Box2D coordinate to query
     */
    public Obstacle get(float xCoord, float yCoord) {
        int x = xCoordToTile(xCoord);
        int y = yCoordToTile(yCoord);

        return get(x, y);
    }

    /**
     * Returns the object at the specified position. Null is returned if the
     * space is empty or if the space is outside the bounds of the region
     *
     * @param x The x index of the board to query
     * @param y The y index of the board to query
     */
    public Obstacle get(int x, int y) {
        if(x < leftBorder || x >= rightBorder || y < bottomBorder || y >= topBorder) {
            return null;
        } else {
            return board[x][y];
        }
    }

    /**
     * Sets the corresponding location in the board to the given object. If the
     * location is out of bounds, nothing happens. If another object is there,
     * it is automatically removed from the world
     *
     * @param obj The obstacle to add
     * @param xCoord The x Box2D coordinate to set
     * @param yCoord The y Box2D coordinate to set
     *
     * @return True if the obstacle was added
     */
    public boolean set(Obstacle obj, float xCoord, float yCoord) {
        int x = xCoordToTile(xCoord);
        int y = yCoordToTile(yCoord);

        return set(obj, x, y);
    }

    /**
     * Sets the given location in the board to the given object. If the
     * location is out of bounds, nothing happens. If another object is there,
     * it is automatically removed from the world
     *
     * @param obj The obstacle to add
     * @param x The x index of the board to set
     * @param y The y index of the board to set
     *
     * @return True if the obstacle was added
     */
    public boolean set(Obstacle obj, int x, int y) {
        if(x < leftBorder || x >= rightBorder || y < bottomBorder || y >= topBorder) {
            return false;
        } else {
            if(board[x][y] != null) {
                board[x][y].markRemoved(true);
            }
            board[x][y] = obj;
            return true;
        }
    }

    /**
     * Removes the tile at the given element from the board without removing it
     * from the world
     *
     * @param xCoord The x Box2D coordinate to query
     * @param yCoord The y Box2D coordinate to query
     *
     * @return If an object was removed
     */
    public boolean removeFromBoard(float xCoord, float yCoord) {
        int x = xCoordToTile(xCoord);
        int y = yCoordToTile(yCoord);

        return removeFromBoard(x, y);
    }

    /**
     * Returns the object at the specified position. Null is returned if the
     * space is empty or if the space is outside the bounds of the region
     *
     * @param x The x index of the board to query
     * @param y The y index of the board to query
     *
     * @return If an object was removed
     */
    public boolean removeFromBoard(int x, int y) {
        if(x >= leftBorder && x < rightBorder && y >= bottomBorder && y < topBorder && board[x][y] != null) {
            board[x][y] = null;
            return true;
        }
        return false;
    }

    /**
     * Processes the object deselected
     */
    public void processBorderChange(LevelDesignerMode.Corner corner, float xCoord, float yCoord) {
        // Offset the location so the corner sits at a corner of the tile, not
        // the center
        int x = xCoordToTile(xCoord + (Constants.TILE_WIDTH / 2.f));
        int y = yCoordToTile(yCoord + (Constants.TILE_HEIGHT / 2.f));

        // Ensure there is at least 1 row and 1 column of the array that can be
        // used
        int top    = Math.min(Math.max(y, 1), board[0].length);
        int bottom = Math.min(Math.max(y, 0), board[0].length - 1);
        int left   = Math.min(Math.max(x, 0), board.length - 1);
        int right  = Math.min(Math.max(x, 1), board.length);

        // Ensure the board always has the minimum size
        top    = Math.max(top, bottomBorder + Constants.MINIMUM_BOARD_WIDTH);
        bottom = Math.min(bottom, topBorder - Constants.MINIMUM_BOARD_WIDTH);
        left   = Math.min(left, rightBorder - Constants.MINIMUM_BOARD_HEIGHT);
        right  = Math.max(right, leftBorder + Constants.MINIMUM_BOARD_HEIGHT);

        // In each case, reset the variables that shouldn't change
        switch(corner) {
            case TOP_LEFT:
                bottom = bottomBorder;
                right = rightBorder;

                break;
            case TOP_RIGHT:
                bottom = bottomBorder;
                left = leftBorder;

                break;
            case BOTTOM_LEFT:
                top = topBorder;
                right = rightBorder;

                break;
            case BOTTOM_RIGHT:
                top = topBorder;
                left = leftBorder;

                break;
            default:
                System.out.println("Invalid corner being processed");
                assert false;
                break;
        }

        // Remove everything that has become out of bounds
        for(int i = leftBorder; i < rightBorder; i++) {
            for(int j = bottomBorder; j < topBorder; j++) {
                if(i < left || i >= right || j < bottom || j >= top) {
                    set(null, i, j);
                }
            }
        }

        // Assign the new borders
        topBorder = top;
        bottomBorder = bottom;
        leftBorder = left;
        rightBorder = right;

        System.out.println("x: [" + leftBorder + ", " + rightBorder + "], y: [" + bottomBorder + ", " + topBorder + "]");
    }

    /**
     * Reset the board to the given size, and center it in the array
     *
     * @param width The new width of the board, in tiles
     * @param height The new height of the board, in tiles
     */
    public void reset(int width, int height) {
        // Ensure the board is at least the minimum size, and no larger than the
        // array containing it
        width = Math.max(Math.min(width, board.length), Constants.MINIMUM_BOARD_WIDTH);
        height = Math.max(Math.min(height, board[0].length), Constants.MINIMUM_BOARD_HEIGHT);

        // Reset the level size based on the size of the screen
        leftBorder = (board.length / 2) - (width / 2);
        bottomBorder = (board[0].length / 2) - (height / 2);

        initialLeftBorder = leftBorder;
        initialBottomBorder = bottomBorder;

        rightBorder = leftBorder + width;
        topBorder = bottomBorder + height;


        System.out.println("x: [" + leftBorder + ", " + rightBorder + "], y: [" + bottomBorder + ", " + topBorder + "], intial: (" + initialLeftBorder + ", " + initialBottomBorder + ")");
    }

    /**
     * Sets the borders around the edge of the board with the correct
     * orientations. Also updates each terrain tile (water, sand, and wall)
     */
    public void setBordersAndUpdateTerrain(Factory factory) {
        // Update terrain tiles based on the new borders and add walls
        for(int i = leftBorder; i < rightBorder; i++) {
            for(int j = bottomBorder; j < topBorder; j++) {
                float xCoord = xTileToCoord(i);
                float yCoord = yTileToCoord(j);

                // Add corners and borders
                if(i == leftBorder && j == bottomBorder) {
                    set(null, i, j);
                    System.out.println("*****");
                    if(addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.BOTTOM_LEFT))){
                        System.out.println("Border added");
                    } else {
                        System.out.println("No border added!");
                    }
                    System.out.println("*****");
                } else if(i == rightBorder - 1 && j == bottomBorder) {
                    set(null, i, j);
                    addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.BOTTOM_RIGHT));
                } else if(i == leftBorder && j == topBorder - 1) {
                    set(null, i, j);
                    addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.TOP_LEFT));
                } else if(i == rightBorder - 1 && j == topBorder - 1) {
                    set(null, i, j);
                    addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.TOP_RIGHT));
                } else if(j == bottomBorder) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.BOTTOM);
//					} else {
                    set(null, i, j);
                    BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.BOTTOM);

                    // Indicate that the edge is next to the left or right side
                    if(i - leftBorder == 1) {
                        border.setNextToSide(1, BorderEdge.Side.LEFT);
                    } else if((rightBorder - 1) - i == 1) {
                        border.setNextToSide(1, BorderEdge.Side.RIGHT);
                    }
                    addNewObstacle(border);
//					}
                } else if(i == leftBorder) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.LEFT);
//					} else {
                    set(null, i, j);
                    BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.LEFT);

                    // Indicate that the edge is next to the top or bottom side
                    if(j - bottomBorder == 1) {
                        border.setNextToSide(1, BorderEdge.Side.BOTTOM);
                    } else if((topBorder - 1) - j == 1) {
                        border.setNextToSide(1, BorderEdge.Side.TOP);
                    }
                    addNewObstacle(border);
//					}
                } else if(i == rightBorder - 1) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.RIGHT);
//					} else {
                    set(null, i, j);
                    BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.RIGHT);

                    // Indicate that the edge is next to the top or bottom side
                    if(i - bottomBorder == 1) {
                        border.setNextToSide(1, BorderEdge.Side.BOTTOM);
                    } else if((topBorder - 1) - i == 1) {
                        border.setNextToSide(1, BorderEdge.Side.TOP);
                    }
                    addNewObstacle(border);
//					}
                } else if(j == topBorder - 1) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.TOP);
//					} else {
                    set(null, i, j);
                    BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.TOP);

                    // Indicate that the edge is next to the left or right side
                    if(i - leftBorder <= 2) {
                        border.setNextToSide(i - leftBorder, BorderEdge.Side.LEFT);
                    } else if((rightBorder - 1) - i <= 2) {
                        border.setNextToSide((rightBorder - 1) - i, BorderEdge.Side.RIGHT);
                    }
                    addNewObstacle(border);
//					}
                } else {
                    if(get(i, j) instanceof BorderEdge || get(i, j) instanceof BorderCorner) {
                        set(null, i, j);
                    }
//                    updateTerrainAroundRegion(i, j);
//                    updateWallAroundRegion(i, j);
//						edges.add((BorderEdge)board[i][j]);
//						board[i][j] = null;
//					} else if(board[i][j] instanceof BorderCorner) {
//						corners.add((BorderCorner)board[i][j]);
//						board[i][j] = null;
//					}
//					updateWaterAroundRegion(i, j);
//					updateSandAroundRegion(i, j);
//					updateWallAroundRegion(i, j);
//					if(board[i][j] instanceof BorderEdge) {
//						edges.add((BorderEdge)board[i][j]);
//						board[i][j] = null;
//					} else if(board[i][j] instanceof BorderCorner) {
//						corners.add((BorderCorner)board[i][j]);
//						board[i][j] = null;
//					}
                }
            }
        }

        // Remove all the border pieces that are still unused from the game
//        for(BorderEdge border : edges) {
//            border.markRemoved(true);
//        }
//        for(BorderCorner border : corners) {
//            border.markRemoved(true);
//        }
    }

    /**
     * A function that clears the board, setting all slots to null. This
     * function can also optionally remove objects from the world as well
     *
     * @param removeFromWorld Whether the function should remove objects from
     *                        the world as it clears the board
     */
    public void clear(boolean removeFromWorld) {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] != null) {
                    if(removeFromWorld) {
                        board[i][j].markRemoved(true);
                    }
                    board[i][j] = null;
                }
            }
        }
    }
}
