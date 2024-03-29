package com.teamwizardry.librarianlib.core.test.tests

import com.teamwizardry.librarianlib.math.Easing
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EasingTests {
    private val steps = (0 .. 20).map { it / 20f }

    private fun testEasing(easing: Easing, vararg points: Float) {
        val actual = steps.map { easing.ease(it) }
        assertEquals(points.toList(), actual)
    }

    @Test
    fun linear() {
        testEasing(Easing.linear,
            *steps.toFloatArray()
        )
    }

    @Test
    fun easeInBack() {
        testEasing(Easing.easeInBack,
            -0.0f, -0.022387894f, -0.042677063f, -0.060507562f, -0.075442195f, -0.08694642f, -0.09436257f, -0.09687699f,
            -0.093479164f, -0.082912765f, -0.06362185f, -0.033703174f, 0.009110219f, 0.067381814f, 0.14374009f,
            0.24039286f, 0.35839865f, 0.49694163f, 0.65310055f, 0.8224179f, 1.0f)
    }

    @Test
    fun easeInBounce() {
        testEasing(Easing.easeInBounce,
            0.0f, 0.015468776f, 0.011875033f, 0.054843724f, 0.060000002f, 0.02734375f, 0.06937504f, 0.1673438f,
            0.22750002f, 0.24984378f, 0.234375f, 0.1810937f, 0.089999914f, 0.07359362f, 0.31937492f, 0.52734375f,
            0.6975f, 0.8298438f, 0.92437494f, 0.98109376f, 1.0f)
    }

    @Test
    fun easeInCirc() {
        testEasing(Easing.easeInCirc,
            0.0f, 0.00397344f, 0.009294888f, 0.016076822f, 0.024448503f, 0.034559723f, 0.04658566f, 0.060733344f,
            0.07725047f, 0.0964374f, 0.118664555f, 0.14439741f, 0.17423481f, 0.20896947f, 0.2496894f, 0.29795918f,
            0.35617682f, 0.42837104f, 0.52237016f, 0.65824336f, 1.0f)
    }

    @Test
    fun easeInCubic() {
        testEasing(Easing.easeInCubic,
            0.0f, 0.0053699072f, 0.011625524f, 0.019027997f, 0.0279076f, 0.038683336f, 0.051887404f, 0.06819457f,
            0.08845453f, 0.113721594f, 0.14526837f, 0.1845596f, 0.23315114f, 0.29248208f, 0.3635679f, 0.44668603f,
            0.5412115f, 0.64572006f, 0.7583161f, 0.87700987f, 1.0f)
    }

    @Test
    fun easeInElastic() {
        testEasing(Easing.easeInElastic,
            0.0f, 6.9061044E-4f, 0.001953125f, 0.0013809914f, -0.0019533413f, -0.0055242716f, -0.0039060337f,
            0.0055248835f, 0.015625f, 0.011047931f, -0.01562673f, -0.044194173f, -0.031248273f, 0.04419906f, 0.125f,
            0.08838345f, -0.12501386f, -0.35355344f, -0.24998611f, 0.35359251f, 1.0f)
    }

    @Test
    fun easeInExpo() {
        testEasing(Easing.easeInExpo,
            0.0f, 0.0026305562f, 0.00528317f, 0.008001822f, 0.010843743f, 0.013884144f, 0.017223103f, 0.020995826f,
            0.025388325f, 0.030662248f, 0.037195675f, 0.045553695f, 0.05661986f, 0.07185725f, 0.09385312f, 0.12761313f,
            0.18408403f, 0.28943816f, 0.48746863f, 0.7502651f, 1.0f)
    }

    @Test
    fun easeInOutBack() {
        testEasing(Easing.easeInOutBack,
            -0.0f, -0.037046105f, -0.06629149f, -0.08578121f, -0.0927017f, -0.08280713f, -0.049316723f, 0.019272083f,
            0.14292297f, 0.34742516f, 0.6066799f, 0.8157477f, 0.94754255f, 1.0257487f, 1.0692606f, 1.0891657f,
            1.0920905f, 1.0822263f, 1.0623733f, 1.0344903f, 1.0f)
    }

    @Test
    fun easeInOutBounce() {
        testEasing(Easing.easeInOutBounce,
            0.0f, 0.0059375167f, 0.030000001f, 0.03468752f, 0.11375001f, 0.1171875f, 0.044999957f, 0.15968746f,
            0.34875f, 0.46218747f, 0.5f, 0.53781253f, 0.65125006f, 0.84031236f, 0.95500004f, 0.8828125f, 0.88625f,
            0.96531254f, 0.97f, 0.9940625f, 1.0f)
    }

    @Test
    fun easeInOutCirc() {
        testEasing(Easing.easeInOutCirc,
            0.0f, 0.00980314f, 0.02237458f, 0.038415886f, 0.058955126f, 0.085594095f, 0.12104814f, 0.17052115f,
            0.24607672f, 0.38392496f, 0.60240865f, 0.73411316f, 0.8091091f, 0.8595599f, 0.8964956f, 0.9248055f,
            0.9470861f, 0.9648868f, 0.9792119f, 0.99075097f, 1.0f)
    }

    @Test
    fun easeInOutCubic() {
        testEasing(Easing.easeInOutCubic,
            0.0f, 0.005561618f, 0.015821688f, 0.03181479f, 0.054955125f, 0.08723013f, 0.13150467f, 0.19194551f,
            0.27427518f, 0.38396296f, 0.516875f, 0.6486634f, 0.7555543f, 0.8343043f, 0.89089435f, 0.9312676f,
            0.95968044f, 0.97903234f, 0.9913228f, 0.997968f, 1.0f)
    }

    @Test
    fun easeInOutElastic() {
        testEasing(Easing.easeInOutElastic,
            0.0f, 9.765625E-4f, 3.3907464E-4f, -0.0036707881f, -0.0039060337f, 0.011970193f, 0.023938464f, -0.03125346f,
            -0.11746114f, 0.04342515f, 0.5f, 0.17360623f, -1.8793787f, -2.0002208f, 6.128245f, 12.257478f, -15.999114f,
            -60.142216f, 22.221588f, 256.0f, 1.0f)
    }

    @Test
    fun easeInOutExpo() {
        testEasing(Easing.easeInOutExpo,
            0.0f, 8.829616E-4f, 0.0037616747f, 0.009071999f, 0.01742549f, 0.029723404f, 0.047319278f, 0.07048774f,
            0.07570442f, 5.696472E-4f, 0.5f, 0.9994301f, 0.9242958f, 0.92951226f, 0.9526807f, 0.97027665f, 0.9825745f,
            0.99092793f, 0.99623835f, 0.9991171f, 1.0f)
    }

    @Test
    fun easeInOutQuad() {
        testEasing(Easing.easeInOutQuad,
            0.0f, 0.0071541504f, 0.022653136f, 0.047492392f, 0.08267796f, 0.12909423f, 0.18726616f, 0.25699592f,
            0.33692795f, 0.4242411f, 0.51478446f, 0.6038155f, 0.68704855f, 0.7614344f, 0.8253523f, 0.87835044f,
            0.9207356f, 0.9532172f, 0.9766668f, 0.9919789f, 0.99999994f)
    }

    @Test
    fun easeInOutQuart() {
        testEasing(Easing.easeInOutQuart,
            0.0f, 0.0015000838f, 0.006445202f, 0.015701272f, 0.030531235f, 0.05288871f, 0.086046286f, 0.13614061f,
            0.21674101f, 0.36372024f, 0.59597033f, 0.7518178f, 0.8384886f, 0.8932573f, 0.93038315f, 0.9562819f,
            0.97436213f, 0.98665386f, 0.994468f, 0.99870205f, 1.0f)
    }

    @Test
    fun easeInOutQuint() {
        testEasing(Easing.easeInOutQuint,
            0.0f, 0.0012014685f, 0.005157346f, 0.012551909f, 0.024385218f, 0.042213615f, 0.0686819f, 0.10892568f,
            0.17556204f, 0.3207396f, 0.6766075f, 0.81020486f, 0.8763359f, 0.9177308f, 0.9460844f, 0.9660135f,
            0.9800055f, 0.9895631f, 0.9956636f, 0.99898046f, 1.0f)
    }

    @Test
    fun easeInOutSine() {
        testEasing(Easing.easeInOutSine,
            0.0f, 0.009477989f, 0.027174205f, 0.053835273f, 0.09014919f, 0.13663286f, 0.19346006f, 0.26023236f,
            0.3357334f, 0.41776752f, 0.50322384f, 0.58844894f, 0.66984147f, 0.7444239f, 0.81016123f, 0.86597675f,
            0.9115687f, 0.9471649f, 0.97330266f, 0.99066967f, 1.0f)
    }

    @Test
    fun easeInQuad() {
        testEasing(Easing.easeInQuad,
            0.0f, 0.0089416085f, 0.0205003f, 0.034991354f, 0.052778672f, 0.07428086f, 0.099975586f, 0.13039975f,
            0.16614068f, 0.20781054f, 0.25599316f, 0.31114954f, 0.37347472f, 0.44272083f, 0.51804286f, 0.59796256f,
            0.6805302f, 0.76365656f, 0.845471f, 0.9245554f, 1.0f)
    }

    @Test
    fun easeInQuart() {
        testEasing(Easing.easeInQuart,
            0.0f, 0.0018934713f, 0.0042778444f, 0.0072527467f, 0.0109445155f, 0.015515988f, 0.021181006f, 0.028226627f,
            0.037048362f, 0.04820804f, 0.0625345f, 0.08130686f, 0.106610574f, 0.14206953f, 0.1943854f, 0.27589238f,
            0.40184304f, 0.5630533f, 0.72419477f, 0.8696827f, 0.99999994f)
    }

    @Test
    fun easeInQuint() {
        testEasing(Easing.easeInQuint,
            0.0f, 0.0033269299f, 0.006733535f, 0.010306559f, 0.014156318f, 0.018424226f, 0.023293259f, 0.029002843f,
            0.035870403f, 0.044323303f, 0.054947402f, 0.06856298f, 0.08634728f, 0.11003886f, 0.14229068f, 0.18729751f,
            0.251919f, 0.34755266f, 0.49219683f, 0.7075576f, 1.0f)
    }

    @Test
    fun easeInSine() {
        testEasing(Easing.easeInSine, 0.0f, 0.0027254026f, 0.011011817f, 0.025017451f, 0.044886466f, 0.07074179f,
            0.102675855f, 0.14073862f, 0.18492274f, 0.23514426f, 0.29122016f, 0.35283974f, 0.41953295f, 0.49063367f,
            0.5652415f, 0.64218605f, 0.7199968f, 0.7968903f, 0.87078065f, 0.9393238f, 1.0f)
    }

    @Test
    fun easeOutBack() {
        testEasing(Easing.easeOutBack, 0.0f, 0.24072547f, 0.4482615f, 0.61631316f, 0.74706525f, 0.8466805f, 0.921687f,
            0.9775589f, 1.0185162f, 1.0477248f, 1.0675527f, 1.0797856f, 1.0857869f, 1.0866119f, 1.0830883f, 1.0758748f,
            1.0655018f, 1.0524012f, 1.0369283f, 1.0193787f, 1.0f)
    }

    @Test
    fun easeOutBounce() {
        testEasing(Easing.easeOutBounce,
            0.0f, 0.01890625f, 0.075625f, 0.17015627f, 0.3025f, 0.47265625f, 0.6806251f, 0.9264062f, 0.91f, 0.8189063f,
            0.765625f, 0.7501562f, 0.7725f, 0.8326562f, 0.93062496f, 0.97265625f, 0.94f, 0.9451563f, 0.98812497f,
            0.9845312f, 1.0f)
    }

    @Test
    fun easeOutCirc() {
        testEasing(Easing.easeOutCirc,
            0.0f, 0.40465558f, 0.60414314f, 0.7173273f, 0.7903913f, 0.84133416f, 0.8785943f, 0.9067094f, 0.9283671f,
            0.9452768f, 0.95858544f, 0.9690965f, 0.977391f, 0.9839022f, 0.98895955f, 0.992819f, 0.9956828f, 0.99771285f,
            0.9990404f, 0.999773f, 1.0f)
    }

    @Test
    fun easeOutCubic() {
        testEasing(Easing.easeOutCubic,
            0.0f, 0.14082046f, 0.2756869f, 0.39916474f, 0.50770694f, 0.6003001f, 0.67784834f, 0.74217635f, 0.7953028f,
            0.8390835f, 0.87509406f, 0.9046259f, 0.92872167f, 0.9482221f, 0.9638056f, 0.97602403f, 0.9853291f,
            0.99209315f, 0.99662656f, 0.999189f, 1.0f)
    }

    @Test
    fun easeOutElastic() {
        testEasing(Easing.easeOutElastic,
            0.0f, 0.6464662f, 1.2499862f, 1.3535534f, 1.1250138f, 0.91161656f, 0.875f, 0.95580095f, 1.0312482f,
            1.0441942f, 1.0156268f, 0.98895204f, 0.984375f, 0.9944751f, 1.003906f, 1.0055243f, 1.0019534f, 0.998619f,
            0.9980469f, 0.99930936f, 1.0f)
    }

    @Test
    fun easeOutExpo() {
        testEasing(Easing.easeOutExpo,
            0.0f, 0.25586045f, 0.4797546f, 0.6503364f, 0.76695496f, 0.8435143f, 0.8939551f, 0.9277743f, 0.9508408f,
            0.9667651f, 0.97782457f, 0.9855016f, 0.9907915f, 0.9943815f, 0.9967576f, 0.9982714f, 0.9991806f, 0.9996787f,
            0.99991125f, 0.9999896f, 1.0f)
    }

    @Test
    fun easeOutQuad() {
        testEasing(Easing.easeOutQuad,
            0.0f, 0.09316665f, 0.1872482f, 0.28000116f, 0.36929062f, 0.45337623f, 0.53106797f, 0.60173196f, 0.6651945f,
            0.7216075f, 0.77132356f, 0.8147995f, 0.8525306f, 0.8850101f, 0.91270715f, 0.93605477f, 0.9554469f,
            0.9712375f, 0.98374337f, 0.9932469f, 1.0f)
    }

    @Test
    fun easeOutQuart() {
        testEasing(Easing.easeOutQuart,
            0.0f, 0.22084956f, 0.38816637f, 0.51724327f, 0.6182747f, 0.69824296f, 0.7620636f, 0.8132872f, 0.85453707f,
            0.88779145f, 0.91456926f, 0.93605477f, 0.9531845f, 0.96670866f, 0.9772339f, 0.9852565f, 0.9911858f,
            0.99536157f, 0.9980688f, 0.9995471f, 1.0f)
    }

    @Test
    fun easeOutQuint() {
        testEasing(Easing.easeOutQuint,
            0.0f, 0.20976917f, 0.3981241f, 0.5567806f, 0.6818686f, 0.7753817f, 0.84301066f, 0.891132f, 0.9251515f,
            0.9491327f, 0.9659826f, 0.97774893f, 0.9858767f, 0.9913942f, 0.99504256f, 0.99736226f, 0.99875265f,
            0.9995119f, 0.9998654f, 0.9999843f, 1.0f)
    }

    @Test
    fun easeOutSine() {
        testEasing(Easing.easeOutSine,
            0.0f, 0.07459539f, 0.1507564f, 0.22809653f, 0.30606934f, 0.3839459f, 0.46081012f, 0.5355835f, 0.60709006f,
            0.6741577f, 0.73573935f, 0.79102266f, 0.8394974f, 0.8809671f, 0.91551167f, 0.9434212f, 0.9651227f,
            0.98111546f, 0.9919223f, 0.9980562f, 1.0f)
    }

    @Test
    fun easeInOutLinear() {
        testEasing(Easing.easeInOutLinear(1f, 2f, 1f),
            0.0f, 0.2f, 0.4f, 0.6f, 0.8f,
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            0.79999995f, 0.5999999f, 0.4000001f, 0.20000005f, 0.0f)
    }

    @Test
    fun easeInOut() {
        testEasing(Easing.easeInOut(1f, 2f, 1f, Easing.easeInQuad, Easing.easeInQuad),
            0.0f, 0.052778672f, 0.16614068f, 0.37347472f, 0.6805302f,
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            0.9472213f, 0.83385926f, 0.62652534f, 0.31946987f, 0.0f)
    }

    @Test
    fun compoundEasing() {
        testEasing(
            Easing.compound(0f)
                .add(1f, Easing.easeInQuad, 1f)
                .hold(2f)
                .add(1f, Easing.easeInQuad, 0f)
                .build(),
            0.0f, 0.052778672f, 0.16614068f, 0.37347472f, 0.6805302f,
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            0.9472213f, 0.83385926f, 0.62652534f, 0.31946987f, 0.0f)
    }

}
