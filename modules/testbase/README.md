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
