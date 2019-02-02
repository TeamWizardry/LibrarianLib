package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextTestLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.text.Fonts
import games.thecodewarrior.bitfont.typesetting.Attribute
import games.thecodewarrior.bitfont.typesetting.AttributedString
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestMCTiny : GuiBase() {
    init {
        main.size = vec(400, 400)

        val background = ColorLayer(Color.WHITE, 0, 0, 400, 400)
        main.add(background)

        val textLayer = TextTestLayer(10, 10, 380, 380)
        val str = AttributedString("""
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec ornare purus ut rhoncus porttitor. \
Phasellus vitae risus ac est suscipit condimentum et a sem. Proin tortor sapien, consectetur in volutpat ac, imperdiet a sapien. \
営意都携学成朝俊権発画丘飛品委億輸面際。青追原高文載付万時玉生描院。会表社際覧以競整暴面態説時名。済供誌浩全戦二楽充戦投強部。態相更異過電転都声田皮一治止金圧周手介。 試携生旬必行創栄自提斉載更著。亡別色洋条丈顔入各記校琴文次。始害観碁紙話末日片交王定飾手。正害東現資必影丈掲手主光静本比大亨治。索前前存日風育学戦詳診月。 \
Лорем ипсум долор сит амет, нам ан антиопам ехпетенда репрехендунт, ет солум убияуе перципит еум. Цонституам детерруиссет ат сед, цу дебет утинам сит. Аццусам аццусата нам ех, еу еам фалли чоро фиерент. Ид сит модус яуаеяуе саперет. Яуо пробо ипсум перпетуа ан. \
Λορεμ ιπσθμ δολορ σιτ αμετ, ει vιδε cηορο δελεcτθσ εθμ. Θτ μθνδι ερρεμ vενιαμ ναμ, ιδ vιξ δελενιτ θλλαμcορπερ, απειριαν ταcιματεσ θτ εστ. \
Νεc τε νοστρο οφφενδιτ σθαvιτατε. Αδ λιβρισ εξπετενδισ μει. Ατ σcριπσεριτ cοτιδιεqθε cοντεντιονεσ εοσ, ει ηισ cονσθλ θταμθρ. \
Αφφερτ φαcιλισισ εστ ιν. Αθτεμ ερρορ μεα ιν. Ιθσ ποσσιτ αδιπισcι ρεcτεqθε αν, νε νιβη νομινατι cοντεντιονεσ περ. Αδ δθο δισcερε δολορεμ cομμθνε. \
Ινανι cομμοδο πηαεδρθμ ιθσ αδ. Νε μει δελιcατα τορqθατοσ. Εξ μεα qθισ ελιτ νομιναvι. Ταλε vερι μθcιθσ εοσ ιν, ιν ιθστο διcιτ cομπρεηενσαμ εαμ.
        """.trimIndent().replace("\\\n", ""))
        str.setAttributesForRange(0 until str.string.length, mapOf(
            Attribute.font to Fonts.Unifont
        ))
        val regex = """legib\w+""".toRegex()
        regex.findAll(str.string).forEach { match ->
            str.setAttributesForRange(match.range, mapOf(
                Attribute.color to Color.RED
            ))
        }
        textLayer.text = str
        textLayer.wrap = 380
        /*
┍━━━━━━━━━━━━┑
│ monospace! │
╞════════════╡
│  ⎡0⋯0⎤     │
│  ⎢⋮⋱⋮⎥     │
│  ⎣0⋯0⎦     │
│            │
└────────────┘

⎛a⎞
⎜b⎟
⎝c⎠

⎡x⎤
⎢y⎥
⎢z⎥
⎣w⎦

⎧√-x , x<0     ⎫
⎪π²  , x=0     ⎮
⎨π²x , 0<x≤10  ⎬
⎪2πx², 10<x≤100⎮
⎩-πx , 100<x   ⎭
√π
≺≻≼≽
>≤ ≥ ≰ ≱ ⪇ ⪈ ≦ ≧ ≨ ≩
         */
        main.add(textLayer)

        val uniTextLayer = TextLayer(310, 10, 280, 580)
        uniTextLayer.unicode = true
        uniTextLayer.text = """
So what makes a typeface legible? A long-standing typographic maxim is that the most legible typefaces are \
“transparent” to the reader–that is, they don’t call undue attention to themselves. Additionally, the most legible \
typefaces contain big features and have restrained design characteristics. While this may seem like a typographic \
oxymoron, it’s not. “Big features” refers to things such as large, open counters, ample lowercase x-heights, and \
character shapes that are obvious and easy to recognize. The most legible typefaces are also restrained. They are not \
excessively light or bold, weight changes within character strokes are subtle, and serifs, if the face has them, do \
not call attention to themselves.

Counters, the white space within letters such as ‘o,’ ‘e,’ ‘c,’ etc., help to define a character. Typographers believe \
that large counters are an aid to character recognition. A byproduct of open counters is usually a large lowercase \
x-height. As long as the x-height is not excessively large, this can also improve legibility in a typeface. Because \
over 95% of the letters we read are lowercase, larger letter proportions usually result in a more legible typeface.

While virtually any serif typeface can benefit from large open counters, “Clarendons” like Nimrod or Scherzo and \
contemporary interpretations of “Old Style" designs, such as Monotype Bembo and ITC Weidemann, tend to come by this \
trait most naturally.

Individual letter shapes can also affect typeface legibility. For example: the two-story ‘a’ such as the one found in \
Stellar or Exlibris is much more legible than the single-story ‘a’ found in Futura or Erbar. The lowercase ‘g’ based \
on Roman letter shapes is more legible then the simple ‘g’ found in Helvetica or Glypha. In Old Style typefaces such \
as Monotype Plantin, Galena and ITC Berkeley Oldstyle, individual characters have more personality than those in \
traditional “legibility” faces with virtually no loss in character legibility.
        """.trimIndent().replace("\\\n", " ")
//        main.add(uniTextLayer)

        /*

┍━━━━━━━━━━━━┑
│ monospace! │
╞════════════╡
│  ⎡0⋯0⎤      │
│  ⎢⋮⋱⋮⎥      │
│  ⎣0⋯0⎦      │
│            │
└────────────┘
⋮⋯⋰⋱
aaaa
⎛
⎜
⎝
⎞
⎟
⎠
⎡
⎢
⎣
⎤
⎥
⎦
⎧
⎨
⎪
⎩
⎫
⎬
⎮
⎭

┌ ┍ ┎ ┏ ┐ ┑ ┒ ┓

└ ┕ ┖ ┗ ┘ ┙ ┚ ┛

├ ┝ ┞ ┟ ┠ ┡ ┢ ┣

┤ ┥ ┦ ┧ ┨ ┩ ┪ ┫

┬ ┭ ┮ ┯ ┰ ┱ ┲ ┳

┴ ┵ ┶ ┷ ┸ ┹ ┺ ┻

┼ ┽ ┾ ┿ ╀ ╁ ╂ ╃

╄ ╅ ╆ ╇ ╈ ╉ ╊ ╋

─ ━ │ ┃ ┄ ┅ ┆ ┇ ┈ ┉ ┊ ┋
╌ ╍ ╎ ╏ ═ ║

╒ ╓ ╔ ╕ ╖ ╗

╘ ╙ ╚ ╛ ╜ ╝

╞ ╟ ╠ ╡ ╢ ╣

╤ ╥ ╦

╧ ╨ ╩

╪ ╫ ╬

¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿
ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽž
ſƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃǄǅǆǇǈǉǊǋǌǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳ
ȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏɐɑɒɓɔɕɖɗɘəɚɛɜɝɞɟɠɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿʀʁʂʃʄʅʆʇʈʉʊʋʌʍʎʏʐʑʒʓʔʕʖʗʘʙʚʛʜʝʞʟʠʡʢʣʤʥʦʧʨʩʪʫʬʭʮʯ
ʰʱʲʳʴʵʶʷʸʹʺʻʼʽʾʿˀˁ˂˃˄˅ˆˇˈˉˊˋˌˍˎˏːˑ˒˓˔˕˖˗˘˙˚˛˜˝˞˟ˠˡˢˣˤ˥˦˧˨˩˪˫ˬ˭ˮ˯˰˱˲˳˴˵˶˷˸˹˺˻˼˽˾˿̀́̂̃̄̅̆̇̈̉̊̋̌̍̎̏̐̑̒ͰͱͲͳʹ͵Ͷͷ͸͹ͺͻͼͽ;Ϳ΀΁΂΃΄΅Ά·ΈΉΊ΋Ό΍ΎΏΐΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡ΢ΣΤΥΦΧΨΩΪΫάέήίΰαβγδεζηθικλμνξοπρςστυφχψωϊϋόύώϏϐϑϒϓϔϕϖϗϘϙϚϛϜϝϞϟϠϡϢϣϤϥϦϧϨϩϪϫϬϭϮϯϰϱϲϳϴϵ϶ϷϸϹϺϻϼϽϾϿЀЁЂЃЄЅІЇЈЉЊЋЌЍЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяѐёђѓєѕіїјљњћќѝўџѠѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿҀҁ҂҃҄҅҆҇҈҉ҊҋҌҍҎҏҐґҒғҔҕҖҗҘҙҚқҜҝҞҟҠҡҢңҤҥҦҧҨҩҪҫҬҭҮүҰұҲҳҴҵҶҷҸҹҺһҼҽӀӁӂӃӄӅӆӇӈӉӊӋӌӍӎӏӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӶӷӸӹӺӻӼӽӾӿ
ᴀᴁᴂᴃᴄᴅᴆᴇᴈᴉᴊᴋᴌᴍᴎᴏᴐᴑᴒᴓᴔᴕᴖᴗᴘᴙᴚᴛᴜᴝᴞᴟᴠᴡᴢᴣᴤᴥᴦᴧᴨᴩᴪᴫᴬᴭᴮᴯᴰᴱᴲᴳᴴᴵᴶᴷᴸᴹᴺᴻᴼᴽᴾᴿᵀᵁᵂᵃᵄᵅᵆᵇᵈᵉᵊᵋᵌᵍᵎᵐᵑᵒᵓᵔᵕᵖᵗᵘᵙᵚᵛᵜᵝᵞᵟᵠᵡᵢᵣᵤᵥᵦᵧᵨᵩᵪᵫ‐‑‒–—―‖‗‘’‚‛“”„‟†‡•‣․‥…‧‰‱′″‴‵‶‷‸‹›※‼‽‾‿⁀⁁⁂⁃⁄⁅⁆⁇⁈⁉⁊⁋⁌⁍⁎⁏⁐⁑⁒⁓⁔⁕⁖⁗⁘⁙⁚⁛⁜⁝⁞⁰ⁱ⁲⁳⁴⁵⁶⁷⁸⁹⁺⁻⁼⁽⁾ⁿ₀₁₂₃₄₅₆₇₈₉₊₋₌ₐₑₒₓₔₕₖₗₘₙₚₛₜ₠₡₢₣₤₥₦₧₨₩₪₫€₭₮₯₰₱₲₳₴₵₶₷₸₹₺₻₼₽₾₿⃒⃓⃘⃙⃚⃐⃑⃔⃕⃖⃗⃛⃜⃝⃞⃟⃠⃡⃢℀℁ℂ℃℄℅℆ℇ℈℉ℊℋℌℍℎℏℐℑℒℓ℔ℕ№℗℘ℙℚℛℜℝ℞℟℠℡™℣ℤ℥Ω℧ℨ℩KÅℬℭ℮ℯℰℱℲℳℴℵℶℷℸℹ℺℻ℼℽℾℿ⅀⅁⅂⅃⅄ⅅⅆⅇⅈⅉ⅊⅋⅌⅍ⅎ⅏⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞⅟ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫⅬⅭⅮⅯⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅺⅻⅼⅽⅾⅿↀↁↂↃↄↅↆↇↈ↉↊↋
←↑→↓↔↕↖↗↘↙↚↛↜↝↞↟↠↡↢↣↤↥↦↧↨↩↪↫↬↭↮↯↰↱↲↳↴↵↶↷↸↹↺↻↼↽↾↿⇀⇁⇂⇃⇄⇅⇆⇇⇈⇉⇊⇋⇌⇍⇎⇏⇐⇑⇒⇓⇔⇕⇖⇗⇘⇙⇚⇛⇜⇝⇞⇟⇠⇡⇢⇣⇤⇥⇦⇧⇨⇩⇪⇫⇬⇭⇮⇯⇰⇱⇲⇳⇴⇵⇶⇷⇸⇹⇺⇻⇼⇽⇾⇿
∀∁∂∃∄∅∆∇∈∉∊∋∌∍∎∏∐∑−∓∔∕∖∗∘∙√∛∜∝∞∟∠∡∢∣∤∥∦∧∨∩∪∫∬∭∮∯∰∱∲∳∴∵∶∷∸∹∺∻∼∽∾∿≀≁
≂≃≄≅≆≇≈≉≊≋≌≍≎≏≐≑≒≓≔≕≖≗≘≙≚≛≜≝≞≟≠≡≢≣≤≥≦≧≨≩≪≫≬≭≮≯≰≱≲≳≴≵≶≷≸≹≺≻≼≽≾≿⊀⊁⊂⊃⊄⊅⊆⊇⊈⊉⊊⊋⊌⊍⊎⊏⊐⊑⊒⊓⊔
⊕⊖⊗⊘⊙⊚⊛⊜⊝⊞⊟⊠⊡⊢⊣⊤⊥⊦⊧⊨⊩⊪⊫⊬⊭⊮⊯⊰⊱⊲⊳⊴⊵⊶⊷⊸⊹⊺⊻⊼⊽⊾⊿⋀⋁⋂⋃⋄⋅⋆⋇⋈⋉⋊⋋⋌⋍⋎⋏⋐⋑⋒⋓⋔⋕⋖⋗⋘⋙⋚⋛⋜⋝⋞⋟⋠⋡⋢⋣⋤⋥⋦⋧⋨⋩⋪⋫⋬⋭
⋮⋯⋰⋱⋲⋳⋴⋵⋶⋷⋸⋹⋺⋻⋼⋽⋾⋿⌀⌁⌂⌃⌄⌅⌆⌇⌈⌉⌊⌋⌌⌍⌎⌏⌐⌑⌒⌓⌔⌕⌖⌗⌘⌙⌚⌛⌜⌝⌞⌟
⌠⌡⌢⌣⌤⌥⌦⌧⌨〈〉⌫⌬⌭⌮⌯⌰⌱⌲⌳⌴⌵⌶⌷⌸⌹⌺⌻⌼⌽⌾⌿⍀⍁⍂⍃⍄⍅⍆⍇⍈⍉⍊⍋⍌⍍⍎⍏⍐⍑⍒⍓⍔⍕⍖⍗⍘⍙⍚⍛⍜⍝⍞⍟⍠⍡⍢⍣⍤⍥⍦⍧⍨⍩⍪⍫⍬⍭⍮⍯⍰⍱⍲⍳⍴⍵⍶⍷⍸⍹⍺⍻⍼⍽⍾⍿⎀⎁⎂⎃⎄⎅⎆⎇⎈⎉⎊⎋⎌⎍⎎⎏⎐⎑⎒⎓⎔⎕⎖⎗⎘⎙⎚
⎛
⎜
⎝
⎞
⎟
⎠
⎡
⎢
⎣
⎤
⎥
⎦
⎧
⎨
⎪
⎩
⎫
⎬
⎮
⎭
⎯
⎰
⎱
⎲
⎳
⎴
⎵
⎶
⎷
⎸
⎹
⎺
⎻
⎼
⎽

⎾⎿⏀⏁⏂⏃⏄⏅⏆⏇⏈⏉⏊⏋⏌⏍⏎⏏⏐⏑⏒⏓⏔⏕⏖⏗⏘⏙⏚⏛⏜⏝⏞⏟⏠⏡⏢⏣⏤⏥⏦⏧⏨⏩⏪⏫⏬⏭⏮⏯⏰⏱⏲⏳⏴⏵⏶⏷⏸⏹⏺⏻⏼⏽⏾⏿
─ ━ │ ┃ ┄ ┅ ┆ ┇ ┈ ┉ ┊ ┋ ┌ ┍ ┎ ┏ ┐ ┑ ┒ ┓ └ ┕ ┖ ┗ ┘ ┙ ┚ ┛ ├ ┝ ┞ ┟ ┠ ┡ ┢ ┣ ┤ ┥ ┦ ┧ ┨ ┩ ┪ ┫ ┬ ┭ ┮ ┯ ┰ ┱ ┲ ┳ ┴ ┵ ┶ ┷ ┸ ┹ ┺ ┻ ┼ ┽ ┾ ┿ ╀ ╁ ╂ ╃ ╄ ╅ ╆ ╇ ╈ ╉ ╊ ╋ ╌ ╍ ╎ ╏ ═ ║ ╒ ╓ ╔ ╕ ╖ ╗ ╘ ╙ ╚ ╛ ╜ ╝ ╞ ╟ ╠ ╡ ╢ ╣ ╤ ╥ ╦ ╧ ╨ ╩ ╪ ╫ ╬
╭ ╮ ╯ ╰ ╱ ╲ ╳ ╴ ╵ ╶ ╷ ╸ ╹ ╺ ╻ ╼ ╽ ╾ ╿
▀ ▁ ▂ ▃ ▄ ▅ ▆ ▇ █ ▉ ▊ ▋ ▌ ▍ ▎ ▏ ▐ ░ ▒ ▓ ▔ ▕ ▖ ▗ ▘ ▙ ▚ ▛ ▜ ▝ ▞ ▟
■ □ ▢ ▣ ▤ ▥ ▦ ▧ ▨ ▩ ▪ ▫ ▬ ▭ ▮ ▯ ▰ ▱
▲ △ ▴ ▵ ▶ ▷ ▸ ▹ ► ▻ ▼ ▽ ▾ ▿ ◀ ◁ ◂ ◃ ◄ ◅ ◆ ◇ ◈ ◉ ◊ ○ ◌ ◍
◎ ● ◐ ◑ ◒ ◓ ◔ ◕ ◖ ◗ ◘ ◙ ◚ ◛ ◜ ◝ ◞ ◟ ◠ ◡ ◢ ◣ ◤ ◥ ◦ ◧ ◨ ◩ ◪ ◫ ◬ ◭ ◮ ◯ ◰ ◱ ◲ ◳ ◴ ◵ ◶ ◷
◸◹◺◻◼◽◾◿
⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⡀
⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎⡏⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛⡜⡝⡞⡟⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿⢀
⢁⢂⢃⢄⢅⢆⢇⢈⢉⢊⢋⢌⢍⢎⢏⢐⢑⢒⢓⢔⢕⢖⢗⢘⢙⢚⢛⢜⢝⢞⢟⢠⢡⢢⢣⢤⢥⢦⢧⢨⢩⢪⢫⢬⢭⢮⢯⢰⢱⢲⢳⢴⢵⢶⢷⢸⢹⢺⢻⢼⢽⢾⢿⣀
⣁⣂⣃⣄⣅⣆⣇⣈⣉⣊⣋⣌⣍⣎⣏⣐⣑⣒⣓⣔⣕⣖⣗⣘⣙⣚⣛⣜⣝⣞⣟⣠⣡⣢⣣⣤⣥⣦⣧⣨⣩⣪⣫⣬⣭⣮⣯⣰⣱⣲⣳⣴⣵⣶⣷⣸⣹⣺⣻⣼⣽⣾⣿

         */
    }
}
