import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails

object LibLibAttributes {
    object Target {
        val attribute: Attribute<String> = Attribute.of("com.teamwizardry.librarianlib.target", String::class.java)

        const val public: String = "public"
        const val internal: String = "internal"

    }

    object Rules {
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> optional(): Class<AttributeCompatibilityRule<T>> =
            OptionalRule::class.java as Class<AttributeCompatibilityRule<T>>
    }

    private open class OptionalRule: AttributeCompatibilityRule<Any> {
        override fun execute(details: CompatibilityCheckDetails<Any>) {
            details.run {
                if (producerValue == null) {
                    compatible()
                }
            }
        }
    }
}