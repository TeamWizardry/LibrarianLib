configurations {
    configureEach {
        attributes.attribute(
            LibLibAttributes.Target.attribute,
            if(this.name.startsWith("published"))
                LibLibAttributes.Target.public
            else
                LibLibAttributes.Target.internal
        )
    }
}
