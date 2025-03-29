import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails

object LibLibAttributes {
    object Target {
        val attribute: Attribute<String> = Attribute.of("com.teamwizardry.librarianlib.target", String::class.java)

        const val public: String = "public"
        const val internal: String = "internal"
    }
}