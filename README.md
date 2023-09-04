# `shisensho-base`

Implementation of [Shisen-Sho](https://en.wikipedia.org/wiki/Shisen-Sho) game, used by my
[Shisen-Sho Android app](https://play.google.com/store/apps/details?id=com.italankin.shisensho).

## Entry points

* [`ShisenShoGame`](src/main/kotlin/com/italankin/shisensho/game/ShisenShoGame.kt) - interface for the game
* [`ShisenShoGameImpl`](src/main/kotlin/com/italankin/shisensho/game/impl/ShisenShoGameImpl.kt) - basic implementation
  of `ShisenShoGame` interface
* [`MultiLayerShisenShoGame`](src/main/kotlin/com/italankin/shisensho/game/MultiLayerShisenShoGame.kt) - support for
  multiple game layers
* [`CreateSolvable.kt`](src/main/kotlin/com/italankin/shisensho/game/util/CreateSolvable.kt) - derives solvable games
  from the given states
* [`IsSolvable.kt`](src/main/kotlin/com/italankin/shisensho/game/util/IsSolvable.kt) - check whether game is
  solvable or not
* [`SimplifyPath.kt`](src/main/kotlin/com/italankin/shisensho/game/util/SimplifyPath.kt) - simplify resulting paths
  removing intermediate points

### `ShisenShoGameImpl`

`ShisenShoGameImpl` uses two algorithms for searching the path:

* [`BfsPathFinder`](src/main/kotlin/com/italankin/shisensho/game/impl/BfsPathFinder.kt) - BFS
* [`SimplePathFinder`](src/main/kotlin/com/italankin/shisensho/game/impl/SimplePathFinder.kt) - simple iterative
  expansion (the one used in [KShisen](https://apps.kde.org/kshisen/))

Gravity and Chinese style are also supported.

## Requirements

* Kotlin 1.9
* JDK 17

## Tests

Run tests with command:

```shell
./gradlew test
```
