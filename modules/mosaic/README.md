## Sprites

- Sprites
    - Animated sprites with manual frame indexing
    - [9-slice texture scaling](https://en.wikipedia.org/wiki/9-slice_scaling)
    - Customizable stretching/tiling behavior using pinned edges
- Sprite sheet definition using .mcmeta
    - Hot-reloadable without reloading all the game assets (useful for quick iteration)
    - Direct access to the pixel data as a BufferedImage
    - 
- `BufferedImage`/`Graphics2D` sprite implementation (automatically uploads the texture) (reimplementation without using awt?)

# Texture .mcmeta format
```json
{
    // spritesheet definition
    "spritesheet": {
        // required:
        // texture size in pixels (used for UV calculations)
        "size": [<w>, <h>],

        "sprites": {
            // static sprite shorthand
            "<sprite name>": [<u>, <v>, <w>, <h>],

            "<sprite name>": {
                // required:
                "pos": [<u>, <v>, <w>, <h>],

                // the number of frames, shorthand for "frames": [0, 1, 2, ..., n-1]
                "frames": 12,

                // animation frame indices
                "frames": [0, 1, 2, 3, 2, 1],

                // the number of ticks per frame, defaults to 1
                "frameTime": 2,

                // uv offset per frame, defaults to [0, <h>]
                "offset": [<u>, <v>],

                // the number of pixels on each edge that should remain non-distorted when stretching the sprite
                // See: https://en.wikipedia.org/wiki/9-slice_scaling
                "caps": [<minU>, <minV>, <maxU>, <maxV>],

                // which edges should be "pinned" when drawing the sprite larger than normal. Edges that have not
                // been pinned will end up being repeated or truncated. If both pins on an axis are false, the sprite
                // will default to pinning on both sides along that axis. Defaults to [true, true, true, true]
                "pinEdges": [<left>, <top>, <right>, <bottom>]

                // shorthand for "pinEdges": [<horizontal>, <vertical>, false, false]
                "pinEdges": [<horizontal>, <vertical>]
            }
        },
        // optional:
        "colors": {
            // create a named color based on the color of the pixel at (<u>, <v>)
            "<color name>": [<u>, <v>]
        }
    }
}
```

## Skeleton starter json
Easy to copy-paste and modify to suit your needs
```json
{
    "spritesheet": {
        "size": [w, h],
        "sprites": {
            "__": [u, v, w, h],

            "__": {
                "pos": [u, v, w, h],
                "frames": 12,

            },

            "__": {
                "pos": [u, v, w, h],

                "caps": [minU, minV, maxU, maxV],

                "pinEdges": [left, top, right, bottom],

                "pinEdges": [horizontal, vertical]
            }
        },
        "colors": {
            "__": [u, v]
        }
    }
}
```

