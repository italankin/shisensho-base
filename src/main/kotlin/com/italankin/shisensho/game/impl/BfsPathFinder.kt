package com.italankin.shisensho.game.impl

import com.italankin.shisensho.game.Path
import com.italankin.shisensho.game.Point
import com.italankin.shisensho.game.ShisenShoGame
import com.italankin.shisensho.game.Tile
import com.italankin.shisensho.game.impl.BfsPathFinder.SearchNode.Direction
import com.italankin.shisensho.game.util.manhattan

/**
 * Implementation of [ShisenShoGameImpl.PathFinder] which uses BFS to find a path between tiles
 */
object BfsPathFinder : ShisenShoGameImpl.PathFinder {

    override fun findPath(game: ShisenShoGame, a: Tile, b: Tile): Path {
        val queue = ArrayDeque<SearchNode>(32)
        queue += game.expandInitial(a)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val tileAt = game[node.x, node.y]
            if (tileAt != null) {
                if (tileAt == b) {
                    // path found
                    val path = ArrayList<Point>()
                    var p: SearchNode? = node
                    while (p != null) {
                        path.add(Point(p.x, p.y))
                        p = p.parent
                    }
                    path.add(Point(a.x, a.y))
                    path.reverse()
                    return path
                }
                // tile itself cannot be expanded
                continue
            }
            queue += game.expand(node, b)
        }
        // no bath between a and b
        return emptyList()
    }

    private fun ShisenShoGame.expandInitial(tile: Tile): List<SearchNode> {
        val result = ArrayList<SearchNode>(4)
        for (direction in Direction.values()) {
            val newX = tile.x + direction.dx
            val newY = tile.y + direction.dy
            if (get(newX, newY) != null) {
                // target tile are not within distance of 1
                // so we don't need to expand surrounding tiles
                continue
            }
            result += SearchNode(newX, newY, 2, direction, null)
        }
        return result
    }

    private fun ShisenShoGame.expand(node: SearchNode, target: Tile): List<SearchNode> {
        val nodes = ArrayList<SearchNode>(4)
        for (direction in Direction.values()) {
            val turnsLeft =
                if (node.direction == direction) node.turnsLeft else (node.turnsLeft - 1)
            if (turnsLeft < 0) {
                // no segments left, exiting
                continue
            }
            val newX = node.x + direction.dx
            if (newX !in minX..maxX) {
                continue
            }
            val newY = node.y + direction.dy
            if (newY !in minY..maxY) {
                continue
            }
            if (turnsLeft < 2) {
                // for the last two segments the distance must decrease on each step in path
                val distance = manhattan(node.x, node.y, target.x, target.y)
                val newDistance = manhattan(newX, newY, target.x, target.y)
                if (distance < newDistance) {
                    // if newDistance increases, skip this node
                    continue
                }
            }
            nodes += SearchNode(newX, newY, turnsLeft, direction, node)
        }
        return nodes
    }

    private data class SearchNode(
        val x: Int,
        val y: Int,
        val turnsLeft: Int,
        val direction: Direction,
        val parent: SearchNode?
    ) {

        enum class Direction(val dx: Int, val dy: Int) {
            UP(0, -1),
            DOWN(0, 1),
            LEFT(-1, 0),
            RIGHT(1, 0)
        }
    }
}
