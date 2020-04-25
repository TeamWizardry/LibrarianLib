package com.teamwizardry.librarianlib.glitter.modules

//class AccelerationUpdateModule(
//        val velocity: ParticleBinding,
//        val acceleration: ParticleBinding
//): ParticleUpdateModule {
//    override fun update(particle: DoubleArray) {
//        update(particle, 0)
//        update(particle, 1)
//        update(particle, 2)
//    }
//
//    private fun update(particle: DoubleArray, index: Int) {
//        val vel = velocity.get(particle, index)
//        velocity.set(particle, index, vel + acceleration.get(particle, index))
//    }
//}
/*
ofVec3f    ComputeCurl(float    x,    float    y,    float    z)
{
                float    eps    =    1.0;
                float    n1,    n2,    a,    b;
                ofVec3f    curl;
                n1    =    noise(x,    y    +    eps,    z);
                n2    =    noise(x,    y    -­‐    eps,    z);
                a    =    (n1    -­‐    n2)/(2    *    eps);
                n1    =    noise(x,    y,    z    +    eps);
                n2    =    noise(x,    y,    z    -­‐    eps);
                b    =    (n1    -­‐    n2)/(2    *    eps);
                curl.x    =    a    -­‐    b;
                n1    =    noise(x,    y,    z    +    eps);
                n2    =    noise(x,    y,    z    -­‐    eps);
                a    =    (n1    -­‐    n2)/(2    *    eps);
                n1    =    noise(x    +    eps,    y,    z);
                n2    =    noise(x    +    eps,    y,    z);
                b    =    (n1    -­‐    n2)/(2    *    eps);
                curl.y    =    a    -­‐    b;
                n1    =    noise(x    +    eps,    y,    z);
                n2    =    noise(x    -­‐    eps,    y,    z);
                a    =    (n1    -­‐    n2)/(2    *    eps);
                n1    =    noise(x,    y    +    eps,    z);
                n2    =    noise(x,    y    -­‐    eps,    z);
                b    =    (n1    -­‐    n2)/(2    *    eps);
                curl.z    =    a    -­‐    b;
                return    curl;
}
 */