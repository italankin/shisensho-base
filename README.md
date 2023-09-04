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

## License

```
MIT License

Copyright (c) 2023 Igor Talankin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
