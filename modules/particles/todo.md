# TODO
- Switch from an array of arrays to a FloatBuffer/DoubleBuffer?
- Fix AccelerationUpdateModule not loading acceleration binding
- Make the BasicPhysicsModule zero out velocities under a threshold, so particles don't keep bouncing infinitesimally.
  - Maybe only do the check after a collision, so they can still drift, but will stop when hitting something?
- Builders. All the builders.